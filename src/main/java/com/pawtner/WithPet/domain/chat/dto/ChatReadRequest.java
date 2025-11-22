package com.pawtner.WithPet.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatReadRequest {

    private Long roomId;   // 어떤 방에서
    private Long ownerId;  // 누가(read를 수행한 사용자)
}
