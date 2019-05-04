package com.laqun.laqunserver.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "jobStopLog")
public class JobStopLog {
    public JobStopLog() {
    }

    public JobStopLog(String sn, String jobName, String stopContent){
        this.sn = sn;
        this.jobName = jobName;
        this.stopContent = stopContent;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String sn;
    private String stopContent;
    private String jobName;
    private String jobContent;
}
