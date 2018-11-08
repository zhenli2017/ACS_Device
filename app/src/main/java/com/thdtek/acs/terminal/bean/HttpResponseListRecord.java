package com.thdtek.acs.terminal.bean;

import java.util.List;

/**
 * Time:2018/10/26
 * User:lizhen
 * Description:
 */

public class HttpResponseListRecord {


    /**
     * msg : 鎴愬姛
     * status : 0
     * data : {"record":[{"passType":0,"ts":1540538568660,"personID":"00000001"},{"passType":0,"ts":1540538572894,"personID":"00000001"},{"passType":0,"ts":1540539071281,"personID":"00000001"},{"passType":0,"ts":1540539073935,"personID":"00000001"},{"passType":0,"ts":1540539076433,"personID":"00000001"},{"passType":0,"ts":1540539099230,"personID":"00000001"},{"passType":0,"ts":1540539102782,"personID":"00000001"},{"passType":0,"ts":1540539111102,"personID":"00000001"},{"passType":0,"ts":1540539112656,"personID":"00000001"},{"passType":0,"ts":1540539135267,"personID":"00000001"}]}
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
             * passType : 0
             * ts : 1540538568660
             * personID : 00000001
             */

            private int passType;
            private long ts;
            private String personID;
            private String name;

            public int getPassType() {
                return passType;
            }

            public void setPassType(int passType) {
                this.passType = passType;
            }

            public long getTs() {
                return ts;
            }

            public void setTs(long ts) {
                this.ts = ts;
            }

            public String getPersonID() {
                return personID;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            @Override
            public String toString() {
                return "RecordBean{" +
                        "passType=" + passType +
                        ", ts=" + ts +
                        ", personID='" + personID + '\'' +
                        ", name='" + name + '\'' +
                        '}';
            }

            public void setPersonID(String personID) {

                this.personID = personID;
            }
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "record=" + record +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "HttpResponseListRecord{" +
                "msg='" + msg + '\'' +
                ", status=" + status +
                ", data=" + data +
                '}';
    }
}
