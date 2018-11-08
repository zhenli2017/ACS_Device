package com.thdtek.acs.terminal.yzface.Message;

public class DataVersion {
    byte[] major = new byte[2];
    byte[] revision = new byte[2];

    public  int getLength()
    {
        int iLength = 0;

        iLength += this.major.length;
        iLength +=  this.revision.length;

        return iLength;
    }

    public byte[] toBytes() {

        int index = 0;
        int iLength = getLength();
        byte[] data = new byte[iLength]; //137

        System.arraycopy(this.major, 0, data, index, major.length);
        index += major.length;

        System.arraycopy(this.revision, 0, data, index, revision.length);
        index += revision.length;

        return  data;
    }
    public DataVersion()
    {

    }
    public DataVersion(byte[] major, byte[] revision) {
        this.major = major;
        this.revision = revision;
    }

    public byte[] getMajor() {
        return major;
    }

    public void setMajor(byte[] major) {
        this.major = major;
    }

    public byte[] getRevision() {
        return revision;
    }

    public void setRevision(byte[] revision) {
        this.revision = revision;
    }
}
