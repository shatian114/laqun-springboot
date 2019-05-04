package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.Sn;
import com.laqun.laqunserver.entity.SnGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SnGroupRepository extends JpaRepository<SnGroup, Long> {
    List<SnGroup> findByGroupNameContains(String name);
    SnGroup getSnGroupById(long id);

    @Modifying
    @Transactional
    void deleteByGroupName(String groupName);
}
