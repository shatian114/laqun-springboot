package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "backImg")
public class BackImg {
    public BackImg() {
    }

    public BackImg(String val){
        this.val = val;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String val;
}
