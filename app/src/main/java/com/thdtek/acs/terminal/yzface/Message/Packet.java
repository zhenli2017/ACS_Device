package com.thdtek.acs.terminal.yzface.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Packet implements Cloneable {
    private  byte[]     magicNumberS = new byte[1];
    private  byte[]     deviceSN = new byte[16];
    private  byte[]     password = new byte[4];
    private  byte[]     token = new  byte[4];
    private  Message    msg;
    private  byte[]     verify = new byte[1];
    private  byte[]     magicNumberE = new byte[1];


    public  static  boolean isPacket(byte [] buffer){

        boolean isPacket = true;

        int index = -1;
        int index2 = -1;

        if(buffer.length < 34){
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

        //检测最后幻数位置
        for(int i=0; i<buffer.length; i++){
            if( buffer[i] == (byte)0x7E){
                index2 = i;
            }
        }

        if(index <0 || index2 <0 ){
            return  false;
        }

        if( (index2 - index) < 33){
            return  false;
        }


        return  isPacket;
    }
    public  String toSendHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes(true));
    }

    public  String toReceivHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes(false));
    }




    public  Packet(Packet packet)
    {
            this.magicNumberS = packet.magicNumberS;
            this.deviceSN = packet.deviceSN;
            this.password = packet.password;
            this.token = packet.token;
            this.msg = packet.msg;
            this.verify = packet.verify;
            this.magicNumberE = packet.magicNumberE;
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength +=  this.magicNumberS.length;
        iLength +=  this.deviceSN.length;
        iLength +=  this.password.length;
        iLength +=  this.token.length;
        iLength +=  this.msg.getLength();
        iLength +=  this.verify.length;
        iLength +=  this.magicNumberE.length;
        return iLength;
    }


    public byte[] toBytes(boolean isSend)
    {
        int index =0;
        int ilength = getLength();
        byte[] buf = new byte[ilength];
        byte[]  verify = new byte[1];

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

        System.arraycopy(this.verify, 0, buf, index, this.verify.length);
        index += this.verify.length;

        System.arraycopy(this.magicNumberE, 0, buf, index, this.magicNumberE.length);
        index += this.magicNumberE.length;

        verify[0] = 0;
        //verify
        for(int i=1; i<buf.length-1-1;i++){
            verify[0] +=buf[i];
        }

        System.arraycopy(verify,0, this.verify,0,this.verify.length );
        if(buf.length>2)
        buf[buf.length-1-1] = verify[0];

        //由于命令中使用了0x7E作为命令的开始和结束标志，
        // 所以7E这个字节就不能出现在命令内容中只能出现在命令头和命令尾。
        //转译码如下：
        //0x7F 01 = 0x7E
        //0x7F 02 = 0x7F
        List<Byte> listByte7F = new ArrayList<Byte>();
        List<Byte> listByte7E = new ArrayList<Byte>();

        //跳过命令头和尾,先检查,排序0X7F
        for(int i=1; i< buf.length-1;i++){
            if(buf[i] == (byte)0x7F){
                listByte7F.add(buf[i]);
                listByte7F.add((byte)0x02);
            }else {
                listByte7F.add(buf[i]);
            }
        }

        byte temp = 0;
        Iterator it = listByte7F.iterator();
        while (it.hasNext()) {
            temp = (byte)it.next();
            if( temp== (byte)0x7E){
                listByte7E.add((byte)0x7F);
                listByte7E.add((byte)0x01);
            }else {
                listByte7E.add(temp);
            }
        }

        buf = new byte[listByte7E.size()+2];

        buf[0] = (byte)0x7E;
        int size = listByte7E.size();
        for (int i = 0; i < size; i++) {
            buf[1+i] = listByte7E.get(i);
        }
        buf[buf.length-1] = (byte)0x7E;

        return buf;

    }

    public Packet()
    {
        this.magicNumberS[0] = 0x7E;
        this.magicNumberE[0] = 0x7E;

    }
    public Packet(byte[] buffer, boolean isSend) throws MyException
    {
        byte verify =0;
        int index =0;
        int index2 =0;

        //检测开始幻数位置
        for(int i=0; i<buffer.length; i++){
            if( buffer[i] == (byte)0x7E){
                index=i;
                break;
            }
        }
        //检测最后幻数位置
        for(int i=0; i<buffer.length; i++){
            if( buffer[i] == (byte)0x7E){
                index2=i;
            }
        }

        //检测magicNumber是否匹配
        if(  index2-index < 33 )
        {
            throw new IllegalArgumentException("buffer format  matched !!!");
        }


        //由于命令中使用了0x7E作为命令的开始和结束标志，
        // 所以7E这个字节就不能出现在命令内容中只能出现在命令头和命令尾。
        //转译码如下：
        //0x7F 01 = 0x7E
        //0x7F 02 = 0x7F
        List<Byte> listByte = new ArrayList<Byte>();
        boolean beforeIs7F = false;
        for(int i=index+1; i<index2;i++)
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

        byte[] bufferEx = new byte[listByte.size()+2];

        bufferEx[0] = (byte)0x7E;
        int size = listByte.size();
        for (int i = 0; i < size; i++) {
            bufferEx[1+i] = listByte.get(i);
        }
        bufferEx[bufferEx.length-1] = (byte)0x7E;



        //计算verify是否正确。
        for(int i=1; i<bufferEx.length-1-1;i++){
            verify +=bufferEx[i];
        }
        if(verify != bufferEx[bufferEx.length-1-1]){
            throw new MyException( "verify 不匹配" ) ;
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

        int intMsgLength = bufferEx.length - 27;
        byte[] byteMsg = new byte[intMsgLength];
        System.arraycopy(bufferEx,index, byteMsg,0,byteMsg.length );
        index += byteMsg.length;

        this.msg = new Message(byteMsg);

        System.arraycopy(bufferEx,index, this.verify,0,this.verify.length );
        index += this.verify.length;

        System.arraycopy(bufferEx,index, this.magicNumberE,0,this.magicNumberE.length );
        index += this.magicNumberE.length;
    }

    public byte[] getMagicNumberS() {
        return magicNumberS;
    }

    public void setMagicNumberS(byte[] magicNumberS) {
        if(magicNumberS.length != 1 || magicNumberS[0] != 0x7F  ){
            throw new IllegalArgumentException("magicNumberS.length != 1  or != 0x7F !!!");
        }
        this.magicNumberS = magicNumberS;
    }

    public byte[] getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(byte[] deviceSN) {
        if(deviceSN.length != 16){
            throw new IllegalArgumentException("deviceSN.length != 16  !!!");
        }
        this.deviceSN = deviceSN;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        if(password.length != 4){
            throw new IllegalArgumentException("password.length != 4  !!!");
        }
        this.password = password;
    }

    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        if(token.length != 4){
            throw new IllegalArgumentException("token.length != 4  !!!");
        }
        this.token = token;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public byte[] getVerify() {
        return verify;
    }

    public void setVerify(byte[] verify) {
        if(verify.length != 1){
            throw new IllegalArgumentException("verify.length != 1  !!!");
        }
/*
        byte[] byteVerify = new byte[1];

        int iLength = this.getLength();
        byte[] bufferEx = new byte[iLength];

        bufferEx = this.toBytes(true);

        for(int i=1; i<bufferEx.length-1-1;i++){
            byteVerify[0] +=bufferEx[i];
        }

        System.arraycopy(byteVerify,0, this.verify,0,this.verify.length );

*/
    }

    public byte[] getMagicNumberE() {
        return magicNumberE;
    }

    public void setMagicNumberE(byte[] magicNumberE) {
        if(magicNumberE.length != 1 || magicNumberE[0] != 0x7F  ){
            throw new IllegalArgumentException("magicNumberE.length != 1  or != 0x7F !!!");
        }
        this.magicNumberE = magicNumberE;
    }

    @Override
    public  Object clone()  {
        return new Packet(this);
    }
}
