package com.pawtner.WithPet.domain.chat.repository;

import com.pawtner.WithPet.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    //[게시글 + 참가자 기준 방 찾기]
    // 실종 글 기준 방 찾기
    @Query("""
        select cr
        from ChatRoom cr
        join ChatParticipant cp1 on cp1.chatRoom = cr
        join ChatParticipant cp2 on cp2.chatRoom = cr
        where cr.originLostPostId = :postId
          and cp1.owner.id = :memberA
          and cp2.owner.id = :memberB
    """)
    Optional<ChatRoom> findDirectRoomByLostPostAndMembers(
            @Param("postId") Long postId,
            @Param("memberA") Long memberA,
            @Param("memberB") Long memberB
    );

    // 발견 글 기준 방 찾기
    @Query("""
        select cr
        from ChatRoom cr
        join ChatParticipant cp1 on cp1.chatRoom = cr
        join ChatParticipant cp2 on cp2.chatRoom = cr
        where cr.originFoundPostId = :postId
          and cp1.owner.id = :memberA
          and cp2.owner.id = :memberB
    """)
    Optional<ChatRoom> findDirectRoomByFoundPostAndMembers(
            @Param("postId") Long postId,
            @Param("memberA") Long memberA,
            @Param("memberB") Long memberB
    );

}
