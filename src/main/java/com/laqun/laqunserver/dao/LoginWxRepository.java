package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.LoginWx;
import com.laqun.laqunserver.entity.Sn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoginWxRepository extends JpaRepository<LoginWx, Long> {
    List<LoginWx> findByWxNameContains(String wxName);

    LoginWx findByWxidAndSn(String wxid, String sn);

    @Transactional
    @Modifying
    List<LoginWx> deleteByState(String state);
    List<LoginWx> findByStateContains(String state);

    @Modifying
    @Transactional
    List<LoginWx> deleteByWxid(String wxid);

    @Modifying
    @Transactional
    @Query("update LoginWx set addNum = addNum + 1 where wxid = ?1")
    List<LoginWx> updateLoginWxAddNumBywxid(String wxid);

    List<LoginWx> findBySn(String sn);

    @Modifying
    @Transactional
    @Query(value = "select all from LoginWx where (state='正在登录' and sn=?1) or sn=''", nativeQuery = true)
    LoginWx getLoginWxesBySn(String sn);

    LoginWx findOneByWxid(String wxid);

    LoginWx findOneByWxNameAndWxid(String wxName, String wxid);


}
