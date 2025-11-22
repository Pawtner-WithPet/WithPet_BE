package com.pawtner.WithPet.domain.chat.service;

import com.pawtner.WithPet.domain.chat.dto.ChatMessageDto;
import com.pawtner.WithPet.domain.chat.dto.ChatReadRequest;
import com.pawtner.WithPet.domain.chat.dto.ChatSendRequest;
import com.pawtner.WithPet.domain.chat.entity.ChatMessage;
import com.pawtner.WithPet.domain.chat.entity.ChatParticipant;
import com.pawtner.WithPet.domain.chat.entity.ChatRoom;
import com.pawtner.WithPet.domain.chat.exception.ChatRoomAccessDeniedException;
import com.pawtner.WithPet.domain.chat.exception.ChatRoomNotFoundException;
import com.pawtner.WithPet.domain.chat.repository.ChatMessageRepository;
import com.pawtner.WithPet.domain.chat.repository.ChatParticipantRepository;
import com.pawtner.WithPet.domain.chat.repository.ChatRoomRepository;
import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.member.repository.MemberRepository;
import com.pawtner.WithPet.global.enums.MsgType;
import com.pawtner.WithPet.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ChatParticipantRepository participantRepository;

    // [ 메시지 전송 ]
    public ChatMessageDto sendMessage(ChatSendRequest req) {
        // 유효성 검증, 권한 체크
        ChatRoom room = chatRoomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new ChatRoomNotFoundException("유효하지 않은 채팅방 ID: " + req.getRoomId()));

        Member sender = memberRepository.findById(req.getSenderId())
                .orElseThrow(() -> new MemberNotFoundException("유효하지 않은 사용자 ID: " + req.getSenderId()));

        boolean isParticipant = participantRepository.existsByChatRoomAndOwner_Id(room, sender.getId());
        if (!isParticipant) {throw new ChatRoomAccessDeniedException("해당 채팅방 참가자가 아닙니다. roomId=" + room.getChatRoomId() + ", senderId=" + sender.getId());}

        // ChatMessage 엔티티 생성 + DB 저장
        MsgType type = MsgType.valueOf(req.getMsgType().toUpperCase()); // enum

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .msgType(type)
                .msgBody(type == MsgType.TEXT ? req.getContent() : null)
                .msgImg(type == MsgType.IMAGE ? req.getContent() : null)
                .build();

        chatMessageRepository.save(message);

        // 방의 lastMessage 업데이트
        room.updateLastMessage(message);


        // 응답 DTO 변환 (이 DTO를 WebSocket으로 그대로 broadcast)
        return ChatMessageDto.builder()
                .messageId(message.getChatMsgId())
                .senderId(sender.getId())
                .mine(false) // 서버에선 상대 기준 모름 → 클라에서 mine 판단해도 됨
                .msgType(message.getMsgType().name())
                .content(type == MsgType.TEXT ? message.getMsgBody()
                        : type == MsgType.IMAGE ? "[사진]"
                        : type == MsgType.FILE ? "[파일]"
                        : "[알림]")
                .createdAt(message.getCreatedAt())
                .read(false) // 읽음 여부는 read 이벤트 / 방 진입 시점에서 따로 처리
                .build();
    }

    // [ 메시지 읽음 처리 ]
    public void markAsRead(ChatReadRequest req) {
        // 유효성 검증, 권한 체크
        ChatRoom room = chatRoomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new ChatRoomNotFoundException("유효하지 않은 채팅방 ID: " + req.getRoomId()));

        Member me = memberRepository.findById(req.getOwnerId())
                .orElseThrow(() -> new MemberNotFoundException("유효하지 않은 사용자 ID: " + req.getOwnerId()));

        ChatParticipant myParticipant = participantRepository
                .findByChatRoomAndOwner(room, me)
                .orElseThrow(() -> new ChatRoomAccessDeniedException("해당 채팅방의 참가자가 아닙니다. roomId=" + req.getRoomId() + ", ownerId=" + req.getOwnerId()));

        // 내가 이 방에서 마지막으로 읽은 시각 갱신
        myParticipant.updateLastReadAt(LocalDateTime.now());
    }
}
