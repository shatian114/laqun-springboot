package com.laqun.laqunserver.entity;

import com.laqun.laqunserver.common.utils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "news")
public class News {
    public News() {
    }

    public News(String newsName, String newsUrl) {
        this.newsName = newsName;
        this.newsUrl = newsUrl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String newsName = "";
    private String newsUrl = "";
    private String addTime = utils.sdf.format(new Date());
}
