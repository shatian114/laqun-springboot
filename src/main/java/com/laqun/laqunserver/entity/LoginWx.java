package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "loginWx", uniqueConstraints = {@UniqueConstraint(name = "uniqueName", columnNames = {"wxName", "wxid"})})
public class LoginWx {
    public LoginWx() {
    }

    public LoginWx(String wxName, String wxPassword, String yjInfo, String wxid, long lastGetTime) {
        this.wxName = wxName;
        this.wxPassword = wxPassword;
        this.yjInfo = yjInfo;
        this.wxid = wxid;
        this.lastGetTime = lastGetTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String wxName = "";
    private String wxPassword = "";
    private String wxid = "";
    private String avatarBase64 = "";
    private String nick = "";
    private int addNum = 0;
    private int addedNum = 0;
    private String state = "";
    private String sn = "";
    private String yjInfo = "";
    private long lastGetTime = 0;
    private String jobState = "";
    private int friendNum = 0;
}
