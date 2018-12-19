package com.thdtek.acs.terminal.yzface.Message;

public class DataGetPersonPic {
    private byte[] fileType = new byte[1];
    private byte[] fileSN   = new byte[1];
    private byte[] personIdOrRecordSN = new byte[4];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.fileType.length;
        iLength += this.fileSN.length;
        iLength += this.personIdOrRecordSN.length;

        return  iLength;
    }

    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //139

        //组装其他部件
        System.arraycopy(this.fileType, 0, data, index, fileType.length);
        index += fileType.length;

        System.arraycopy(this.fileSN, 0, data, index, fileSN.length);
        index += fileSN.length;

        System.arraycopy(this.personIdOrRecordSN, 0, data, index, personIdOrRecordSN.length);
        index += personIdOrRecordSN.length;

        return  data;
    }
    public DataGetPersonPic(byte[] data) {
        int index =0;
        if(data.length != 6){
            throw new IllegalArgumentException("data.length != 6,DataGetPersonPic length must == 6 !!!");
        }
        //组装其他部件
        System.arraycopy(data,index, this.fileType,0,this.fileType.length );
        index += this.fileType.length;

        System.arraycopy(data,index, this.fileSN,0,this.fileSN.length );
        index += this.fileSN.length;

        System.arraycopy(data,index, this.personIdOrRecordSN,0,this.personIdOrRecordSN.length );
        index += this.personIdOrRecordSN.length;
    }

    public DataGetPersonPic() {
    }

    public byte[] getFileType() {
        return fileType;
    }

    public void setFileType(byte[] fileType) {
        if(fileType.length != 1){
            throw new IllegalArgumentException("fileType.length != 1  !!!");
        }
        this.fileType = fileType;
    }

    public byte[] getFileSN() {
        return fileSN;
    }

    public void setFileSN(byte[] fileSN) {
        if(fileSN.length != 1){
            throw new IllegalArgumentException("fileSN.length != 1  !!!");
        }
        this.fileSN = fileSN;
    }

    public byte[] getPersonIdOrRecordSN() {
        return personIdOrRecordSN;
    }

    public void setPersonIdOrRecordSN(byte[] personIdOrRecordSN) {
        if(personIdOrRecordSN.length != 4){
            throw new IllegalArgumentException("personIdOrRecordSN.length != 4  !!!");
        }
        this.personIdOrRecordSN = personIdOrRecordSN;
    }
}
