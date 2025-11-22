package com.pawtner.WithPet.domain.chat.controller;

import com.pawtner.WithPet.domain.chat.dto.ChatMessageDto;
import com.pawtner.WithPet.domain.chat.dto.ChatReadRequest;
import com.pawtner.WithPet.domain.chat.dto.ChatSendRequest;
import com.pawtner.WithPet.domain.chat.service.ChatCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatCommandService chatCommandService;
    private final SimpMessagingTemplate messagingTemplate;

    // [ 메시지 전송 ]
    /**
     * 클라이언트가 /app/chat.send 로 메시지를 보내면
     * → DB에 저장하고
     * → /topic/chat.room.{roomId} 로 브로드캐스트 */
    @MessageMapping("/chat.send")
    public void handleSendMessage(ChatSendRequest request) {
        ChatMessageDto saved = chatCommandService.sendMessage(request);

        String destination = "/topic/chat.room." + request.getRoomId(); //STOMP 구독 경로
        messagingTemplate.convertAndSend(destination, saved); //해당 경로로 저장한 내용을 브로드캐스트
    }


    // [ 메시지 읽음 처리 ]
    /**
     * 읽음 처리 (클라이언트 → 서버)
     * 클라이언트는 /app/chat.read 로 send */
    @MessageMapping("/chat.read")
    public void handleReadMessage(ChatReadRequest request) {
        // 1) 내 lastReadAt 갱신
        chatCommandService.markAsRead(request);

        // 2) 상대방에게 "누가 읽었다"는 이벤트 브로드캐스트 - 프론트에서 받아서 내 메시지에 "읽음" 표시를 업데이트
        String destination = "/topic/chat.room." + request.getRoomId() + ".read";
        Long readerId = request.getOwnerId();

        messagingTemplate.convertAndSend(destination, readerId);
    }


}
