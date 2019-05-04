package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "resource", uniqueConstraints = {@UniqueConstraint(name = "uniqueVal", columnNames = {"val", "type"})})
public class Resource {
    public Resource() {
    }

    public Resource(String val, String type) {
        this.val = val;
        this.type = type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String val = "";
    private String type = "";
}
