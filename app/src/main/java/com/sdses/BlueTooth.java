package com.sdses;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BlueTooth 
{
	private static UUID SS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static BluetoothAdapter mAdapter = null;
	private static BluetoothDevice mDevice = null;
	private static BluetoothSocket mSocket = null; 
	private static InputStream  mInputStream = null;
	private static OutputStream mOutputStream = null;
	
	public static long BthConnect(String BlueToothName)
	{
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mAdapter == null) 
		{
			return -1;
		}
		
		if (!mAdapter.isEnabled()) 
		{
			return -2;
			/*
			mAdapter.enable();
			if (!mAdapter.isEnabled()) 
			{
				return -2;
			}
			*/
		}
		
		//获得已配对的蓝牙设备列表
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();		
		if(pairedDevices.size() <= 0)
		{
			return -3;
		}
		
		ArrayList<BluetoothDevice> ALBluetoothDevice = new ArrayList<BluetoothDevice>();
		for (BluetoothDevice device : pairedDevices) 
		{
			ALBluetoothDevice.add(device);
		}
		
		//mAdapter.cancelDiscovery();
		
		//查找设备
		for(int i = 0; i < ALBluetoothDevice.size(); i++)
		{
			BluetoothDevice cDevice = (BluetoothDevice)ALBluetoothDevice.get(i);
			String theBlueToothName = cDevice.getName();
			if(theBlueToothName.indexOf(BlueToothName) < 0)
			{
				continue;
			}
			
			//开始连接设备
			mDevice = cDevice;
			try 
			{
				mSocket = mDevice.createRfcommSocketToServiceRecord(SS_UUID);
				mSocket.connect();
				mInputStream = mSocket.getInputStream();
				mOutputStream = mSocket.getOutputStream();
				return 0;
			} 
			catch (Exception e) 
			{
				return -4;
			}	
		}
		
		return -5; //没有配对设备
	}
	
	public static long BthSendSocket(byte[] buffer, int len)
	{
		if(mOutputStream == null)
		{
			return -1;
		}
		
		try 
		{
			mOutputStream.write(buffer, 0, len);
		} 
		catch (IOException e) 
		{
			return -2;
		}
				
		return len;
	}
	
	public static long BthRecvSocket(byte[] buffer)
	{
		if(mInputStream == null)
		{
			return -1;
		}

		try 
		{
//			int nAvailable = mInputStream.available();
//			if(nAvailable <= 0)
//			{
//				return 0;
//			}
			
			return mInputStream.read(buffer, 0, 4096);
		} 
		catch (IOException e) 
		{
			return -2;
		}
	}

	public static void BthDisconnect()
	{
		if(mInputStream != null)
		{
			try 
			{
				mInputStream.close();
				mInputStream = null;
			} 
			catch (IOException e) 
			{
				return;
			}
		}
		
		if(mOutputStream != null)
		{
			try 
			{
				mOutputStream.close();
				mOutputStream = null;
			} 
			catch (IOException e) 
			{
				return;
			}
		}
		
		if(mSocket != null)
		{
			try 
			{
				mSocket.close();
				mSocket = null;
			} 
			catch (IOException e) 
			{
				return;
			}
		}
	}
}
