package com.thdtek.acs.terminal.bean;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Unique;

import java.math.BigDecimal;

/**
 * Time:2018/7/2
 * User:lizhen
 * Description:
 */

@Entity
public class PersonBean {
    @Id(autoincrement = true)
    private Long id;//必须和auth_id相同,修改id的set/get方法
    private long person_id;
    private double person_ts;//最后一次修改的时间
    private long auth_id;
    private double auth_ts;
    @SerializedName("photo")
    private String facePic;
    private String oldFacePic;
    private String name;
    @SerializedName("ID_NO")
    private String iD_no;
    @SerializedName("IC_NO")
    private String employee_card_id;
    @SerializedName("startTs")
    private double start_ts;
    @SerializedName("endTs")
    private double end_ts;
    @SerializedName("passCount")
    private long count;
    private String now_pic;
    @Unique
    private String fid;//http使用，是所有http操作的id
    private String personalizedPermissions;
    private String weekly;


    public PersonBean(Long auth_id, long person_id, String facePic, String name, String employee_card_id) {
        //必须是权限id,用于识别员工,登记访客,未登记访客,不能改成id
        this.auth_id = auth_id;
        this.person_id = person_id;
        this.facePic = facePic;
        this.name = name;
        this.employee_card_id = employee_card_id;
    }


    public String getWeekly() {
        return this.weekly;
    }


    public void setWeekly(String weekly) {
        this.weekly = weekly;
    }


    public String getPersonalizedPermissions() {
        return this.personalizedPermissions;
    }


    public void setPersonalizedPermissions(String personalizedPermissions) {
        this.personalizedPermissions = personalizedPermissions;
    }


    public String getFid() {
        return this.fid;
    }


    public void setFid(String fid) {
        this.fid = fid;
    }


    public String getNow_pic() {
        return this.now_pic;
    }


    public void setNow_pic(String now_pic) {
        this.now_pic = now_pic;
    }


    public long getCount() {
        return this.count;
    }


    public void setCount(long count) {
        this.count = count;
    }


    public double getEnd_ts() {
        return this.end_ts;
    }


    public void setEnd_ts(double end_ts) {
        this.end_ts = end_ts;
    }


    public double getStart_ts() {
        return this.start_ts;
    }


    public void setStart_ts(double start_ts) {
        this.start_ts = start_ts;
    }


    public String getEmployee_card_id() {
        return this.employee_card_id;
    }


    public void setEmployee_card_id(String employee_card_id) {
        this.employee_card_id = employee_card_id;
    }


    public String getID_no() {
        return this.iD_no;
    }


    public void setID_no(String iD_no) {
        this.iD_no = iD_no;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getOldFacePic() {
        return this.oldFacePic;
    }


    public void setOldFacePic(String oldFacePic) {
        this.oldFacePic = oldFacePic;
    }


    public String getFacePic() {
        return this.facePic;
    }


    public void setFacePic(String facePic) {
        this.facePic = facePic;
    }


    public double getAuth_ts() {
        return this.auth_ts;
    }


    public void setAuth_ts(double auth_ts) {
        this.auth_ts = auth_ts;
    }


    public long getAuth_id() {
        return this.auth_id;
    }


    public void setAuth_id(long auth_id) {
        this.auth_id = auth_id;
    }


    public double getPerson_ts() {
        return this.person_ts;
    }


    public void setPerson_ts(double person_ts) {
        this.person_ts = person_ts;
    }


    public long getPerson_id() {
        return this.person_id;
    }


    public void setPerson_id(long person_id) {
        this.person_id = person_id;
    }


    public Long getId() {
        return this.auth_id;
    }


    public void setId(Long id) {
        this.auth_id = id;
    }


    @Generated(hash = 190628030)
    public PersonBean(Long id, long person_id, double person_ts, long auth_id, double auth_ts, String facePic,
            String oldFacePic, String name, String iD_no, String employee_card_id, double start_ts,
            double end_ts, long count, String now_pic, String fid, String personalizedPermissions, String weekly) {
        this.id = id;
        this.person_id = person_id;
        this.person_ts = person_ts;
        this.auth_id = auth_id;
        this.auth_ts = auth_ts;
        this.facePic = facePic;
        this.oldFacePic = oldFacePic;
        this.name = name;
        this.iD_no = iD_no;
        this.employee_card_id = employee_card_id;
        this.start_ts = start_ts;
        this.end_ts = end_ts;
        this.count = count;
        this.now_pic = now_pic;
        this.fid = fid;
        this.personalizedPermissions = personalizedPermissions;
        this.weekly = weekly;
    }


    @Generated(hash = 836535228)
    public PersonBean() {
    }


}
