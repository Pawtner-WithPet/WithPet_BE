package com.pawtner.WithPet.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessageDto {

    private Long messageId;

    private Long senderId;
    private boolean mine;           // 내가 보낸 메시지인지 여부

    private String msgType;         // TEXT / IMAGE / FILE / NOTICE
    private String content;         // TEXT면 본문, 나머진 "[사진]" 등 라벨

    private LocalDateTime createdAt;

    private boolean read;           // 내 메시지 기준: 상대가 읽었는지 여부
}
