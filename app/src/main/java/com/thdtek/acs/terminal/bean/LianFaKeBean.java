package com.thdtek.acs.terminal.bean;

import java.util.List;

/**
 * Time:2018/11/15
 * User:lizhen
 * Description:
 */

public class LianFaKeBean {


    /**
     * code : 1
     * msg : 该租客验证成功
     * result : {"id":6,"idcard":"440307199207122835","phone":"13510991466","nickname":"李圳","name":"李圳","type":2,"sex":1,"del":0,"house":[{"hid":1,"tit":"嘿嘿嘿hihihi","address":"湖北省武汉市武昌区紫阳路232号"}]}
     */

    private int code;
    private String msg;
    private ResultBean result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 6
         * idcard : 440307199207122835
         * phone : 13510991466
         * nickname : 李圳
         * name : 李圳
         * type : 2
         * sex : 1
         * del : 0
         * house : [{"hid":1,"tit":"嘿嘿嘿hihihi","address":"湖北省武汉市武昌区紫阳路232号"}]
         */

        private int id;
        private String idcard;
        private String phone;
        private String nickname;
        private String name;
        private int type;
        private int sex;
        private int del;
        private List<HouseBean> house;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIdcard() {
            return idcard;
        }

        public void setIdcard(String idcard) {
            this.idcard = idcard;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getDel() {
            return del;
        }

        public void setDel(int del) {
            this.del = del;
        }

        public List<HouseBean> getHouse() {
            return house;
        }

        public void setHouse(List<HouseBean> house) {
            this.house = house;
        }

        public static class HouseBean {
            /**
             * hid : 1
             * tit : 嘿嘿嘿hihihi
             * address : 湖北省武汉市武昌区紫阳路232号
             */

            private int hid;
            private String tit;
            private String address;

            public int getHid() {
                return hid;
            }

            public void setHid(int hid) {
                this.hid = hid;
            }

            public String getTit() {
                return tit;
            }

            public void setTit(String tit) {
                this.tit = tit;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }
    }
}
