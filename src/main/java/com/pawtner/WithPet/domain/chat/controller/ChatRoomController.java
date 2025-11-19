package com.pawtner.WithPet.domain.chat.controller;

import com.pawtner.WithPet.domain.chat.dto.ChatRoomDetailResponse;
import com.pawtner.WithPet.domain.chat.dto.ChatRoomListItemDto;
import com.pawtner.WithPet.domain.chat.dto.ChatRoomOriginPostResponse;
import com.pawtner.WithPet.domain.chat.dto.EnterChatFromPostRequest;
import com.pawtner.WithPet.domain.chat.service.ChatQueryService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatQueryService chatQueryService;

    // [ 채팅방 목록 조회 ]
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatRoomListItemDto>>> getMyChatRooms(
            @RequestParam("ownerId") Long ownerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Page<ChatRoomListItemDto> chatRooms = chatQueryService.getMyChatRooms(ownerId, page, size);

        return ResponseEntity.ok(ApiResponse.success("SUCCESS_GET_CHATROOMS", "채팅방 목록을 성공적으로 조회했습니다.", chatRooms.getContent())); //우선 페이지네이션x, content만 반환
    }


    // [ 채팅 목록에서 방 진입 -> 채팅 내역 조회 ]
    @GetMapping("/enter")
    public ResponseEntity<ApiResponse<ChatRoomDetailResponse>> enterExistingRoom(
            @RequestParam("ownerId") Long ownerId,
            @RequestParam("roomId") Long roomId
    ) {
        ChatRoomDetailResponse response = chatQueryService.enterExistingRoom(ownerId, roomId);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS_ENTER_CHATROOM", "채팅방에 성공적으로 진입했습니다.", response));
    }


    // [ 게시글에서 방 진입 -> 생성 or 기존 방의 채팅 내역 조회 ]
    @PostMapping("/enter-from-post")
    public ResponseEntity<ApiResponse<ChatRoomDetailResponse>> enterFromPost(
            @RequestBody EnterChatFromPostRequest request
    ) {
        ChatRoomDetailResponse response = chatQueryService.enterFromPost(request);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS_ENTER_CHATROOM", "게시글에서 채팅방에 성공적으로 진입했습니다.", response));
    }

    // [ 채팅 상세글 이동 (채팅 시작한 게시글 id 반환) ]
    @GetMapping("/origin-post")
    public ResponseEntity<ApiResponse<ChatRoomOriginPostResponse>> getOriginPost(
            @RequestParam("ownerId") Long ownerId,
            @RequestParam("roomId") Long roomId
    ) {
        ChatRoomOriginPostResponse response = chatQueryService.getOriginPost(ownerId, roomId);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS_GET_CHATROOM_ORIGIN", "채팅방의 상세 게시글 정보를 성공적으로 조회했습니다.", response));
    }
}
