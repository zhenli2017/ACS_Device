package com.thdtek.acs.terminal.yzface.Message;

public class DataFileChunk {
    byte[] fileHandle = new byte[4];
    byte[] filePost = new byte[3];
    byte[]  chunk;

    public byte[] toBytes() {

        int index = 0;
        int iLength = getLength();
        byte[] data = new byte[iLength]; //137

        System.arraycopy(this.fileHandle, 0, data, index, fileHandle.length);
        index += fileHandle.length;

        System.arraycopy(this.filePost, 0, data, index, filePost.length);
        index += filePost.length;

        System.arraycopy(this.chunk, 0, data, index, chunk.length);
        index += chunk.length;

        return data;
    }
    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength() {
        int iLength = 0;
        iLength += this.fileHandle.length;
        iLength += this.filePost.length;
        iLength += this.chunk.length;

        return  iLength;
    }

    public DataFileChunk() {

    }

    public DataFileChunk(byte[] buffer) {
        int index =0;

        if( buffer.length<7 ) {
            throw new IllegalArgumentException(" buffer.length < 7  !!!");
        }

        //组装其他部件
        System.arraycopy(buffer,index, this.fileHandle,0,this.fileHandle.length );
        index += this.fileHandle.length;

        System.arraycopy(buffer,index, this.filePost,0,this.filePost.length );
        index += this.filePost.length;

        this.chunk = new byte[buffer.length -7];
        System.arraycopy(buffer,index, this.chunk,0,this.chunk.length );
        index += this.chunk.length;

    }

    public byte[] getFileHandle() {
        return fileHandle;
    }

    public void setFileHandle(byte[] fileHandle) {
        this.fileHandle = fileHandle;
    }

    public byte[] getFilePost() {
        return filePost;
    }

    public void setFilePost(byte[] filePost) {
        this.filePost = filePost;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public void setChunk(byte[] chunk) {

        this.chunk = new byte[chunk.length];
        System.arraycopy(chunk,0, this.chunk,0,this.chunk.length );

    }
}
