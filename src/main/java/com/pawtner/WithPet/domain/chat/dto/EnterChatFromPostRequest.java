package com.pawtner.WithPet.domain.chat.dto;

import com.pawtner.WithPet.global.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnterChatFromPostRequest {

    private Long ownerId;      // 현재 로그인한 사용자 id
    private Long postId;       // 게시글 id
    private String postType;   // "LOST" or "FOUND"
}
