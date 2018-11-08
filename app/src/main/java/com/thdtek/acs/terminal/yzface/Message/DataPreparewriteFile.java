package com.thdtek.acs.terminal.yzface.Message;

public class DataPreparewriteFile {
    byte[] personID = new byte[4];
    byte[] fileType = new byte[1];
    byte[] fileSN   = new byte[1];


    public byte[] toBytes() {

        int index = 0;
        int iLength = getLength();
        byte[] data = new byte[iLength]; //137

        System.arraycopy(this.personID, 0, data, index, personID.length);
        index += personID.length;

        System.arraycopy(this.fileType, 0, data, index, fileType.length);
        index += fileType.length;

        System.arraycopy(this.fileSN, 0, data, index, fileSN.length);
        index += fileSN.length;

        return data;
    }
    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength() {
        int iLength = 0;
        iLength += this.personID.length;
        iLength += this.fileType.length;
        iLength += this.fileSN.length;

        return  iLength;
    }

    public DataPreparewriteFile(){
    }

   public DataPreparewriteFile(byte[] data){
        int index =0;
        if(data.length != 6){
            throw new IllegalArgumentException("data.length != 6 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.personID,0,this.personID.length );
        index += this.personID.length;

        System.arraycopy(data,index, this.fileType,0,this.fileType.length );
        index += this.fileType.length;

        System.arraycopy(data,index, this.fileSN,0,this.fileSN.length );
        index += this.fileSN.length;

    }

    public DataPreparewriteFile(byte[] personID, byte[] fileType, byte[] fileSN) {
        this.personID = personID;
        this.fileType = fileType;
        this.fileSN = fileSN;
    }

    public byte[] getPersonID() {
        return personID;
    }

    public void setPersonID(byte[] personID) {
        this.personID = personID;
    }

    public byte[] getFileType() {
        return fileType;
    }

    public void setFileType(byte[] fileType) {
        this.fileType = fileType;
    }

    public byte[] getFileSN() {
        return fileSN;
    }

    public void setFileSN(byte[] fileSN) {
        this.fileSN = fileSN;
    }
}
