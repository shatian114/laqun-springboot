package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "addWx")
public class AddWx {
    public AddWx() {
    }

    public AddWx(String phone, int priority, String customer){
        this.phone = phone;
        this.priority = priority;
        this.customer = customer;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String phone = "";
    private String wxid = "";
    private String nick = "";
    private int sex = 0;
    private String province = "";
    private String city = "";
    private int isLa = 0;
    private String laTime = "";
    private String laQunId = "";
    private int priority = 0;
    private String customer = "";
    private int isUse = 0;
    private String avatar = "";
    private int isFriend = 0;
    private String loginWx = "";
}
