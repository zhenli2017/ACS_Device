package com.thdtek.acs.terminal.yzface.Message;

public class DataPersonDBInfo {
    private byte[] maxCapacity               = new byte[4];
    private byte[] currentCount             = new byte[4];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.maxCapacity.length;
        iLength += this.currentCount.length;
        return  iLength;
    }
    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //139

        //组装其他部件
        System.arraycopy(this.maxCapacity, 0, data, index, maxCapacity.length);
        index += maxCapacity.length;

        System.arraycopy(this.currentCount, 0, data, index, currentCount.length);
        index += currentCount.length;

        return  data;
    }
    public DataPersonDBInfo() {

    }

    public DataPersonDBInfo(byte[] data) {
        int index =0;
        if(data.length != 8){
            throw new IllegalArgumentException("data.length != 8,DataPersonDBInfo length must ==8 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.maxCapacity,0,this.maxCapacity.length );
        index += this.maxCapacity.length;

        System.arraycopy(data,index, this.currentCount,0,this.currentCount.length );
        index += this.currentCount.length;


    }

    public byte[] getMaxCapacity() {
        return maxCapacity;
    }

    public byte[] getCurrentCount() {
        return currentCount;
    }

    public void setMaxCapacity(byte[] maxCapacity) {
        if(maxCapacity.length != 4){
            throw new IllegalArgumentException("maxCapacity.length != 4  !!!");
        }
        this.maxCapacity = maxCapacity;
    }

    public void setCurrentCount(byte[] currentCount) {
        if(currentCount.length != 4){
            throw new IllegalArgumentException("currentCount.length != 4  !!!");
        }
        this.currentCount = currentCount;
    }
}
