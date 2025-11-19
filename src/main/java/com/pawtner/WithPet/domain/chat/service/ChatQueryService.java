package com.pawtner.WithPet.domain.chat.service;

import com.pawtner.WithPet.domain.chat.dto.*;
import com.pawtner.WithPet.domain.chat.entity.ChatMessage;
import com.pawtner.WithPet.domain.chat.entity.ChatParticipant;
import com.pawtner.WithPet.domain.chat.entity.ChatRoom;
import com.pawtner.WithPet.domain.chat.exception.ChatRoomAccessDeniedException;
import com.pawtner.WithPet.domain.chat.exception.ChatRoomOriginNotFoundException;
import com.pawtner.WithPet.domain.chat.repository.ChatMessageRepository;
import com.pawtner.WithPet.domain.chat.repository.ChatParticipantRepository;
import com.pawtner.WithPet.domain.chat.repository.ChatRoomRepository;
import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.member.repository.MemberRepository;
import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import com.pawtner.WithPet.domain.search.exception.FoundPetPostNotFoundException;
import com.pawtner.WithPet.domain.search.exception.InvalidPostTypeException;
import com.pawtner.WithPet.domain.search.exception.LostPetPostNotFoundException;
import com.pawtner.WithPet.domain.search.repository.FoundPetPostRepository;
import com.pawtner.WithPet.domain.search.repository.LostPetPostRepository;
import com.pawtner.WithPet.global.enums.PostType;
import com.pawtner.WithPet.domain.chat.exception.ChatRoomNotFoundException;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

    private final MemberRepository memberRepository;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository participantRepo;
    private final ChatMessageRepository messageRepo;

    private final LostPetPostRepository lostPostRepo;
    private final FoundPetPostRepository foundPostRepo;

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

        // > 본문 긴 경우 미리보기 (40자 + ...)
    private String trimText(String text) {
        if (text == null) return null;
        String t = text.strip();
        return t.length() > 40 ? t.substring(0, 40) + "…" : t;
    }


    // [ 채팅목록에서 방을 클릭해 진입하는 경우 ]
    @Transactional
    public ChatRoomDetailResponse enterExistingRoom(Long ownerId, Long roomId) {

        Member me = memberRepository.findById(ownerId)
                .orElseThrow(() -> new MemberNotFoundException("유효하지 않은 사용자 ID: " + ownerId));

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException("유효하지 않은 채팅방 ID: " + roomId));

        // 1:1 → 상대 참가자 찾기
        List<ChatParticipant> participants = participantRepo.findByChatRoom(room);
        if (participants.size() != 2) {
            throw new IllegalStateException("참가자 수가 " + participants.size() + "명입니다.(1:1 채팅방 X)");
        }

        ChatParticipant myParticipant = participants.stream()
                .filter(cp -> cp.getOwner().getId().equals(ownerId))
                .findFirst()
                .orElseThrow(() -> new ChatRoomAccessDeniedException(
                        "해당 채팅방의 참가자가 아닙니다. roomId=" + roomId + ", ownerId=" + ownerId
                ));

        ChatParticipant opponentParticipant = participants.stream()
                .filter(cp -> !cp.getOwner().getId().equals(ownerId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상대 참가자를 찾을 수 없습니다."));

        Member opponent = opponentParticipant.getOwner();

        // 채팅방 들어간 시점에 lastReadAt 갱신 (전체 메시지 읽음 처리)
        myParticipant.updateLastReadAt(LocalDateTime.now());

        // 메시지 전체 조회
        List<ChatMessage> messages = messageRepo.findByChatRoom_ChatRoomIdOrderByCreatedAtAsc(roomId);

        List<ChatMessageDto> messageDtos = messages.stream()
                .map(m -> toMessageDto(m, me, opponentParticipant))
                .toList();

        // 시작 게시글 정보 세팅
        String originType = null;
        Long originId = null;

        if (room.getOriginLostPostId() != null && room.getOriginLostPostId() != 0) {
            originType = "LOST";
            originId = room.getOriginLostPostId();
        } else if (room.getOriginFoundPostId() != null && room.getOriginFoundPostId() != 0) {
            originType = "FOUND";
            originId = room.getOriginFoundPostId();
        }

        return ChatRoomDetailResponse.builder()
                .roomId(room.getChatRoomId())
                .opponentId(opponent.getId())
                .opponentNickname(opponent.getNickname())
                .opponentProfileImg(opponent.getImage())
                .originPostType(originType)
                .originPostId(originId)
                .messages(messageDtos)
                .build();
    }

        // > 채팅 메시지 DTO 함수
    private ChatMessageDto toMessageDto(ChatMessage m, Member me, ChatParticipant opponentParticipant) {
        boolean mine = m.getSender().getId().equals(me.getId());

        String contentLabel;
        switch (m.getMsgType()) {
            case TEXT -> contentLabel = m.getMsgBody();
            case IMAGE -> contentLabel = "[사진]";
            case FILE -> contentLabel = "[파일]";
            case NOTICE -> contentLabel = "[알림]";
            default -> contentLabel = "";
        }

        boolean read;
        if (mine) {
            // 내가 보낸 메시지 → 상대가 읽었는지 (상대 lastReadAt 기준)
            LocalDateTime oppLastReadAt = opponentParticipant.getLastReadAt();
            read = (oppLastReadAt != null && !m.getCreatedAt().isAfter(oppLastReadAt));
        } else {
            // 상대가 보낸 메시지 ->  "읽음"
            read = true;
        }

        return ChatMessageDto.builder()
                .messageId(m.getChatMsgId())
                .senderId(m.getSender().getId())
                .mine(mine)
                .msgType(m.getMsgType().name())
                .content(contentLabel)
                .createdAt(m.getCreatedAt())
                .read(read)
                .build();
    }



    // [ 게시글에서 채팅방 진입하는 경우 ]
    @Transactional
    public ChatRoomDetailResponse enterFromPost(EnterChatFromPostRequest request) {
        Long ownerId = request.getOwnerId();
        Long postId = request.getPostId();
        PostType postType = parsePostType(request.getPostType());

        Member me = memberRepository.findById(ownerId)
                .orElseThrow(() -> new MemberNotFoundException("유효하지 않은 사용자 ID: " + ownerId));

        // 1) 게시글 + 게시글 작성자 찾기
        Member postOwner;
        Optional<ChatRoom> existingRoomOpt;

        if (postType == PostType.LOST) {
            LostPetPost post = lostPostRepo.findById(postId)
                    .orElseThrow(() -> new LostPetPostNotFoundException("유효하지 않은 실종 게시글 ID: " + postId));
            postOwner = post.getOwner();

            // 2) 기존 방 있는지 확인
            existingRoomOpt = chatRoomRepository
                    .findDirectRoomByLostPostAndMembers(postId, ownerId, postOwner.getId());

        } else { // FOUND
            FoundPetPost post = foundPostRepo.findById(postId)
                    .orElseThrow(() -> new FoundPetPostNotFoundException("유효하지 않은 발견 게시글 ID: " + postId));
            postOwner = post.getOwner();

            // 2) 기존 방 있는지 확인
            existingRoomOpt = chatRoomRepository
                    .findDirectRoomByFoundPostAndMembers(postId, ownerId, postOwner.getId());
        }

        // 3-1) 존재하는 방이면 기존 진입 로직 재사용
        if (existingRoomOpt.isPresent()) {
            ChatRoom room = existingRoomOpt.get();
            return enterExistingRoom(ownerId, room.getChatRoomId());
        }

        // 3-2) 방이 없으면 새로 만들고, 그 정보로 바로 응답 생성
        ChatRoom newRoom = createNewRoomForPost(postType, postId, me, postOwner);

        return ChatRoomDetailResponse.builder()
                .roomId(newRoom.getChatRoomId())
                .opponentId(postOwner.getId())
                .opponentNickname(postOwner.getNickname())
                .opponentProfileImg(postOwner.getImage())
                .originPostType(postType.name())
                .originPostId(postId)
                .messages(List.of())   // 새 방이라 메시지 없음
                .build();
    }

        // > 채팅방 생성 함수
    @Transactional
    public ChatRoom createNewRoomForPost(PostType postType, Long postId, Member me, Member postOwner) {

        ChatRoom room = ChatRoom.builder()
                .owner(me)
                .originLostPostId(postType == PostType.LOST ? postId : 0L)
                .originFoundPostId(postType == PostType.FOUND ? postId : 0L)
                .build();

        chatRoomRepository.save(room);

        ChatParticipant meParticipant = ChatParticipant.builder()
                .chatRoom(room)
                .owner(me)
                .lastReadAt(LocalDateTime.now())
                .build();

        ChatParticipant opponentParticipant = ChatParticipant.builder()
                .chatRoom(room)
                .owner(postOwner)
                .lastReadAt(null)
                .build();

        participantRepo.save(meParticipant);
        participantRepo.save(opponentParticipant);

        return room;
    }
        // > 게시물 type 검사
    private PostType parsePostType(String raw) {
        if (raw == null) {
            throw new InvalidPostTypeException("postType 이 null 입니다.");
        }

        try {
            return PostType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            // LOST / FOUND 이외 값 들어온 경우
            throw new InvalidPostTypeException("유효하지 않은 postType: " + raw);
        }
    }



    // [ 채팅 상세글 이동 (채팅 시작한 게시글 id 반환) ]
    public ChatRoomOriginPostResponse getOriginPost(Long ownerId, Long roomId) {

        Member me = memberRepository.findById(ownerId)
                .orElseThrow(() -> new MemberNotFoundException("유효하지 않은 사용자 ID: " + ownerId));
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException("유효하지 않은 채팅방 ID: " + roomId));

        // 이 방의 참가자인지 확인 (권한 체크)
        boolean isParticipant = participantRepo.existsByChatRoomAndOwner_Id(room, ownerId);
        if (!isParticipant) {
            throw new ChatRoomAccessDeniedException("해당 채팅방의 참가자가 아닙니다. roomId=" + roomId + ", ownerId=" + ownerId);
        }

        // origin post 정보 읽기
        Long lostId = room.getOriginLostPostId();
        Long foundId = room.getOriginFoundPostId();

        if (lostId != null && lostId != 0L) {
            return ChatRoomOriginPostResponse.builder()
                    .postId(lostId)
                    .postType("LOST")
                    .build();
        }

        if (foundId != null && foundId != 0L) {
            return ChatRoomOriginPostResponse.builder()
                    .postId(foundId)
                    .postType("FOUND")
                    .build();
        }

        // 둘 다 없으면 예외
        throw new ChatRoomOriginNotFoundException("해당 채팅방에 연결된 origin 게시글이 없습니다. roomId=" + roomId);
    }
}
