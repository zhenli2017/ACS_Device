package com.thdtek.acs.terminal.yzface.Message;

import java.util.ArrayList;
import java.util.List;

public class DataFile {
    public  DataPreparewriteFile fileHead = new DataPreparewriteFile();
    public  byte[] fileHandle  = new byte[4];
    public  byte[] crc32chunks = new byte[4];
    public List<DataFileChunk>  chunks = new ArrayList<DataFileChunk>();
}
