package com.thdtek.acs.terminal.yzface.Message;

public class DataRecordInfoCount {
    private  byte[] recordCapacity = new byte[4];
    private byte[] newRecordInfos  = new byte[4];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.recordCapacity.length;
        iLength +=  this.newRecordInfos.length;
        return iLength;
    }

    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //8

        System.arraycopy(this.recordCapacity, 0, data, index, recordCapacity.length);
        index += recordCapacity.length;

        System.arraycopy(this.newRecordInfos, 0, data, index, newRecordInfos.length);
        index += newRecordInfos.length;

        return  data;
    }

    public DataRecordInfoCount() {
    }

    public DataRecordInfoCount(byte[] data) {
        int index =0;
        if(data.length != 8){
            throw new IllegalArgumentException("data.length != 8,DataRecordInfoCount length must == 8 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.recordCapacity,0,this.recordCapacity.length );
        index += this.recordCapacity.length;

        System.arraycopy(data,index, this.newRecordInfos,0,this.newRecordInfos.length );
        index += this.newRecordInfos.length;
    }

    public byte[] getRecordCapacity() {
        return recordCapacity;
    }

    public void setRecordCapacity(byte[] recordCapacity) {
        if(recordCapacity.length != 4){
            throw new IllegalArgumentException("recordCapacity.length != 4  !!!");
        }
        this.recordCapacity = recordCapacity;
    }

    public byte[] getNewRecordInfos() {
        return newRecordInfos;
    }

    public void setNewRecordInfos(byte[] newRecordInfos) {
        if(newRecordInfos.length != 4){
            throw new IllegalArgumentException("newRecordInfos.length != 4  !!!");
        }
        this.newRecordInfos = newRecordInfos;
    }
}
