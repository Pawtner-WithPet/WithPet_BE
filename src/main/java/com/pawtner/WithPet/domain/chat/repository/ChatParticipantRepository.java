package com.pawtner.WithPet.domain.chat.repository;

import com.pawtner.WithPet.domain.chat.entity.ChatParticipant;
import com.pawtner.WithPet.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    // [참가자 기준으로 내가 참여한 방 목록을 방/마지막메시지까지 한 번에 로드]
    @Query("""
       select cp
       from ChatParticipant cp
       join fetch cp.chatRoom cr
       left join fetch cr.lastMessage lm
       where cp.owner.id = :ownerId
       order by cr.lastMessageAt desc, cr.chatRoomId desc
    """)
    Page<ChatParticipant> findMyRoomsWithRoomAndLastMessage(
            @Param("ownerId") Long ownerId, Pageable pageable
    );

    @Query("""
        select cp
        from ChatParticipant cp
        where cp.chatRoom = :chatRoom
          and cp.owner.id <> :ownerId
    """)
    Optional<ChatParticipant> findOpponentByRoomAndOwnerIdNot(
            @Param("chatRoom") ChatRoom chatRoom,
            @Param("ownerId") Long ownerId
    ); //해당 방에서 본인 아닌 참가자

    @Query("""
       select cr.chatRoomId
       from ChatParticipant cp
       join cp.chatRoom cr
       where cp.owner.id = :ownerId
    """)
    List<Long> findMyRoomIds(@Param("ownerId") Long ownerId);



    //[방 + 유저 기준 참가자 찾기]
    Optional<ChatParticipant> findByChatRoomAndOwner_Id(ChatRoom chatRoom, Long ownerId);

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);


    boolean existsByChatRoomAndOwner_Id(ChatRoom chatRoom, Long ownerId);
}
