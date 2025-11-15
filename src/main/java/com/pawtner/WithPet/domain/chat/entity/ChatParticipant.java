package com.pawtner.WithPet.domain.chat.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_participant")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatParticipant {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_par_id")
    private Long chatParId;

    // 참여 시간
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    // 마지막 읽은 시간
    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    // 채팅방 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    // 사용자 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;


    @Builder
    public ChatParticipant(ChatRoom chatRoom, Member owner, LocalDateTime lastReadAt) {
        this.chatRoom = chatRoom;
        this.owner = owner;
        this.lastReadAt = lastReadAt;
    }

}
