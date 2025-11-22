package com.pawtner.WithPet.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSendRequest {

    private Long roomId;
    private Long senderId;
    private String msgType; // "TEXT", "IMAGE", ...
    private String content; // TEXT면 본문, IMAGE면 이미지 URL 등
}
