package com.pawtner.WithPet.domain.chat.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.global.enums.MsgType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_msg_id")
    private Long chatMsgId;

    // 메시지 타입 (ENUM: text or image)
    @Enumerated(EnumType.STRING)
    @Column(name = "msg_type", nullable = false, length = 20)
    private MsgType msgType;

    // 메시지 내용
    @Column(name = "msg_body", columnDefinition = "TEXT")
    private String msgBody;

    // 메시지 사진
    @Column(name = "msg_img", length = 200)
    private String msgImg;

    // 작성 시간
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 채팅방 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    // 발송인 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;


    @Builder
    public ChatMessage(MsgType msgType, String msgBody, String msgImg, ChatRoom chatRoom, Member sender) {
        this.msgType = msgType;
        this.msgBody = msgBody;
        this.msgImg = msgImg;
        this.chatRoom = chatRoom;
        this.sender = sender;
    }


}
