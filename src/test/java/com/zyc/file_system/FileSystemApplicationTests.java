package com.zyc.file_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@SpringBootTest
class FileSystemApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    public void test1(){
        String reg = ".+(.JPEG|.jpeg|.JPG|.jpg)";
        String imgp= "Redocn_2012100818523401\\.jpg";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(imgp);
        System.out.println(matcher.find());
    }

    @Test
    public void test2(){


    }

    public static void main(String[] args) throws InterruptedException {

        ReentrantLock lock = new ReentrantLock(true);
        Thread t1 = new Thread(){
            @Override
            public void run() {
                lock.lock();
                LockSupport.park();
                System.out.println("t1");
                lock.unlock();
            }
        };

        System.out.println("main_1");
        t1.start();
        lock.lock();
        System.out.println("main_2");
        lock.unlock();


    }

}
