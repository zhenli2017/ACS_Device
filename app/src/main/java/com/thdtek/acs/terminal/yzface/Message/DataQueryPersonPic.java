package com.thdtek.acs.terminal.yzface.Message;

public class DataQueryPersonPic {
    private  byte[] personID          = new byte[4];
    private  byte[] picList           = new byte[5];
    private  byte[] fingerprintList   = new byte[10];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength +=  this.personID.length;
        iLength +=  this.picList.length;
        iLength +=  this.fingerprintList.length;
        return iLength;
    }
    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //137

        System.arraycopy(this.personID, 0, data, index, personID.length);
        index += personID.length;

        System.arraycopy(this.picList, 0, data, index, picList.length);
        index += picList.length;

        System.arraycopy(this.fingerprintList, 0, data, index, fingerprintList.length);
        index += fingerprintList.length;

        return  data;
    }

    public DataQueryPersonPic(byte[] data) {
        int index =0;
        if(data.length != 19){
            throw new IllegalArgumentException("data.length != 19,DataQueryPersonPic length must == 19 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.personID,0,this.personID.length );
        index += this.personID.length;

        System.arraycopy(data,index, this.picList,0,this.picList.length );
        index += this.picList.length;

        System.arraycopy(data,index, this.fingerprintList,0,this.fingerprintList.length );
        index += this.fingerprintList.length;

    }

    public DataQueryPersonPic() {

    }

    public byte[] getPersonID() {
        return personID;
    }

    public void setPersonID(byte[] personID) {
        if(personID.length != 4){
            throw new IllegalArgumentException("personID.length != 4 !!!");
        }
        this.personID = personID;
    }

    public byte[] getPicList() {
        return picList;
    }

    public void setPicList(byte[] picList) {
        if(picList.length != 5){
            throw new IllegalArgumentException("picList.length != 5 !!!");
        }
        this.picList = picList;
    }

    public byte[] getFingerprintList() {
        return fingerprintList;
    }

    public void setFingerprintList(byte[] fingerprintList) {
        if(fingerprintList.length != 10){
            throw new IllegalArgumentException("fingerprintList.length != 10 !!!");
        }
        this.fingerprintList = fingerprintList;
    }
}
