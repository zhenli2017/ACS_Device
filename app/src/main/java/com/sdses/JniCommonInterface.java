package com.sdses;

import android.content.Context;

public class JniCommonInterface 
{
	static 
	{
        System.loadLibrary("wlt2bmp");
        System.loadLibrary("PortCommunication");
        System.loadLibrary("TerminalProtocol");
        System.loadLibrary("CommonInterface");
    }
	
	public static long GetUsbPermission(Context ctx, int Vid, int Pid)
	{
		return UsbHidPort.GetUsbPermission(ctx, Vid, Pid);
	}
	
	public static native long OpenDevice(String PortType, String PortPara, String ExtendPara);
	public static native long SetCurrentDevice(long DevHandle);
	public static native long GetCurrentDevice();
	public static native long CloseDevice();
	public static native long TerminalGetModel(byte[] TerminalModel);
	public static native long TerminalHeartBeat();
	
	public static native long IdFindCard();
	public static native long IdSelectCard();
	public static native long IdReadBaseMsg(byte[] pucCHMsg, long[] puiCHMsgLen, byte[] pucPHMsg, long[] puiPHMsgLen);
	public static native long IdReadBaseFpMsg(byte[] pucCHMsg, long[] puiCHMsgLen, byte[] pucPHMsg, long[] puiPHMsgLen, byte[] pucFPMsg, long[] puiFPMsgLen);
	public static native long IdReadNewAppMsg(byte[] pucAppMsg, long[] puiAppMsgLen);
	public static native long SdtFindCard(byte[] pucManaInfo);
	public static native long SdtSelectCard(byte[] pucManaMsg);
	public static native long SdtReadBaseMsg(byte[] pucCHMsg, long[] puiCHMsgLen, byte[] pucPHMsg, long[] puiPHMsgLen);
	public static native long SdtReadBaseFpMsg(byte[] pucCHMsg, long[] puiCHMsgLen, byte[] pucPHMsg, long[] puiPHMsgLen, byte[] pucFPMsg, long[] puiFPMsgLen);
	public static native long SdtReadNewAppMsg(byte[] pucAppMsg, long[] puiAppMsgLen);
	public static native long SamGetStatus();
	public static native long SamGetId(byte[] SamId, long[] SamIdLen);
	public static native long SamGetIdStr(byte[] SamIdStr);
	public static native long SdtSamGetStatus();
	public static native long SdtSamGetId(byte[] SamId, long[] SamIdLen);
	public static native long SdtSamGetIdStr(byte[] SamIdStr);
	
	public static native long IdReadCard(byte CardType, byte InfoEncoding, byte[] IdCardInfo, long TimeOutMs);
	public static native long SdtReadCard(byte CardType, byte InfoEncoding, byte[] IdCardInfo, long TimeOutMs);
	public static native long IdReadNewAddress(byte[] NewAddress);
	public static native long SdtReadNewAddress(byte[] NewAddress);
	public static native long IdCardGetName(byte[] Name);
	public static native long IdCardGetNameEn(byte[] NameEn);
	public static native long IdCardGetGender(byte[] Gender);
	public static native long IdCardGetGenderId(byte[] GenderId);
	public static native long IdCardGetNation(byte[] Nation);
	public static native long IdCardGetNationId(byte[] NationId);
	public static native long IdCardGetBirthDate(byte[] BirthDate);
	public static native long IdCardGetAddress(byte[] Address);
	public static native long IdCardGetIdNumber(byte[] IdNumber);
	public static native long IdCardGetSignOrgan(byte[] SignOrgan);
	public static native long IdCardGetBeginTerm(byte[] BeginTerm);
	public static native long IdCardGetValidTerm(byte[] ValidTerm);
	public static native long IdCardGetFPBuffer(byte[] FPBuffer, long[] FPBufferLen);
	public static native long IdCardGetPhotoFile(String PhotoFile);
	public static native long IdCardGetPhotoBuffer(byte WltBmpJpg, byte[] PhotoBuffer, long[] PhotoBufferLen);
	
	public static native long MagRead(byte Tracks, byte[] TrackData1, byte[] TrackData2, byte[] TrackData3, byte TimeOutSec);
	public static native long MagWrite(byte Tracks, String TrackData1, String TrackData2, String TrackData3, byte TimeOutSec);
	
	public static native long M1FindCard(byte[] UID, long[] UIDLen);
	public static native long M1Authentication(byte KeyType, byte SecAddr, byte[] Key, byte[] UID);
	public static native long M1ReadBlock(byte BlockAddr, byte[] BlockData, long[] BlockDataLen);
	public static native long M1WriteBlock(byte BlockAddr, long BlockDataLen, byte[] BlockData);
	public static native long M1Halt();
	
	public static native long FpCapFeature(byte[] Feature, long[] FeatureLen);
	public static native long FpMatchFeature(long FeatureLen1, byte[] Feature1, long FeatureLen2, byte[] Feature2, long[] Score);
	
	public static native long SsseReadCard(int iType, byte[] SSCardInfo, byte[] SSErrorInfo);
	public static native long SsseGetCardInfo(String Tag, byte[] SSCardInfo);
		
	public static native long CpuPowerOn(byte Slot, byte[] ATRS, long[] ATRSLen);
	public static native long CpuApdu(byte Slot, long SendApduLen, byte[] SendApdu, byte[] RecvApdu, long[] RecvApduLen);
	public static native long CpuPowerOff(byte Slot);
	
	public static native long IccGetCardInfo(int ICtype, String AIDList, String TagList, byte[] IcCardInfo);
	public static native long IccGetARQC(int ICtype, String trData, String AIDList, byte[] ARQC, byte[] trAppData);
	public static native long IccARPCExeScript(int ICtype, String trData, String ARPC, String trAppData, byte[] ScriptResult, byte[] TC);
	public static native long IccGetTrDetail(int ICtype, String AIDList, byte[] TrDetail);
	public static native long IccGetLoadDetail(int ICtype, String AIDList, byte[] LoadDetail);	
	
	public static native long HexToAsc(byte[] Hex, long HexLength, byte[] Asc);
	public static native long AscToHex(String Asc, long HexLength, byte[] Hex);
}
