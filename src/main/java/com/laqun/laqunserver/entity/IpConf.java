package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ipConf")
public class IpConf {
    public IpConf() {
    }

    public IpConf(String ipAddr, long lastUseTime){
        this.ipAddr = ipAddr;
        this.lastUseTime = lastUseTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String ipAddr;
    private int useNum;
    private long lastUseTime;
}
