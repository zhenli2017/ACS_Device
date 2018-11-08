package com.thdtek.acs.terminal.yzface.Entity;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DeviceUtil {

    /**
     * 获取手机的MAC地址
     *
     * @return
     */
    public static String getMac(int i) {
        String str = "";
        String macSerial = "";

        if (i == 0) {//WIFI
            try {
                Process pp = Runtime.getRuntime().exec(
                        "cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);

                for (; null != str; ) {
                    str = input.readLine();
                    if (str != null) {
                        macSerial = str.trim();// 去空格
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else if(i==1) { //lan

            if (macSerial == null || "".equals(macSerial)) {
                try {
                    return loadFileAsString("/sys/class/net/eth0/address")
                            .toUpperCase().substring(0, 17);
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }
        return macSerial;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }


    public static String getMacAddress() {
        String strMacAddr = null;
        try {
            // 获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
        }
        return strMacAddr;
    }

    public static byte[] getMacAddressByte() {

        byte[] b=new byte[6];
        try {
            // 获得IpD地址
            InetAddress ip = getLocalInetAddress();
           b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
        } catch (Exception e) {
        }
        return b;
    }

    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // 列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface
                    .getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {// 是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface
                        .nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

    public static  String getLocalIPAddress()
    {
        try
        {
            for(Enumeration<NetworkInterface> mEnumeration = NetworkInterface.getNetworkInterfaces(); mEnumeration.hasMoreElements();)
            {
                NetworkInterface intf = mEnumeration.nextElement();

                for(Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIPAddr.nextElement();
                    //如果不是回环地址
                    if (!inetAddress.isLoopbackAddress())
                    {
                    //直接返回本地IP地址
                    return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex)
        {
            Log.e("Error", ex.toString());
        }
        return null;
    }

    public static  byte[] getLocalIPAddressByte()
    {
        try
        {
            for(Enumeration<NetworkInterface> mEnumeration = NetworkInterface.getNetworkInterfaces(); mEnumeration.hasMoreElements();)
            {
                NetworkInterface intf = mEnumeration.nextElement();

                for(Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIPAddr.nextElement();
                    //如果不是回环地址
                    if ( !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() )
                    {
                        //直接返回本地IP地址
                        String strMac = inetAddress.getHostAddress().toString();
                        byte[]IP = new byte[4];

                        String[] strings = strMac.split("\\.");
                        for(int i=0; i<strings.length;i++){
                            IP[i] = (byte)Integer.parseInt(strings[i]);
                        }


                        return IP;
                    }
                }
            }
        }
        catch (SocketException ex)
        {
            Log.e("Error", ex.toString());
        }
        return null;
    }
}
