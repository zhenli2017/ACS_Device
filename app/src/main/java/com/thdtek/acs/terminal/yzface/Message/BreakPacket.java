package com.thdtek.acs.terminal.yzface.Message;

import java.util.ArrayList;
import java.util.List;

public class BreakPacket {
    private  byte[]     magicNumberS = new byte[1];
    private  byte[]     deviceSN = new byte[16];
    private  byte[]     password = new byte[4];
    private  byte[]     token = new  byte[4];
    private  Message    msg;

    public  static  boolean isBreakPacket(byte [] buffer){

        boolean isBreakPacket = true;

        int index = -1;

        if(buffer.length < 32){
            return  false;
        }

        //检测magicNumber是否匹配
        //检测开始幻数位置
        for(int i=0; i<buffer.length; i++){
            if( buffer[i] == (byte)0x7E){
                index=i;
                break;
            }
        }

        if(index <0 ){
            return  false;
        }

        if((buffer.length - index) <32){
            return  false;
        }
        return  isBreakPacket;
    }

    public  String toSendHexString(boolean isSend)
    {
        return  ByteUtil.bytesToHexString(this.toBytes(isSend));
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength +=  this.magicNumberS.length;
        iLength +=  this.deviceSN.length;
        iLength +=  this.password.length;
        iLength +=  this.token.length;
        iLength +=  this.msg.getLength();
        return iLength;
    }

    public byte[] toBytes(boolean isSend)
    {
        int index =0;
        int ilength = getLength();
        byte[] buf = new byte[ilength];

        System.arraycopy(this.magicNumberS, 0, buf, index, this.magicNumberS.length);
        index += this.magicNumberS.length;

        if(isSend == false) { //false，接收包
            System.arraycopy(this.deviceSN, 0, buf, index, this.deviceSN.length);
            index += this.deviceSN.length;

            System.arraycopy(this.password, 0, buf, index, this.password.length);
            index += this.password.length;

            System.arraycopy(this.token, 0, buf, index, this.token.length);
            index += this.token.length;
        }else {
            System.arraycopy(this.token, 0, buf, index, this.token.length);
            index += this.token.length;

            System.arraycopy(this.deviceSN, 0, buf, index, this.deviceSN.length);
            index += this.deviceSN.length;

            System.arraycopy(this.password, 0, buf, index, this.password.length);
            index += this.password.length;
        }

        System.arraycopy(this.msg.toBytes(), 0, buf, index, this.msg.getLength());
        index += this.msg.getLength();

        return  buf;
    }

    public BreakPacket(byte[] buffer, boolean isSend) throws MyException
    {
        byte verify = 0;
        int index   = -1;

        if(  buffer.length < 32 )
        {
            throw new IllegalArgumentException("buffer.length < 32 !!!");
        }

        //检测magicNumber是否匹配
        //检测开始幻数位置
        for(int i=0; i<buffer.length; i++){
            if( buffer[i] == (byte)0x7E){
                index=i;
                break;
            }
        }

        if(index <0 ){
            throw new IllegalArgumentException("buffer nof found magicNumberS 0x7E !!!");
        }

        if((buffer.length - index) <32){
            throw new IllegalArgumentException("2 buffer.length < 32 !!!");
        }

        //由于命令中使用了0x7E作为命令的开始和结束标志，
        // 所以7E这个字节就不能出现在命令内容中只能出现在命令头和命令尾。
        //转译码如下：
        //0x7F 01 = 0x7E
        //0x7F 02 = 0x7F
        List<Byte> listByte = new ArrayList<Byte>();
        boolean beforeIs7F = false;
        for(int i=index+1; i<buffer.length;i++)
        {
            if(buffer[i]==0x01 )
            {
                if(beforeIs7F) {
                    listByte.add((byte)0x7E);
                }else {
                    listByte.add(buffer[i]);
                }
                beforeIs7F = false;
            }else if(buffer[i]==0x02)
            {
                if(beforeIs7F) {
                    listByte.add((byte)0x7F);
                }else {
                    listByte.add(buffer[i]);
                }
                beforeIs7F = false;
            }else if(buffer[i]==0x7F)
            {
                beforeIs7F = true;
            }else{
                listByte.add(buffer[i]);
                beforeIs7F = false;
            }
        }

        byte[] bufferEx = new byte[listByte.size()+1];

        bufferEx[0] = (byte)0x7E;
        int size = listByte.size();
        for (int i = 0; i < size; i++) {
            bufferEx[1+i] = listByte.get(i);
        }

        index =0;

        //组装其他部件
        System.arraycopy(buffer,index, this.magicNumberS,0,this.magicNumberS.length );
        index += this.magicNumberS.length;

        if(isSend == false) { //false，接收包

            System.arraycopy(bufferEx,index, this.deviceSN,0,this.deviceSN.length );
            index += this.deviceSN.length;

            System.arraycopy(bufferEx,index, this.password,0,this.password.length );
            index += this.password.length;

            System.arraycopy(bufferEx,index, this.token,0,this.token.length );
            index += this.token.length;

        }else  // true,发送包
        {
            System.arraycopy(bufferEx,index, this.token,0,this.token.length );
            index += this.token.length;

            System.arraycopy(bufferEx,index, this.deviceSN,0,this.deviceSN.length );
            index += this.deviceSN.length;

            System.arraycopy(bufferEx,index, this.password,0,this.password.length );
            index += this.password.length;
        }

        //获取消息长度
        byte[] msgLength = new byte[4];
        System.arraycopy(bufferEx,index+3, msgLength,0,msgLength.length );
        int imsgLenth = (int)ByteUtil.unsigned4BytesToInt(msgLength,0);

        if( (bufferEx.length -25) != imsgLenth){
            throw new IllegalArgumentException("MSG length NG !!!");
        }
        byte[] byteMsg = new byte[imsgLenth];
        this.msg = new Message(byteMsg);

        System.arraycopy(bufferEx,index, byteMsg,0,byteMsg.length );
        index += byteMsg.length;

        this.msg = new Message(byteMsg);

    }

    public byte[] getMagicNumberS() {
        return magicNumberS;
    }

    public void setMagicNumberS(byte[] magicNumberS) {
        this.magicNumberS = magicNumberS;
    }

    public byte[] getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(byte[] deviceSN) {
        this.deviceSN = deviceSN;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }
}
