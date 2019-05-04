package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByNewsNameContains(String newsName);

    @Modifying
    @Transactional
    void deleteByNewsName(String newsName);
}
