package com.example.lib;

import java.util.concurrent.CountDownLatch;

import jdk.internal.vm.annotation.Contended;

public class 缓存行测试 {
    public static long COUNT = 1000000000L;

    @Contended
    private static class T {
//        public long p1, p2, p3, p4, p5, p6, p7;
        public long x = 0L;
//        public long p8, p9, p10, p11, p12, p13, p14;
    }

    public static T[] arr = new T[2];

    static {
        arr[0] = new T();
        arr[1] = new T();
    }

    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i = 0; i<COUNT; i++){
                    arr[0].x = i;
                }
                latch.countDown();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i = 0; i<COUNT; i++){
                    arr[1].x = i;
                }
                latch.countDown();
            }
        });

//        final long start = System.nanoTime();
        final long start = System.currentTimeMillis();
        t1.start();
        t2.start();
        latch.await();
//        System.out.println("程序运行时间: " + (System.nanoTime() -start)/1000000);
        System.out.println("程序运行时间: " + (System.currentTimeMillis() -start));


    }
}