package com.thdtek.acs.terminal.yzface.Message;

public class DataPersonInfo {
    private byte[] id               = new byte[4];
    private byte[] name             = new byte[30];
    private byte[] number           = new byte[30];
    private byte[] department       = new byte[30];
    private byte[] job              = new byte[30];
    private byte[] picNumber        = new byte[1];
    private byte[] cardNumber       = new byte[8];
    private byte[] termOfValidity   = new byte[5];
    private byte[] status           = new byte[1];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.id.length;
        iLength += this.name.length;
        iLength += this.number.length;
        iLength += this.department.length;
        iLength += this.job.length;
        iLength += this.picNumber.length;
        iLength += this.cardNumber.length;
        iLength += this.termOfValidity.length;
        iLength += this.status.length;
        return  iLength;
    }

    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //139

        //组装其他部件
        System.arraycopy(this.id, 0, data, index, id.length);
        index += id.length;

        System.arraycopy(this.name, 0, data, index, name.length);
        index += name.length;

        System.arraycopy(this.number, 0, data, index, number.length);
        index += number.length;

        System.arraycopy(this.department, 0, data, index, department.length);
        index += department.length;

        System.arraycopy(this.job, 0, data, index, job.length);
        index += job.length;

        System.arraycopy(this.picNumber, 0, data, index, picNumber.length);
        index += picNumber.length;

        System.arraycopy(this.cardNumber, 0, data, index, cardNumber.length);
        index += cardNumber.length;

        System.arraycopy(this.termOfValidity, 0, data, index, termOfValidity.length);
        index += termOfValidity.length;

        System.arraycopy(this.status, 0, data, index, status.length);
        index += status.length;

        return  data;
    }
    public  DataPersonInfo(){

    }

    public DataPersonInfo(byte[] data) {
        int index =0;
        if(data.length != 139){
            throw new IllegalArgumentException("data.length != 139,DataPersonInfo length must == 139 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.id,0,this.id.length );
        index += this.id.length;

        System.arraycopy(data,index, this.name,0,this.name.length );
        index += this.name.length;

        System.arraycopy(data,index, this.number,0,this.number.length );
        index += this.number.length;

        System.arraycopy(data,index, this.department,0,this.department.length );
        index += this.department.length;

        System.arraycopy(data,index, this.job,0,this.job.length );
        index += this.job.length;

        System.arraycopy(data,index, this.picNumber,0,this.picNumber.length );
        index += this.picNumber.length;

        System.arraycopy(data,index, this.cardNumber,0,this.cardNumber.length );
        index += this.cardNumber.length;

        System.arraycopy(data,index, this.termOfValidity,0,this.termOfValidity.length );
        index += this.termOfValidity.length;

        System.arraycopy(data,index, this.status,0,this.status.length );
        index += this.status.length;
    }


    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        if(id.length != 4){
            throw new IllegalArgumentException("id.length != 4  !!!");
        }
        this.id = id;
    }

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        if(name.length != 30){
            throw new IllegalArgumentException("name.length != 30  !!!");
        }
        this.name = name;
    }

    public byte[] getNumber() {
        return number;
    }

    public void setNumber(byte[] number) {
        if(number.length != 30){
            throw new IllegalArgumentException("number.length != 30  !!!");
        }
        this.number = number;
    }

    public byte[] getDepartment() {
        return department;
    }

    public void setDepartment(byte[] department) {
        if(department.length != 30){
            throw new IllegalArgumentException("department.length != 30  !!!");
        }
        this.department = department;
    }

    public byte[] getJob() {
        return job;
    }

    public void setJob(byte[] job) {
        if(job.length != 30){
            throw new IllegalArgumentException("job.length != 30  !!!");
        }
        this.job = job;
    }

    public byte[] getPicNumber() {
        return picNumber;
    }

    public void setPicNumber(byte[] picNumber) {
        if(picNumber.length != 1){
            throw new IllegalArgumentException("job.length != 1  !!!");
        }
        this.picNumber = picNumber;
    }

    public byte[] getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(byte[] cardNumber) {
        if(cardNumber.length != 8){
            throw new IllegalArgumentException("cardNumber.length != 8  !!!");
        }
        this.cardNumber = cardNumber;
    }

    public byte[] getTermOfValidity() {
        return termOfValidity;
    }

    public void setTermOfValidity(byte[] termOfValidity) {
        if(termOfValidity.length != 5){
            throw new IllegalArgumentException("termOfValidity.length != 5  !!!");
        }
        this.termOfValidity = termOfValidity;
    }

    public byte[] getStatus() {
        return status;
    }

    public void setStatus(byte[] status) {
        if(termOfValidity.length != 1){
            throw new IllegalArgumentException("termOfValidity.length != 1  !!!");
        }
        this.status = status;
    }
}
