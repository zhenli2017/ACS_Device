package com.thdtek.acs.terminal.server;

import com.thdtek.acs.terminal.bean.AccessRecordBean;

import java.util.List;

public class UploadRecordThreadForHttp extends Thread {

    @Override
    public void run() {
        super.run();

        RecordDaoForHttp dao = new RecordDaoForHttp();
        List<AccessRecordBean> lst = dao.queryUnUploadRecords();

        if(lst != null){
            UploadRecordForHttp uploadRecordForHttp = new UploadRecordForHttp();
            uploadRecordForHttp.upload(lst);
        }
    }
}
