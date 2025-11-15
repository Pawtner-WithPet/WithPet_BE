package com.pawtner.WithPet.domain.chat.repository;

import com.pawtner.WithPet.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방에 대한 unread 카운트
    long countByChatRoom_ChatRoomIdAndCreatedAtAfter(Long chatRoomId, LocalDateTime lastReadAt);


}
