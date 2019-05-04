package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.Sn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SnRepository extends JpaRepository<Sn, Long> {
    List<Sn> findBySnContains(String sn);

    Sn findBySn(String sn);

    @Modifying
    @Transactional
    void deleteBySn(String sn);

    @Modifying
    @Transactional
    @Query(value = "update Sn set jobName = ?1, jobContent = ?2 where jobName='任务已停止' and sn in ?3")
    void setSnByJobNameAndSnIn(String jobName, String jobContent, String[] snArr);

    @Modifying
    @Transactional
    @Query("update Sn set lastHttpTime = ?1 where sn = ?2")
    void updateSnLastHttpTimeBySn(String lastHttpTime, String sn);

}
