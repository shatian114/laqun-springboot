package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.JobStopLog;
import com.laqun.laqunserver.entity.Sn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JobStopLogRepository extends JpaRepository<JobStopLog, Long> {


}
