package com.thdtek.acs.terminal.yzface.Message;

import java.util.ArrayList;
import java.util.List;

public class DataRecordInfoList {
    private byte[] recordCount = new byte[2];
    private List<DataRecordInfo> dataRecordInfoList = new ArrayList<DataRecordInfo>();

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public boolean Add(DataRecordInfo info){
        return  this.dataRecordInfoList.add(info);
    }

    public int size(){
        return  this.dataRecordInfoList.size();
    }
    public  int getLength()
    {
        int iLength = 0;
        iLength += recordCount.length;
        for(int i=0; i<dataRecordInfoList.size(); i++){
            iLength += dataRecordInfoList.get(i).getLength();
        }
        return iLength;
    }

    public byte[] toBytes(){

        int index =0;
        int iLength  = 0;
        byte[] data = null;

        iLength += recordCount.length;

        for(int i=0; i<dataRecordInfoList.size(); i++){
            iLength += dataRecordInfoList.get(i).getLength();
        }

        if( iLength>0 ) {
            data = new byte[iLength]; //137

            System.arraycopy(this.recordCount, 0, data, index, recordCount.length);
            index += recordCount.length;

            for(int i=0; i<dataRecordInfoList.size(); i++){
                System.arraycopy(dataRecordInfoList.get(i).toBytes(), 0, data, index, dataRecordInfoList.get(i).getLength());
                index += dataRecordInfoList.get(i).getLength();
            }
        }
        return data;
    }

    public DataRecordInfoList() {
    }

    public List<DataRecordInfo> getDataRecordInfoList() {
        return dataRecordInfoList;
    }

    public void setDataRecordInfoList(List<DataRecordInfo> dataRecordInfoList) {
        this.dataRecordInfoList = dataRecordInfoList;
    }

    public byte[] getrecordCount() {
        return recordCount;
    }

    public void setRecordCunt(byte[] recordCount) {
        if(recordCount.length != 2){
            throw new IllegalArgumentException("recordCount.length != 2  !!!");
        }
        this.recordCount = recordCount;
    }
}
