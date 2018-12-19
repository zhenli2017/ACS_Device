package com.thdtek.acs.terminal.bean;

import java.util.List;

/**
 * Time:2018/10/26
 * User:lizhen
 * Description:
 */

public class HttpResponseListRecord {


    /**
     * msg : 成功
     * status : 0
     * data : {"record":[{"name":"李圳","passType":0,"ts":1.54287105593E9,"personID":"00000001"},{"name":"李圳","passType":0,"ts":1.542871060055E9,"personID":"00000001"}]}
     */

    private String msg;
    private int status;
    private DataBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<RecordBean> record;

        public List<RecordBean> getRecord() {
            return record;
        }

        public void setRecord(List<RecordBean> record) {
            this.record = record;
        }

        public static class RecordBean {
            /**
             * name : 李圳
             * passType : 0
             * ts : 1.54287105593E9
             * personID : 00000001
             */

            private String name;
            private int passType;
            private double ts;
            private String personID;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getPassType() {
                return passType;
            }

            public void setPassType(int passType) {
                this.passType = passType;
            }

            public double getTs() {
                return ts;
            }

            public void setTs(double ts) {
                this.ts = ts;
            }

            public String getPersonID() {
                return personID;
            }

            public void setPersonID(String personID) {
                this.personID = personID;
            }
        }
    }
}
