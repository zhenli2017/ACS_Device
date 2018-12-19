package com.thdtek.acs.terminal.yzface.Message;

public class DataId {
    private byte[] id = new byte[4];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.id.length;

        return  iLength;
    }
    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //139

        //组装其他部件
        System.arraycopy(this.id, 0, data, index, id.length);
        index += id.length;

        return  data;
    }

    public DataId() {

    }
    public DataId(byte[] data) {
        int index =0;
        if(data.length != 4){
            throw new IllegalArgumentException("data.length != 4,DataId length must == 4 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.id,0,this.id.length );
        index += this.id.length;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        if(id.length != 4){
            throw new IllegalArgumentException("suffix.length != 4  !!!");
        }
        this.id = id;
    }
}
