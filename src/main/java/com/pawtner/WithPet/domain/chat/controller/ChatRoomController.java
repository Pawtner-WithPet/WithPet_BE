package com.pawtner.WithPet.domain.chat.controller;

import com.pawtner.WithPet.domain.chat.dto.ChatRoomListItemDto;
import com.pawtner.WithPet.domain.chat.service.ChatQueryService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatQueryService chatQueryService;

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomListItemDto>>> getMyChatRooms(
            @RequestParam("ownerId") Long ownerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Page<ChatRoomListItemDto> chatRooms = chatQueryService.getMyChatRooms(ownerId, page, size);

        return ResponseEntity.ok(ApiResponse.success("SUCCESS_GET_CHATROOMS", "채팅방 목록을 성공적으로 조회했습니다.", chatRooms.getContent())); //우선 페이지네이션x, content만 반환
    }
}
