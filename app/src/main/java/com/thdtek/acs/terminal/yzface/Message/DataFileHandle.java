package com.thdtek.acs.terminal.yzface.Message;

public class DataFileHandle {
    byte[] handle = new byte[4];

    public byte[] toBytes() {

        int index = 0;
        int iLength = getLength();
        byte[] data = new byte[iLength]; //137

        System.arraycopy(this.handle, 0, data, index, handle.length);
        index += handle.length;
        return data;
    }
    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength() {
        int iLength = 0;
        iLength += this.handle.length;

        return  iLength;
    }
    public  DataFileHandle(){

    }
    public DataFileHandle(byte[] handle) {
        this.handle = handle;
    }

    public byte[] getHandle() {
        return handle;
    }

    public void setHandle(byte[] handle) {
        this.handle = handle;
    }
}
