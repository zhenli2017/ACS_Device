package com.thdtek.acs.terminal.yzface.Message;

public class DataCardParameter {

    private   byte[] WGType     = new byte[1];
    private   byte[] WGEnable   = new byte[1];
    private   byte[] WGbitOrder = new byte[1];
    private   byte[] DataType   = new byte[1];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.WGType.length;
        iLength += this.WGEnable.length;
        iLength += this.WGbitOrder.length;
        iLength += this.DataType.length;
        return  iLength;
    }

    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //7

        //组装其他部件
        System.arraycopy(this.WGType, 0, data, index, WGType.length);
        index += WGType.length;

        System.arraycopy(this.WGEnable, 0, data, index, WGEnable.length);
        index += WGEnable.length;

        System.arraycopy(this.WGbitOrder, 0, data, index, WGbitOrder.length);
        index += WGbitOrder.length;

        System.arraycopy(this.DataType, 0, data, index, DataType.length);
        index += DataType.length;

        return  data;
    }
    public DataCardParameter(byte[] data) {
        int index =0;
        if(data.length != 4){
            throw new IllegalArgumentException("data.length != 4,DataCardParameter length must == 4 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.WGType,0,this.WGType.length );
        index += this.WGType.length;

        if( (this.WGType[0] < 1) || (this.WGType[0] > 0x05) ){
            throw new IllegalArgumentException("WGType must >=1 && <=5   !!!");
        }

        System.arraycopy(data,index, this.WGEnable,0,this.WGEnable.length );
        index += this.WGEnable.length;

        if( (this.WGEnable[0] < 0) || (this.WGEnable[0] > 0x2) ){
            throw new IllegalArgumentException("WGEnable must >=0 && <=0x02   !!!");
        }

        System.arraycopy(data,index, this.WGbitOrder,0,this.WGbitOrder.length );
        index += this.WGbitOrder.length;

        if( (this.WGbitOrder[0] < 1) || (this.WGbitOrder[0] > 2) ){
            throw new IllegalArgumentException("WGbitOrder must >=1 && <=0x2   !!!");
        }

        System.arraycopy(data,index, this.DataType,0,this.DataType.length );
        index += this.DataType.length;

        if( (this.DataType[0] < 1) || (this.DataType[0] > 2) ){
            throw new IllegalArgumentException("DataType must >=1 && <=0x2   !!!");
        }
    }

    public DataCardParameter() {

    }

    public void setWGEnable(byte[] WGEnable) {
        if(WGEnable.length != 1){
            throw new IllegalArgumentException("WGEnable.length != 1  !!!");
        }
        this.WGEnable = WGEnable;
    }

    public void setWGType(byte[] WGType) {

        if(WGType.length != 1){
            throw new IllegalArgumentException("WGType.length != 1  !!!");
        }

        this.WGType = WGType;
    }

    public void setWGbitOrder(byte[] WGbitOrder) {

        if(WGbitOrder.length != 1){
            throw new IllegalArgumentException("WGbitOrder.length != 1  !!!");
        }

        this.WGbitOrder = WGbitOrder;
    }

    public void setDataType(byte[] dataType) {

        if(dataType.length != 1){
            throw new IllegalArgumentException("dataType.length != 1  !!!");
        }

        DataType = dataType;
    }

    public byte[] getWGEnable() {
        return WGEnable;
    }

    public byte[] getWGType() {
        return WGType;
    }

    public byte[] getWGbitOrder() {
        return WGbitOrder;
    }

    public byte[] getDataType() {
        return DataType;
    }
}

