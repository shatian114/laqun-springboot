package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.AddWx;
import com.laqun.laqunserver.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByName(String name);
    List<Customer> findByNameContains(String name);

    @Transactional
    @Modifying
    void deleteByName(String name);

    @Modifying
    @Transactional
    @Query("update Customer  set addNum = addNum + 1, oddNum = oddNum - 1 where name = ?1")
    void updateCustomerAddNumAndOddNumByName(String name);

    @Modifying
    @Transactional
    @Query("update Customer  set addNum = addNum - 1, oddNum = oddNum + 1 where name = ?1")
    void updateCustomerAddNumAndOddNumByNameRelease(String name);

    @Modifying
    @Transactional
    @Query("update Customer set oddNum = oddNum + ?1 where name = ?2")
    void updateOddNumByName(int oddNum, String name);
}
