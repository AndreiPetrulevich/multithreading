package ru.geekbrains.multithreading;

import java.util.Arrays;

public class Main extends Thread {


    public static void main(String[] args) {
        final int SIZE = 10000000;
        final int HALF_SIZE = SIZE/2;
        myFirstMethod(SIZE);
        myFirstMethod(HALF_SIZE);
        mySecondMethod(SIZE);
        mySecondMethod(HALF_SIZE);
        mySecondMethod(SIZE, 5);
        mySecondMethod(HALF_SIZE, 5);
    }

    public static float[] makeArray(int size) {
        float[] array = new float[size];
        Arrays.fill(array, 1);
        return array;
    }

    public static void myFirstMethod(int size) {
        float[] array = makeArray(size);
        long time = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            array[i] = calculate(i, array[i]);
        }
        System.out.println(System.currentTimeMillis() - time);
    }

    private static float calculate(int index, float element) {
        return (float)(element * Math.sin(0.2f + index / 5) * Math.cos(0.2f + index / 5) * Math.cos(0.4f + index / 2));
    }

    public static void mySecondMethod(int size) {
        float[] array = makeArray(size);
        float[] leftHalfArray = new float[size / 2];
        float[] rightHalfArray = new float[size / 2 + size % 2];
        long time = System.currentTimeMillis();
        System.arraycopy(array, 0, leftHalfArray, 0, leftHalfArray.length);
        System.arraycopy(array, leftHalfArray.length, rightHalfArray, 0, rightHalfArray.length);

        Thread leftHalfThread = new Thread(() -> {
            for (int i = 0; i < leftHalfArray.length; i++) {
                leftHalfArray[i] = calculate(i, leftHalfArray[i]);
            }
        });
        Thread rightHalfThread = new Thread(() -> {
            for (int i = 0; i < rightHalfArray.length; i++) {
                rightHalfArray[i] = calculate(i, rightHalfArray[i]);
            }
        });

        leftHalfThread.start();
        rightHalfThread.start();

        try {
            leftHalfThread.join();
            rightHalfThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.arraycopy(leftHalfArray, 0, array, 0, leftHalfArray.length);
        System.arraycopy(rightHalfArray, 0, array, leftHalfArray.length, rightHalfArray.length);
        System.out.println(System.currentTimeMillis() - time);
    }

    public static void mySecondMethod(int size, int threads) {
        float[] array = makeArray(size);
        int chunkSize = size / threads;
        float[][] chunks = new float[threads][chunkSize];
        Thread[] threadArray = new Thread[threads];
        long time = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            System.arraycopy(array, i * chunkSize, chunks[i], 0, chunkSize);
            float[] chunk = chunks[i];
            threadArray[i] = new Thread(() -> {
                for (int j = 0; j < chunk.length; j++) {
                    chunk[j] = calculate(j, chunk[j]);
                }
            });
            threadArray[i].start();
        }

        try {
            for (int i =0; i < threads; i++) {
                threadArray[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i =0; i < threads; i++) {
            System.arraycopy(chunks[i], 0, array, i * chunkSize, chunkSize);
        }

        System.out.println(System.currentTimeMillis() - time);
    }
}

