package com.thdtek.acs.terminal.yzface.Message;

public class DataSN {
    byte[] prefix = new byte[5];
    byte[] sn = new byte[16];
    byte[] suffix = new byte[3];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.prefix.length;
        iLength += this.sn.length;
        iLength += this.suffix.length;
        return  iLength;
    }
    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //139

        //组装其他部件
        System.arraycopy(this.prefix, 0, data, index, prefix.length);
        index += prefix.length;

        System.arraycopy(this.sn, 0, data, index, sn.length);
        index += sn.length;

        System.arraycopy(this.suffix, 0, data, index, suffix.length);
        index += suffix.length;

        return  data;
    }
    public DataSN() {
    }
    public DataSN(byte[] data) {

        int index =0;
        if(data.length != 24){
            throw new IllegalArgumentException("data.length != 24,DataSN length must == 24 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.prefix,0,this.prefix.length );
        index += this.prefix.length;

        System.arraycopy(data,index, this.sn,0,this.sn.length );
        index += this.sn.length;

        System.arraycopy(data,index, this.suffix,0,this.suffix.length );
        index += this.suffix.length;
    }

    public void setSn(byte[] sn) {

        if(sn.length != 16){
            throw new IllegalArgumentException("sn.length != 16  !!!");
        }

        this.sn = sn;
    }

    public byte[] getPrefix() {
        return prefix;
    }

    public byte[] getSuffix() {
        return suffix;
    }

    public byte[] getSn() {
        return sn;
    }

    public void setPrefix(byte[] prefix) {
        if(prefix.length != 5){
            throw new IllegalArgumentException("prefix.length != 5  !!!");
        }
        this.prefix = prefix;
    }

    public void setSuffix(byte[] suffix) {
        if(suffix.length != 3){
            throw new IllegalArgumentException("suffix.length != 3  !!!");
        }
        this.suffix = suffix;
    }
}
