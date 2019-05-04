package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.IpConf;
import com.laqun.laqunserver.entity.Sn;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IpConfRepository extends JpaRepository<IpConf, Long> {
    List<IpConf> findByIpAddrContains(String ipAddr);

    @Modifying
    @Transactional
    List<IpConf> deleteByIpAddr(String ipAddr);

    List<IpConf> findIpConfBy(Pageable pageable);

    @Modifying
    @Transactional
    @Query("update IpConf set useNum = useNum + 1, lastUseTime = ?1 where ipAddr = ?2")
    List<IpConf> updateIpConfUseNumAndLastUseTimeByIpAddr(long lastUseTime, String ipAddr);
}
