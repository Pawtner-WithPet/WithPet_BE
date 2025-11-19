package com.pawtner.WithPet.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomDetailResponse {

    private Long roomId;

    // 상대 정보
    private Long opponentId;
    private String opponentNickname;
    private String opponentProfileImg;

    // 시작 게시글 정보
    private String originPostType;   // "LOST" or "FOUND"
    private Long originPostId;

    // 메시지 목록
    private List<ChatMessageDto> messages;
}
