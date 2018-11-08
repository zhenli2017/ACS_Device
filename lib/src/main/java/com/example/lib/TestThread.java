package com.example.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Time:2018/9/21
 * User:lizhen
 * Description:
 */

public class TestThread extends Thread {

    private ArrayBlockingQueue<String> mQueue;

    public TestThread(ArrayBlockingQueue<String> queue) {
        mQueue = queue;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                String take = mQueue.take();
                try {
                    InputStream inputStream = new FileInputStream(new File("D:\\file\\a.txt"));
                    inputStream.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(take);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException "+e.getMessage());
            }
        }

    }
}
