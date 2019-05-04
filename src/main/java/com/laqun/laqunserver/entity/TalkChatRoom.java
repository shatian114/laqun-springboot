package com.laqun.laqunserver.entity;

import com.laqun.laqunserver.common.utils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "talkChatRoom")
public class TalkChatRoom {
    public TalkChatRoom() {
    }

    public TalkChatRoom(String qunQr){
        this.qunQr = qunQr;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String qunQr = "";
    private String qunid = "";
    private String nick = "";
    private int friendNum = 0;
    private int isClose = 0;
    private String addTime = utils.sdf.format(new Date());
}
