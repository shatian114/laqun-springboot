package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.Resource;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Transactional
    @Modifying
    void deleteByType(String type);

    @Modifying
    @Transactional
    void deleteByTypeAndVal(String type, String val);
    List<Resource> findByType(String type);
    List<Resource> findByType(String type, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "select * from resource where type = ?1 order by rand() limit ?2", nativeQuery = true)
    List<Resource> findResourcesByType(String type, int limitNum);
}
