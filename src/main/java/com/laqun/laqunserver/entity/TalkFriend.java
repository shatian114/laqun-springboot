package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "talkFriend")
public class TalkFriend {
    public TalkFriend() {
    }

    public TalkFriend(String wxName){
        this.wxName = wxName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String wxName;
    private int isEnable;
    private int isClose;
}
