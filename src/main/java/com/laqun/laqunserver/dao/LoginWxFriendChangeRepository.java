package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.LoginWxFriendChange;
import com.laqun.laqunserver.entity.Sn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoginWxFriendChangeRepository extends JpaRepository<LoginWxFriendChange, Long> {


}
