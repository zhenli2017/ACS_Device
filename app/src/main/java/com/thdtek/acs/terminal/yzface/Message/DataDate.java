package com.thdtek.acs.terminal.yzface.Message;

public class DataDate { //ssmmHHddMMWWyy
    private   byte[] second = new byte[1];
    private   byte[] minute = new byte[1];
    private   byte[] hour     = new byte[1];
    private   byte[] date = new byte[1];
    private   byte[] month    = new byte[1];
    private   byte[] week   = new byte[1];
    private   byte[] year   = new byte[1];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.second.length;
        iLength += this.minute.length;
        iLength += this.hour.length;
        iLength += this.date.length;
        iLength += this.month.length;
        iLength += this.week.length;
        iLength += this.year.length;

        return  iLength;
    }

    public byte[] toBytes(){
        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //7

        //组装其他部件
        System.arraycopy(this.second, 0, data, index, second.length);
        index += second.length;

        System.arraycopy(this.minute, 0, data, index, minute.length);
        index += minute.length;

        System.arraycopy(this.hour, 0, data, index, hour.length);
        index += hour.length;

        System.arraycopy(this.date, 0, data, index, date.length);
        index += date.length;

        System.arraycopy(this.month, 0, data, index, month.length);
        index += month.length;

        System.arraycopy(this.week, 0, data, index, week.length);
        index += week.length;

        System.arraycopy(this.year, 0, data, index, year.length);
        index += year.length;

        return  data;
    }
    public DataDate() {

    }

    public DataDate(byte[] data) {
        int index =0;
        if(data.length != 7){
            throw new IllegalArgumentException("data.length != 7,DataPersonInfo length must == 7 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.second,0,this.second.length );
        index += this.second.length;

        if( (this.second[0] < 0) || (this.second[0] > 0x59) ){
            throw new IllegalArgumentException("second must >=0 && <=0x59   !!!");
        }

        System.arraycopy(data,index, this.minute,0,this.minute.length );
        index += this.minute.length;

        if( (this.minute[0] < 0) || (this.minute[0] > 0x59) ){
            throw new IllegalArgumentException("minute must >=0 && <=0x59   !!!");
        }

        System.arraycopy(data,index, this.hour,0,this.hour.length );
        index += this.hour.length;

        if( (this.hour[0] < 0) || (this.hour[0] > 0x23) ){
            throw new IllegalArgumentException("hour must >=0 && <=0x23   !!!");
        }


        System.arraycopy(data,index, this.date,0,this.date.length );
        index += this.date.length;

        if( (this.date[0] < 1) || (this.date[0] > 0x31) ){
            throw new IllegalArgumentException("date must >=1 && <=0x31   !!!");
        }

        System.arraycopy(data,index, this.month,0,this.month.length );
        index += this.month.length;

        if( ( this.month[0] < 1) || ( this.month[0] > 0x12) ){
            throw new IllegalArgumentException("month must >=1 && <=0x12   !!!");
        }


        System.arraycopy(data,index, this.week,0,this.week.length );
        index += this.week.length;

        if( (this.week[0] < 1) || (this.week[0] > 0x7) ){
            throw new IllegalArgumentException("month must >=1 && <=0x7   !!!");
        }


        System.arraycopy(data,index, this.year,0,this.year.length );
        index += this.year.length;

    }

    public byte[] getSecond() {
        return second;
    }

    public byte[] getMinute() {
        return minute;
    }

    public byte[] getHour() {
        return hour;
    }

    public byte[] getDate() {
        return date;
    }

    public byte[] getMonth() {
        return month;
    }

    public byte[] getWeek() {
        return week;
    }

    public byte[] getYear() {
        return year;
    }

    public void setSecond(byte[] second) {
        if(second.length != 1){
            throw new IllegalArgumentException("second.length != 1  !!!");
        }

        if( (second[0] < 0) || (second[0] > 0x59) ){
            throw new IllegalArgumentException("second must >=0 && <=0x59   !!!");
        }

        this.second = second;
    }

    public void setMinute(byte[] minute) {
        if(minute.length != 1){
            throw new IllegalArgumentException("minute.length != 1  !!!");
        }

        if( (minute[0] < 0) || (minute[0] > 0x59) ){
            throw new IllegalArgumentException("minute must >=0 && <=0x59   !!!");
        }

        this.minute = minute;
    }

    public void setHour(byte[] hour) {
        if(hour.length != 1){
            throw new IllegalArgumentException("hour.length != 1  !!!");
        }

        if( (hour[0] < 0) || (hour[0] > 0x23) ){
            throw new IllegalArgumentException("hour must >=0 && <=0x23   !!!");
        }

        this.hour = hour;
    }

    public void setDate(byte[] date) {
        if(date.length != 1){
            throw new IllegalArgumentException("date.length != 1  !!!");
        }

        if( (date[0] < 1) || (date[0] > 0x31) ){
            throw new IllegalArgumentException("date must >=1 && <=0x31   !!!");
        }

        this.date = date;
    }

    public void setMonth(byte[] month) {
        if(month.length != 1){
            throw new IllegalArgumentException("month.length != 1  !!!");
        }

        if( (month[0] < 1) || (month[0] > 0x12) ){
            throw new IllegalArgumentException("month must >=1 && <=0x12   !!!");
        }

        this.month = month;
    }

    public void setWeek(byte[] week) {
        if(week.length != 1){
            throw new IllegalArgumentException("week.length != 1  !!!");
        }

        if( (week[0] < 1) || (week[0] > 0x7) ){
            throw new IllegalArgumentException("month must >=1 && <=0x7   !!!");
        }

        this.week = week;
    }

    public void setYear(byte[] year) {
        if(year.length != 1){
            throw new IllegalArgumentException("week.length != 1  !!!");
        }
        this.year = year;
    }
}
