package com.laqun.laqunserver.dao;

import com.laqun.laqunserver.entity.News;
import com.laqun.laqunserver.entity.TalkChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TalkChatRoomRepository extends JpaRepository<TalkChatRoom, Long> {
    List<TalkChatRoom> findByQunQrContains(String qunQr);
    TalkChatRoom findOneByQunQr(String qunQr);

    @Modifying
    @Transactional
    void deleteByQunQr(String qunQr);

    List<TalkChatRoom> findByFriendNumLessThan(int friendNum, Pageable pageable);
}
