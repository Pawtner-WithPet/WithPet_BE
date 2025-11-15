package com.pawtner.WithPet.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomListItemDto {
    // 채팅방 정보
    private Long roomId;

    // 상대 유저 정보
    private Long opponentId;
    private String opponentNickname;
    private String opponentProfileImg;

    // 최근 채팅 정보
    private String lastMessageContent;
    private LocalDateTime lastMessageAt;

    // 읽지 않은 메시지 개수
    private Long unreadCount;
}
