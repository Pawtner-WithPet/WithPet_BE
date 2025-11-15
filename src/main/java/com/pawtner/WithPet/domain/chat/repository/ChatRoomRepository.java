package com.pawtner.WithPet.domain.chat.repository;

import com.pawtner.WithPet.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
