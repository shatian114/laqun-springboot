package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "loginFriendChange")
public class LoginWxFriendChange {
    public LoginWxFriendChange() {
    }

    public LoginWxFriendChange(String wxid, int friendNum, int changeTime) {
        this.wxid = wxid;
        this.friendNum = friendNum;
        this.changeTime = changeTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String wxid = "";
    private int friendNum = 0;
    private int changeTime = 0;
}
