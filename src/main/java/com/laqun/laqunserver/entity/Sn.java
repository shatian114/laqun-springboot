package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "sn")
public class Sn {
    public Sn() {
    }

    public Sn(String sn) {
        this.sn = sn;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String sn = "";
    private String currentState = "";
    private String jobName = "";
    private String jobContent = "";
    private String lastHttpTime = "";
    private String appVer = "";
    private int goodWxNum = 0;
    private int badWxNum = 0;
    private String currentWx = "";
    private String remark = "";
}
