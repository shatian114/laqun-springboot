package com.laqun.laqunserver.entity;

import com.laqun.laqunserver.common.utils;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name = "snGroup")
public class SnGroup {
    public SnGroup() {
    }

    public SnGroup(String groupName, String groupMember) {
        this.groupName = groupName;
        this.groupMember = groupMember;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String groupName = "";
    private String groupMember = "";
    private String addTime = utils.sdf.format(new Date());
}
