package com.pawtner.WithPet.global.exception;

import com.pawtner.WithPet.domain.chat.exception.ChatRoomAccessDeniedException;
import com.pawtner.WithPet.domain.chat.exception.ChatRoomNotFoundException;
import com.pawtner.WithPet.domain.search.exception.InvalidPostTypeException;
import com.pawtner.WithPet.global.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
@Controller
public class StompExceptionHandler {

    private static final String PATH = "STOMP"; // REST에서의 request.getRequestURI() 대체

    // 채팅방 접근 권한 없음 (참가자가 아닌 사용자가 메시지 전송/읽음 처리 시도)
    @MessageExceptionHandler(ChatRoomAccessDeniedException.class)
    @SendToUser("/queue/errors")
    public ApiErrorResponse handleChatRoomAccessDenied(ChatRoomAccessDeniedException e) {
        log.warn("[STOMP] ChatRoomAccessDeniedException: {}", e.getMessage());
        return ApiErrorResponse.of(403, PATH, "CHAT_ROOM_ACCESS_DENIED", e.getMessage());
    }

    // 채팅방을 찾을 수 없음
    @MessageExceptionHandler(ChatRoomNotFoundException.class)
    @SendToUser("/queue/errors")
    public ApiErrorResponse handleChatRoomNotFound(ChatRoomNotFoundException e) {
        log.warn("[STOMP] ChatRoomNotFoundException: {}", e.getMessage());
        return ApiErrorResponse.of(404, PATH, "CHAT_ROOM_NOT_FOUND", e.getMessage());
    }

    // 사용자를 찾을 수 없음
    @MessageExceptionHandler(MemberNotFoundException.class)
    @SendToUser("/queue/errors")
    public ApiErrorResponse handleMemberNotFound(MemberNotFoundException e) {
        log.warn("[STOMP] MemberNotFoundException: {}", e.getMessage());
        return ApiErrorResponse.of(404, PATH, "MEMBER_NOT_FOUND", e.getMessage());
    }

    // 잘못된 postType
    @MessageExceptionHandler(InvalidPostTypeException.class)
    @SendToUser("/queue/errors")
    public ApiErrorResponse handleInvalidPostType(InvalidPostTypeException e) {
        log.warn("[STOMP] InvalidPostTypeException: {}", e.getMessage());
        return ApiErrorResponse.of(400, PATH, "INVALID_POST_TYPE", e.getMessage());
    }

    // 그 외 STOMP 처리 중의 모든 예외
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ApiErrorResponse handleOtherExceptions(Exception e) {
        log.error("[STOMP] Unexpected Exception", e);
        return ApiErrorResponse.of(500, PATH, "INTERNAL_ERROR", "알 수 없는 오류가 발생했습니다.");
    }
}
