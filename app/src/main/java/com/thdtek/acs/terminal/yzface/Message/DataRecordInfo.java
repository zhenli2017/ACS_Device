package com.thdtek.acs.terminal.yzface.Message;

public class DataRecordInfo {
    private   byte[] recordSN = new byte[4];
    private   byte[] personID = new byte[4];
    private   byte[] date     = new byte[6];
    private   byte[] direction = new byte[1];
    private   byte[] status    = new byte[1];
    private   byte[] picture   = new byte[1];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.recordSN.length;
        iLength +=  this.personID.length;
        iLength +=  this.date.length;
        iLength +=  this.direction.length;
        iLength +=  this.status.length;
        iLength +=  this.picture.length;
        return iLength;
    }
    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //137

        System.arraycopy(this.recordSN, 0, data, index, recordSN.length);
        index += recordSN.length;

        System.arraycopy(this.personID, 0, data, index, personID.length);
        index += personID.length;

        System.arraycopy(this.date, 0, data, index, date.length);
        index += date.length;

        System.arraycopy(this.direction, 0, data, index, direction.length);
        index += direction.length;

        System.arraycopy(this.status, 0, data, index, status.length);
        index += status.length;

        System.arraycopy(this.picture, 0, data, index, picture.length);
        index += picture.length;


        return  data;
    }
    public DataRecordInfo()
    {

    }
    public DataRecordInfo(byte[] data) {

        int index =0;
        if(data.length != 17){
            throw new IllegalArgumentException("data.length != 17,DataIPInfo length must == 17 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.recordSN,0,this.recordSN.length );
        index += this.recordSN.length;

        System.arraycopy(data,index, this.personID,0,this.personID.length );
        index += this.personID.length;

        System.arraycopy(data,index, this.date,0,this.date.length );
        index += this.date.length;

        System.arraycopy(data,index, this.direction,0,this.direction.length );
        index += this.direction.length;

        System.arraycopy(data,index, this.status,0,this.status.length );
        index += this.status.length;

        System.arraycopy(data,index, this.picture,0,this.picture.length );
        index += this.picture.length;
    }

    public byte[] getRecordSN() {
        return recordSN;
    }

    public void setRecordSN(byte[] recordSN) {
        if(recordSN.length != 4){
            throw new IllegalArgumentException("recordSN.length != 4  !!!");
        }
        this.recordSN = recordSN;
    }

    public byte[] getPersonID() {
        return personID;
    }

    public void setPersonID(byte[] personID) {
        if(personID.length != 4){
            throw new IllegalArgumentException("personID.length != 4  !!!");
        }
        this.personID = personID;
    }

    public byte[] getDate() {
        return date;
    }

    public void setDate(byte[] date) {
        if(date.length != 6){
            throw new IllegalArgumentException("date.length != 6  !!!");
        }
        this.date = date;
    }

    public byte[] getDirection() {
        return direction;
    }

    public void setDirection(byte[] direction) {
        if(direction.length != 1){
            throw new IllegalArgumentException("direction.length != 6  !!!");
        }
        this.direction = direction;
    }

    public byte[] getStatus() {
        return status;
    }

    public void setStatus(byte[] status) {
        if(status.length != 1){
            throw new IllegalArgumentException("status.length != 6  !!!");
        }
        this.status = status;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        if(picture.length != 1){
            throw new IllegalArgumentException("picture.length != 6  !!!");
        }
        this.picture = picture;
    }
}
