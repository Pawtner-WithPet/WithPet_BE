package com.pawtner.WithPet.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomOriginPostResponse {

    private Long postId;      // 게시글 ID
    private String postType;  // "LOST" or "FOUND"
}
