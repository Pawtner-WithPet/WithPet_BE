package com.pawtner.WithPet.domain.chat.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;


    // 채팅방 개설시간
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // 마지막 메시지
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id", referencedColumnName = "chat_msg_id", nullable = true)
    private ChatMessage lastMessage;

    // 마지막 메시지 시간
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;


    // 채팅방 개설자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;


    // 게시글 연계
    @Column(name = "origin_lost_post_id")
    private Long originLostPostId;

    @Column(name = "origin_found_post_id")
    private Long originFoundPostId;


    @Builder
    public ChatRoom(Member owner, Long originLostPostId, Long originFoundPostId) {
        this.owner = owner;
        this.originLostPostId = originLostPostId;
        this.originFoundPostId = originFoundPostId;
    }

    // 최근 메시지/시간 갱신 메서드
    public void updateLastMessage(ChatMessage message) {
        this.lastMessage = message;
        this.lastMessageAt = message.getCreatedAt();
    }
}
