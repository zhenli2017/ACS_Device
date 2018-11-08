package com.thdtek.acs.terminal.yzface.Message;

public class Message {
    private byte[] category = new byte[1];
    private byte[] command = new byte[1];
    private byte[] parameter = new byte[1];
    private byte[] dataLength = new byte[4];
    private byte[] data =null;

    public  int getCode()
    {
        int targets = (parameter[0] & 0xff) | ((command[0] << 8) & 0xff00)| ((category[0] << 16) & 0xff0000); // | 表示安位或
        return targets;
    }

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength +=  this.category.length;
        iLength +=  this.command.length;
        iLength +=  this.parameter.length;
        if(dataLength !=null)
            iLength +=  this.dataLength.length;
        if(data != null)
            iLength +=  this.data.length;
        return iLength;
    }

    public byte[] toBytes()
    {
        int index =0;
        int iLength  = getLength();
        byte [] buf = new byte[iLength];

        System.arraycopy(this.category, 0, buf, index, category.length);
        index += category.length;

        System.arraycopy(this.command, 0, buf, index, command.length);
        index += command.length;

        System.arraycopy(this.parameter, 0, buf, index, parameter.length);
        index += parameter.length;

        if(this.dataLength != null ) {

            System.arraycopy(this.dataLength, 0, buf, index, dataLength.length);
            index += dataLength.length;

            int idataLength = (int) ByteUtil.unsigned4BytesToInt(dataLength, 0);

            if (idataLength > 0) {
                if(this.data != null) {
                    System.arraycopy(this.data, 0, buf, index, data.length);
                    index += data.length;
                }
            }
        }

        return  buf;
    }

    public Message() {

    }
    public Message(byte[] buffer) {

        int index =0;

        if( buffer.length<7 ) {
            throw new IllegalArgumentException(" buffer.length < 7  !!!");
        }

        //组装其他部件
        System.arraycopy(buffer,index, this.category,0,this.category.length );
        index += this.category.length;

        System.arraycopy(buffer,index, this.command,0,this.command.length );
        index += this.command.length;

        System.arraycopy(buffer,index, this.parameter,0,this.parameter.length );
        index += this.parameter.length;

        System.arraycopy(buffer,index, this.dataLength,0,this.dataLength.length );
        index += this.dataLength.length;


        int iLength  = (int)ByteUtil.unsigned4BytesToInt(this.dataLength,0);

        if(iLength != buffer.length -7){
            throw new IllegalArgumentException(" dataLength !=data.length   !!!");
        }

        if(iLength>1) {
            this.data = new byte[iLength];
            System.arraycopy(buffer, index, this.data, 0, iLength);
        }
    }


    public byte[] getDataLength() {
        return dataLength;
    }

    public void setDataLength(byte[] dataLength) {

        if(dataLength != null) {
            if (dataLength.length != 4) {
                throw new IllegalArgumentException("dataLength.length != 4  !!!");
            }
            this.dataLength = dataLength;
        }else {
            this.dataLength = null;
        }
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {

        if(data != null ) {
            int iLength = (int) ByteUtil.unsigned4BytesToInt(this.dataLength, 0);
            if (data.length != iLength) {
                throw new IllegalArgumentException("data.length != dataLength  !!!");
            }
            this.data = new byte[iLength];
            System.arraycopy(data, 0, this.data, 0, iLength);
        }else {
            this.data = null;
        }
    }

    public byte[] getCategory() {
        return category;
    }

    public void setCategory(byte[] category) {
        if(category.length != 1){
            throw new IllegalArgumentException("category.length != 1  !!!");
        }
        this.category = category;
    }

    public byte[] getCommand() {
        return command;
    }

    public void setCommand(byte[] command) {
        if(command.length != 1){
            throw new IllegalArgumentException("command.length != 1  !!!");
        }
        this.command = command;
    }

    public byte[] getParameter() {
        return parameter;
    }

    public void setParameter(byte[] parameter) {
        if(parameter.length != 1){
            throw new IllegalArgumentException("parameter.length != 1  !!!");
        }
        this.parameter = parameter;
    }
}
