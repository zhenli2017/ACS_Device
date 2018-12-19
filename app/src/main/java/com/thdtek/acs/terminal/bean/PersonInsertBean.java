package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/11/14
 * User:lizhen
 * Description:
 */

public class PersonInsertBean {

    /**
     * msg : 成功
     * code : 0
     * data : {"personnel":{"personnel_gender":0,"personnel_name":"李圳","personnel_id_number":"440307199207122835","personnel_id":339,"personnel_phonenumber":"66666611111"}}
     */

    private String msg;
    private int code;
    private DataBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * personnel : {"personnel_gender":0,"personnel_name":"李圳","personnel_id_number":"440307199207122835","personnel_id":339,"personnel_phonenumber":"66666611111"}
         */

        private PersonnelBean personnel;

        public PersonnelBean getPersonnel() {
            return personnel;
        }

        public void setPersonnel(PersonnelBean personnel) {
            this.personnel = personnel;
        }

        public static class PersonnelBean {
            /**
             * personnel_gender : 0
             * personnel_name : 李圳
             * personnel_id_number : 440307199207122835
             * personnel_id : 339
             * personnel_phonenumber : 66666611111
             */

            private int personnel_gender;
            private String personnel_name;
            private String personnel_id_number;
            private int personnel_id;
            private String personnel_phonenumber;

            public int getPersonnel_gender() {
                return personnel_gender;
            }

            public void setPersonnel_gender(int personnel_gender) {
                this.personnel_gender = personnel_gender;
            }

            public String getPersonnel_name() {
                return personnel_name;
            }

            public void setPersonnel_name(String personnel_name) {
                this.personnel_name = personnel_name;
            }

            public String getPersonnel_id_number() {
                return personnel_id_number;
            }

            public void setPersonnel_id_number(String personnel_id_number) {
                this.personnel_id_number = personnel_id_number;
            }

            public int getPersonnel_id() {
                return personnel_id;
            }

            public void setPersonnel_id(int personnel_id) {
                this.personnel_id = personnel_id;
            }

            public String getPersonnel_phonenumber() {
                return personnel_phonenumber;
            }

            public void setPersonnel_phonenumber(String personnel_phonenumber) {
                this.personnel_phonenumber = personnel_phonenumber;
            }
        }
    }
}
