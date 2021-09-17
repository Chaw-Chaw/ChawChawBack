package com.project.chawchaw.repository.chat;

import com.project.chawchaw.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser,Long> {

//    @Query("select cu from ChatRoomUser cu where (cu.toUser.id=:toUserId and cu.fromUser.id=:fromUserId) or (cu.fromUser.id=:toUserId and cu.toUser.id=:fromUserId)")
//    Optional<ChatRoomUser>findByToUserIdAndFromUserId(@Param("toUserId")Long toUserId,@Param("fromUserId")Long fromUserId);



//    @Query("select cu from ChatRoomUser cu where cu.toUser.id=:userId or cu.fromUser.id=:userId")
//    List<ChatRoomUser>findByChatRoomUserByUserId(@Param("userId")Long userId);


    @Query("select cu from ChatRoomUser cu where cu.user.id=:userId")
    List<ChatRoomUser>findByChatRoomUserByUserId(@Param("userId")Long userId);

    @Query("select ru from ChatRoomUser ru where ru.user.id=:fromUserId and ru.chatRoom.id in " +
            "(select ru2.chatRoom.id from ChatRoomUser ru2 where ru2.user.id = :toUserId)")
    Optional<ChatRoomUser> isChatRoom(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    @Query("select cu from ChatRoomUser cu join fetch cu.user join fetch cu.chatRoom where cu.chatRoom.id=:roomId")
    List<ChatRoomUser>findByRoomId(@Param("roomId")Long roomId);

//    @Query("select cu from ChatRoomUser cu where cu.chatRoom.id=:roomId and cu.user.id=:userId")
//    Optional<ChatRoomUser>findByRoomIdAndUserId(@Param("roomId")Long roomId,@Param("userId")Long userId);

}
