package com.thdtek.acs.terminal.yzface.Message;

import java.util.ArrayList;
import java.util.List;

public class DataPersonInfoList {
    private byte[] personInfoCount = new byte[4];
    private List<DataPersonInfo> dataPersonInfoList = new ArrayList<DataPersonInfo>();

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public boolean Add(DataPersonInfo info){
        return  this.dataPersonInfoList.add(info);
    }

    public int size(){
        return  this.dataPersonInfoList.size();
    }
    public  int getLength()
    {
        int iLength = 0;
        iLength += personInfoCount.length;
        for(int i=0; i<dataPersonInfoList.size(); i++){
            iLength += dataPersonInfoList.get(i).getLength();
        }
        return iLength;
    }

    public byte[] toBytes(){

        int index =0;
        int iLength  = 0;
        byte[] data = null;

        iLength += personInfoCount.length;

        for(int i=0; i<dataPersonInfoList.size(); i++){
            iLength += dataPersonInfoList.get(i).getLength();
        }

        if( iLength>0 ) {
            data = new byte[iLength]; //137

            System.arraycopy(this.personInfoCount, 0, data, index, personInfoCount.length);
            index += personInfoCount.length;

            for(int i=0; i<dataPersonInfoList.size(); i++){
                System.arraycopy(dataPersonInfoList.get(i).toBytes(), 0, data, index, dataPersonInfoList.get(i).getLength());
                index += dataPersonInfoList.get(i).getLength();
            }
        }
        return data;
    }

    public DataPersonInfoList() {
    }

    public byte[] getPersonInfoCount() {
        return personInfoCount;
    }

    public List<DataPersonInfo> getDataPersonInfoList() {
        return dataPersonInfoList;
    }

    public void setPersonInfoCount(byte[] personInfoCount) {
        if(personInfoCount.length != 4){
            throw new IllegalArgumentException("recordCount.length != 4  !!!");
        }
        this.personInfoCount = personInfoCount;
    }

    public void setDataPersonInfoList(List<DataPersonInfo> dataPersonInfoList) {
        this.dataPersonInfoList = dataPersonInfoList;
    }
}
