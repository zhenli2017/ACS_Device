package com.sdses;

import java.util.HashMap;
import java.util.Iterator;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

public class UsbHidPort 
{
	private static final String ACTION_USB_PERMISSION = "com.sdses.JniCommonInterface.USB_PERMISSION";
    
    private static final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
    {
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();
            if (action.equals(ACTION_USB_PERMISSION)) 
            {
                synchronized (this) 
                {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) 
                    {
                        if (device != null) 
                        {
                            
                        }
                    } 
                    else 
                    {
                        
                    }
                }
            } 
        }
    };
    
    public static UsbManager usbManager = null;
    public static UsbDevice usbDevice = null;
    public static UsbInterface usbInterface = null;
    public static UsbDeviceConnection usbConnection = null;
    public static UsbEndpoint epOut;
    public static UsbEndpoint epIn;
    public static int SendBlockSize = 0;
    public static int RecvBlockSize = 0;
    
	public static synchronized long GetUsbPermission(Context ctx, int Vid, int Pid)
	{
		usbManager = (UsbManager)ctx.getSystemService(Context.USB_SERVICE);    	
    	HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                
        while (deviceIterator.hasNext())
        {
            UsbDevice device = deviceIterator.next();   
            if ((device.getVendorId() == Vid) && (device.getProductId() == Pid)){
            	usbDevice = device;
                break;
            }
        }
        
        if (usbDevice == null) 
        {
            return -1;
        }
        
        if(usbDevice.getInterfaceCount() <= 0)
        {
        	return -2;
        }
        
        usbInterface = usbDevice.getInterface(0);
		
        // 判断是否有权限
        if(!usbManager.hasPermission(usbDevice))
        {
        	PendingIntent mPermissionIntent = PendingIntent.getBroadcast(ctx, 0, new Intent(ACTION_USB_PERMISSION), 0);
			IntentFilter permissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
			ctx.registerReceiver(mUsbReceiver, permissionFilter);
			usbManager.requestPermission(usbDevice, mPermissionIntent);
    		System.out.print("Requesting Usb Permission");
    		while(!usbManager.hasPermission(usbDevice))
    		{
    			
    		}
        }  	

		return 0;
	}
	
	public static long UsbOpenPort(int Vid, int Pid)
	{
		usbConnection = usbManager.openDevice(usbDevice);
		if(usbConnection == null)
		{
			return -1;			
		}
		
		if(!usbConnection.claimInterface(usbInterface, true))
		{
			usbConnection.close();
			usbConnection = null;
			return -2;
		}
		
		for(int i = 0; i < usbInterface.getEndpointCount(); i++)
		{
			UsbEndpoint endPoint = usbInterface.getEndpoint(i); 
			switch(endPoint.getType())
			{
				case UsbConstants.USB_ENDPOINT_XFER_INT:
				{
					switch(endPoint.getDirection())
					{
						case UsbConstants.USB_DIR_OUT:
						{
							epOut = endPoint;
							break;
						}
						case UsbConstants.USB_DIR_IN:
						{
							epIn = endPoint;
							break;
						}
					}
					break;
				}
				case UsbConstants.USB_ENDPOINT_XFER_CONTROL:
				{
					epOut = endPoint;
					epIn = endPoint;
					break;
				}
			}
		}
		
		SendBlockSize = epOut.getMaxPacketSize();
		RecvBlockSize = epIn.getMaxPacketSize();
		
		return SendBlockSize;
	}
	
	public static long UsbWrite(byte[] buffer, int len, int timeout)
	{		
		return usbConnection.bulkTransfer(epOut, buffer, SendBlockSize, timeout);
	}
	
	public static long UsbRead(byte[] buffer, int timeout)
	{	
		/*
		try 
		{
			Thread.sleep(10);
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		return usbConnection.bulkTransfer(epIn, buffer, RecvBlockSize, timeout);
	}
	
	public static long UsbCtrlWrite(byte[] buffer, int len, int timeout)
	{		
		return usbConnection.controlTransfer(0x21, 0x09, 0x0200, 0x0000, buffer, SendBlockSize, timeout);
	}
	
	public static long UsbCtrlRead(byte[] buffer, int timeout)
	{		
		try 
		{
			Thread.sleep(10);
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return usbConnection.controlTransfer(0xA1, 0x01, 0x0100, 0x0000, buffer, RecvBlockSize, timeout);
	}
	
	public static void UsbClosePort()
	{
		usbConnection.releaseInterface(usbInterface);
		usbConnection.close();		
	}
}
