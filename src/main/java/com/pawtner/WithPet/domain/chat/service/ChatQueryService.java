package com.pawtner.WithPet.domain.chat.service;

import com.pawtner.WithPet.domain.chat.dto.ChatRoomListItemDto;
import com.pawtner.WithPet.domain.chat.entity.ChatMessage;
import com.pawtner.WithPet.domain.chat.entity.ChatParticipant;
import com.pawtner.WithPet.domain.chat.entity.ChatRoom;
import com.pawtner.WithPet.domain.chat.repository.ChatMessageRepository;
import com.pawtner.WithPet.domain.chat.repository.ChatParticipantRepository;
import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.member.repository.MemberRepository;
import com.pawtner.WithPet.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

    private final ChatParticipantRepository participantRepo;
    private final ChatMessageRepository messageRepo;
    private final MemberRepository memberRepository;

    // [ 참여한 채팅방 목록 조회 (최근 메시지 시간 내림차순) ]
    public Page<ChatRoomListItemDto> getMyChatRooms(Long ownerId, int page, int size) {
        // 존재하지 않는 사용자일 경우 예외 발생
        Member owner = null;
        if (ownerId != null) {
            owner = memberRepository.findById(ownerId)
                    .orElseThrow(() -> new MemberNotFoundException("해당 사용자 정보를 찾을 수 없습니다. 사용자 ID: " + ownerId));
        }

        Pageable pageable = PageRequest.of(page, size);

        // 1) 참여한 방 + 마지막 메시지
        Page<ChatParticipant> participants =
                participantRepo.findMyRoomsWithRoomAndLastMessage(ownerId, pageable);

        // 응답 DTO 준비
        List<ChatRoomListItemDto> content = new ArrayList<>(participants.getNumberOfElements());

        // 2) 각 방에 대해 상대 정보, 프리뷰, unread 계산
        for (ChatParticipant cp : participants.getContent()) {
            ChatRoom room = cp.getChatRoom();
            ChatMessage last = room.getLastMessage();

            // 2-1) 최근 메시지 내용(텍스트/라벨)
            String lastMessageContent = null;
            LocalDateTime lastMessageAt = room.getLastMessageAt();

            if (last != null) {
                switch (last.getMsgType()) {
                    case TEXT -> lastMessageContent = trimText(last.getMsgBody());
                    case IMAGE -> lastMessageContent = "[사진]";
                    case FILE -> lastMessageContent = "[파일]";
                    case NOTICE -> lastMessageContent = "[알림]";
                }

                // room.lastMessageAt이 null이라면 메시지 생성 시각으로 대체
                if (lastMessageAt == null) {
                    lastMessageAt = last.getCreatedAt();
                }
            }

            // 2-2) 1:1 채팅 -> 상대 참가자 찾기
            Member opponent = participantRepo
                    .findOpponentByRoomAndOwnerIdNot(room, ownerId)
                    .map(ChatParticipant::getOwner)
                    .orElse(null);

            Long opponentId = null;
            String opponentNickname = null;
            String opponentProfileImg = null;

            if (opponent != null) {
                opponentId = opponent.getId();
                opponentNickname = opponent.getNickname();
                opponentProfileImg = opponent.getImage();
            }

            // 2-3) 안 읽은 메시지 개수 계산 (created_at > last_read_at 카운트)
            LocalDateTime lastReadAt = cp.getLastReadAt();
            long unread = 0L;

            if (lastReadAt != null) {
                unread = messageRepo.countByChatRoom_ChatRoomIdAndCreatedAtAfter(
                        room.getChatRoomId(), lastReadAt
                );
            } else if (last != null) { // 입장 후 한 번도 읽지 않은 경우: 방 전체 메시지 수 = unread
                unread = messageRepo.countByChatRoom_ChatRoomIdAndCreatedAtAfter(
                        room.getChatRoomId(), LocalDateTime.MIN
                );
            }

            // 2-4) DTO 빌드
            content.add(ChatRoomListItemDto.builder()
                    .roomId(room.getChatRoomId())
                    .opponentId(opponentId)
                    .opponentNickname(opponentNickname)
                    .opponentProfileImg(opponentProfileImg)
                    .lastMessageContent(lastMessageContent)
                    .lastMessageAt(lastMessageAt)
                    .unreadCount(unread)
                    .build());
        }

        return new PageImpl<>(content, pageable, participants.getTotalElements());
    }

    // 본문 긴 경우 미리보기 (40자 + ...)
    private String trimText(String text) {
        if (text == null) return null;
        String t = text.strip();
        return t.length() > 40 ? t.substring(0, 40) + "…" : t;
    }
}
