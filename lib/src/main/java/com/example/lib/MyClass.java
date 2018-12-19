package com.example.lib;


import com.example.lib.interfaceTest.ByteUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyClass {
    public static void main(String[] args) {

        System.out.println(Integer.parseInt("0001869F",16));
    }
    public static String add0(String msg) {

        if (msg.length() >= 30) {
            return msg;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 60 - msg.length(); i++) {
            stringBuilder.append("0");
        }
        return msg + stringBuilder.toString();
    }
    public static void udp() throws IOException {
        // 创建一个数据报套接字，并将其绑定到指定port上
        DatagramSocket datagramSocket = new DatagramSocket(6000);
        datagramSocket.setSoTimeout(5000);
        // DatagramPacket(byte buf[], int length),建立一个字节数组来接收UDP包

        byte[] buf = "THD".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName("192.168.0.255"), 15001);
        // 发送消息
        datagramSocket.send(sendPacket);
        byte[] bytes = new byte[1024];


        DatagramPacket receivePacket = new DatagramPacket(bytes, bytes.length);
        // receive()来等待接收UDP数据报
        datagramSocket.receive(receivePacket);
        String string = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println(string + " ip = " + receivePacket.getAddress() + " port " + receivePacket.getPort());
    }

}
