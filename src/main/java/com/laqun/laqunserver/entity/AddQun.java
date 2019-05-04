package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "addQun")
public class AddQun {
    public AddQun() {
    }

    public AddQun(String qunQr, int priority, String customer, int laNum, int lastGetTime){
        this.qunQr = qunQr;
        this.priority = priority;
        this.customer = customer;
        this.laNum = laNum;
        this.lastGetTime = lastGetTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String qunQr = "";
    private String qunid = "";
    private String nick = "";
    private String sn = "";
    private int laNum = 0;
    private int laedNum = 0;
    private int laTimeout = 0;
    private int lastGetTime = 0;
    private int priority = 0;
    private String customer = "";
    private int isUse = 0;
    private int friendNum = 0;
    private int isBad = 0;
}
