package com.thdtek.acs.terminal.yzface.Message;

public class MessageHead {
    private byte[] category = new byte[1];
    private byte[] command = new byte[1];
    private byte[] parameter = new byte[1];
    private byte[] dataLength = new byte[4];
}
