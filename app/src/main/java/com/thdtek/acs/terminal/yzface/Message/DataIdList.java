package com.thdtek.acs.terminal.yzface.Message;

import java.util.ArrayList;
import java.util.List;

public class DataIdList {
    private byte[] idCount = new byte[4];
    private List<DataId> dataIdList = new ArrayList<DataId>();

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public boolean Add(DataId dataId){
        return  this.dataIdList.add(dataId);
    }

    public int size(){
        return  this.dataIdList.size();
    }
    public  int getLength()
    {
        int iLength = 0;
        iLength += idCount.length;
        for(int i=0; i<dataIdList.size(); i++){
            iLength += dataIdList.get(i).getLength();
        }
        return iLength;
    }

    public byte[] toBytes(){

        int index =0;
        int iLength  = 0;
        byte[] data = null;

        iLength += idCount.length;

        for(int i=0; i<dataIdList.size(); i++){
            iLength += dataIdList.get(i).getLength();
        }

        if( iLength>0 ) {
            data = new byte[iLength]; //137

            System.arraycopy(this.idCount, 0, data, index, idCount.length);
            index += idCount.length;

            for(int i=0; i<dataIdList.size(); i++){
                System.arraycopy(dataIdList.get(i).toBytes(), 0, data, index, dataIdList.get(i).getLength());
                index += dataIdList.get(i).getLength();
            }
        }
        return data;
    }

    public DataIdList() {

    }

    public DataIdList( byte[] data) {
        int index =0;
        if(data.length < 4){
            throw new IllegalArgumentException("data.length < 4 ,DataIdList length must >= 4 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.idCount,0,this.idCount.length );
        index += this.idCount.length;

        //计算数量为idCount的数据长度
        if(ByteUtil.getInt(this.idCount,false) != (data.length-4)/4){
            throw new IllegalArgumentException("data.length !=4* ,DataIdList length must == 4*N !!!");
        }

        index =4;
        DataId dataId = new DataId();
        byte[] tmpId = new byte[4];

        for(int i=0; i<ByteUtil.getInt(this.idCount,false); i++) {
            tmpId = new byte[4];
            dataId = new DataId();
            System.arraycopy(data, index, tmpId, 0, tmpId.length);
            index += tmpId.length;
            dataId.setId(tmpId);
            dataIdList.add(dataId);
        }
    }

    public byte[] getIdCount() {
        return idCount;
    }

    public List<DataId> getDataIdList() {
        return dataIdList;
    }

    public void setIdCount(byte[] idCount) {
        if(idCount.length != 4){
            throw new IllegalArgumentException("idCount.length != 4  !!!");
        }
        this.idCount = idCount;
    }

    public void setDataIdList(List<DataId> dataIdList) {
        this.dataIdList = dataIdList;
    }
}
