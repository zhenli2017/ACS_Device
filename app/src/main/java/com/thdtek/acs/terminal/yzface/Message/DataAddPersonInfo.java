package com.thdtek.acs.terminal.yzface.Message;

import java.util.ArrayList;
import java.util.List;

public class DataAddPersonInfo {
    byte[]  numberPersionInfo         = new byte[4];
    List<DataPersonInfo>  personInfos = new ArrayList<DataPersonInfo>();

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength() {
        int iLength = 0;
        iLength += this.numberPersionInfo.length;

        for(int i=0; i<this.personInfos.size(); i++) {
            iLength += this.personInfos.get(i).getLength();
        }
        return  iLength;
    }

    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //139*N

        System.arraycopy(this.numberPersionInfo, 0, data, index, numberPersionInfo.length);
        index += numberPersionInfo.length;

        for(int i=0; i<this.personInfos.size(); i++) {
            System.arraycopy(this.personInfos.get(i).toBytes(), 0, data, index, this.personInfos.get(i).getLength());
            index += this.personInfos.get(i).getLength();
        }

        return  data;
    }
    public DataAddPersonInfo(byte[] data) {

        int index = 0;
        if( data.length  < 4 ){
            throw new IllegalArgumentException(" data.length  < 4  !!!");
        }

        System.arraycopy(data,index, this.numberPersionInfo,0,this.numberPersionInfo.length );
        index += this.numberPersionInfo.length;

        int length =  (data.length -4);

        int mod = (length %  139);
        if(mod %139 !=0) {//第10的整数倍 就换行
            throw new IllegalArgumentException(" data mod  139 NO OK  !!!");
        }
        int rem = (length/139);

        if(rem  != ByteUtil.unsigned4BytesToInt(this.numberPersionInfo,0)) {//第10的整数倍 就换行
            throw new IllegalArgumentException(" numberPersionInfo   NO OK  !!!");
        }

        byte[] bytesPersonInfo = new byte[139];
        DataPersonInfo personInfo =null;
        for(int i=0; i< rem ; i++) {

            System.arraycopy(data,index, bytesPersonInfo,0,bytesPersonInfo.length );
            index += bytesPersonInfo.length;

            personInfo = new DataPersonInfo(bytesPersonInfo);
            personInfos.add(personInfo);
        }

    }

    public byte[] getNumberPersionInfo() {
        return numberPersionInfo;
    }

    public void setNumberPersionInfo(byte[] numberPersionInfo) {
        this.numberPersionInfo = numberPersionInfo;
    }

    public List<DataPersonInfo> getPersonInfos() {
        return personInfos;
    }

    public void setPersonInfos(List<DataPersonInfo> personInfos) {
        if(personInfos.size() < 1){
            throw new IllegalArgumentException(" personInfos.size() < 1  !!!");
        }

        personInfos = personInfos;
    }
}
