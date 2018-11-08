package com.thdtek.acs.terminal.util.camera;


import com.hwit.HwitManager;

// https://blog.csdn.net/j182010/article/details/52679091
public class Wiegand {


    public Wiegand() {
      //  韦根协议又称韦根码，韦根码在数据的传输中只需两条数据线，一条为DATA0，另一条为DATA1。
      //  协议规定，两条数据线在无数据时均为高电平， DATA0为低电平代表数据0，DATA1为低电平代表数据1
        // （低电平信号低于1V，高电平信号大于4V），
      //  数据信号波形如图2.1所示。图2.1中低电平脉冲宽度在250μs左右，两个脉冲间的时间间隔在2.5ms左右。
    }

    public static void DelayIo(int delay){
        for(int i=0; i< delay; i++){
            //DATA0
            HwitManager.HwitSetIOValue(17, 1);
            //DATA1
            HwitManager.HwitSetIOValue(16, 1);
        }
    }
    public static void SetWG0(int ticks)
    {
        for(int i=0; i<ticks; i++) {
            //DATA1
            HwitManager.HwitSetIOValue(16, 1);
            //DATA0
            HwitManager.HwitSetIOValue(17, 0);
        }

    }

    public static void SetWG1(int ticks)
    {
        for(int i=0; i<ticks; i++) {
            //DATA0
            HwitManager.HwitSetIOValue(17, 1);
            //DATA1
            HwitManager.HwitSetIOValue(16, 0);
        }
    }

    public static void SetWGx(int ticks)
    {
        for(int i=0; i<ticks; i++) {
            //DATA0
            HwitManager.HwitSetIOValue(17, 1);
            //DATA1
            HwitManager.HwitSetIOValue(16, 1);
        }
    }

    //韦根26输出格式：
    //bit0为bit1~bit12的偶校验
    //bit1~bit24为3字节卡号信息
    //bit25为bit13~bit24的奇校验
    public static String GetWG26Bits(int n){
        String num = Integer.toBinaryString(n);
        StringBuilder sb = new StringBuilder("");
        boolean evnt = false;
        boolean odd = false;
        byte bitcount = 0;

        if(num.length() < 32) {
            for(int i =0;i < 32 - num.length(); i ++){
                sb.append("0");
            }
            num = sb.toString()+ num ;
        }

        // 3字节卡号信息
        num = num.substring(8,32);

        char bitchars[] = num.toCharArray();

        //计算bit0~bit11的偶校验
        for (int i = 0; i < 12; i++) {
            if(bitchars[i] =='1') {
                bitcount++;
            }
        }

        if((bitcount % 2)==0) {
            evnt = false;
        }else {
            evnt = true;
        }

        if(evnt) {
            num ="1"+num;
        }else {
            num ="0"+num;
        }

        bitcount = 0;
        //bit12~bit23的奇校验
        for (int i = 12; i < 24; i++) {
            if(bitchars[i] =='1') {
                bitcount++;
            }
        }

        if((bitcount % 2)==0) {
            odd = true ;
        }else {
            odd = false;
        }

        if(odd) {
            num =num+"1";
        }else {
            num =num+"0";
        }

        System.out.println("WG26 Bites: "+num);

        return num;
    }

    public static void WG26Write(int id)
    {
        String strWG26 = GetWG26Bits(id);

        char[]  arrayWG26 = strWG26.toCharArray();

        //DATA0/DATA1 SET 1
        SetWGx(2);

        //传输WG26BIT
        for(int i=0; i<26;i++){

            SetWGx(2);

            if(arrayWG26[i]=='1'){
                SetWG1(1);
            }else {
                SetWG0(1);
            }
        }

        //DATA0/DATA1 SET 1
        SetWGx(2);

    }
}
