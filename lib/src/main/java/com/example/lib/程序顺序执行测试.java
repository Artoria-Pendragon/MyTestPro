package com.example.lib;

import java.util.concurrent.CountDownLatch;

/*
https://www.cnblogs.com/karbon/p/14596740.html
程序执行乱序
java程序在执行的时候并不一定是按照顺序执行的，多条语句可能是先执行第五条再执行第一条之类的情况，
但是他会遵守一个原则是单线程的数据最终一致性，也就是说在单线程情况下是不会有问题的。

乱序执行有什么好处呢？
好处就是程序整体的执行效率提高了，假设两个线程的情况，理论上A语句先执行，B语句后执行CPU先执行A语句，
但是A语句需要加载一些数据到缓存，而缓存的执行速度大概只有CPU寄存器的1%，如果等待这个过程可能要耗费100ns
（基本上是纳秒级，不是毫秒），但是线程B不需要加载数据到缓存，可直接由cpu计算，整个过程只需要1ns，
那么如果执着于按照先A后B的顺序，cpu就会等待缓存的加载然后执行，才会到B语句执行，总共耗时101ns，
但如果乱序执行的话，在等待A的过程中，先执行B，那么总共的耗时就是100ns。提升了效率。
执行顺序也是有原则可寻的，java 有8大happens-before

单线程happens-before原则：在同一个线程中，书写在前面的操作happens-before后面的操作。
锁的happens-before原则：同一个锁的unlock操作happens-before此锁的lock操作。
volatile的happens-before原则：对一个volatile变量的写操作happens-before对此变量的任意操作(当然也包括写操作了)。
happens-before的传递性原则：如果A操作 happens-before B操作，B操作happens-before C操作，那么A操作happens-before C操作。
线程启动的happens-before原则：同一个线程的start方法happens-before此线程的其它方法。
线程中断的happens-before原则：对线程interrupt方法的调用happens-before被中断线程的检测到中断发送的代码。
线程终结的happens-before原则：线程中的所有操作都happens-before线程的终止检测。
对象创建的happens-before原则：一个对象的初始化完成先于他的finalize方法调用。
 */
public class 程序顺序执行测试 {
    public static int a = 0, b = 0;
    public static int x = 0, y = 0;
    public static void main(String[] args) throws InterruptedException {
        test1();

    }

    public static void test1() throws InterruptedException {
        for (long j = 0;j<Long.MAX_VALUE;j++){
            a = 0; b = 0; x = 0; y = 0;
            final CountDownLatch latch = new CountDownLatch(2);

            Thread t1 =new Thread(new Runnable() {
                @Override
                public void run() {
                    a = 1;
                    x = b;
                    latch.countDown();
                }
            });

            Thread t2 =new Thread(new Runnable() {
                @Override
                public void run() {
                    b = 1;
                    y = a;
                    latch.countDown();
                }
            });
            t1.start();
            t2.start();

            latch.await();
            if(x == 0 && y == 0){
                System.out.println("第"+j+"次运行时，x = 0 y = 0");
                break;
            }
        }
    }
}
