package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.AddQun;
import com.laqun.laqunserver.entity.AddWx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddQunRepository extends JpaRepository<AddQun, Long> {
    List<AddQun> findByQunQrContains(String qunQr);

    @Modifying
    @Transactional
    void deleteByQunQr(String qunQr);

    @Modifying
    @Transactional
    @Query(value = "select all from addQun  where isBad = 0 and customer in ?1 and laNum > laedNum and ( isUse = 0 or unix_timestamp(now())-lastGetTime > ?2 ) limit 1", nativeQuery = true)
    AddQun getAdd(String[] customerArr, int qunUseTime);

    @Modifying
    @Transactional
    @Query(value = "select all from addQun where customer in ?1 and laNum > laedNum limit 1", nativeQuery = true)
    AddQun getAddQunByCustomerAndLaNumGreaterThanLaedNum(String[] customerArr);

    AddQun findOneByQunQr(String qunQr);
}
