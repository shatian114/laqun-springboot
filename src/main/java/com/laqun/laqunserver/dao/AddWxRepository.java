package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.AddWx;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddWxRepository extends JpaRepository<AddWx, Long> {
    List<AddWx> findByPhoneContains(String phone);
    List<AddWx> findByCustomerAndPhoneContains(String customer, String phone);
    @Modifying
    List<AddWx> deleteByPhone(String phone);

    List<AddWx> findByIsUseOrderByPriorityAsc(int isUse, Pageable pageable);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update AddWx set isUse = ?1, loginWx = ?2 where phone = ?3")
    void updateAddWxIsUseAndLoginWxByPhone(int isUse, String loginWx, String phone);

    AddWx findOneByPhone(String phone);

    @Modifying
    @Transactional
    @Query("update AddWx set isUse = 0, loginWx = '' where phone = ?1")
    void updateAddWxIsUseAndLoginWxByPhone(String phone);

    @Modifying
    @Transactional
    @Query("update AddWx set isLa = 1, laTime = ?1, laQunId = ?2 where wxid in ?3")
    void updateAddWxIsLaAndLaTimeAndLaQunIdByWxid(String laTime, String laQunId, String[] wxidArr);

}
