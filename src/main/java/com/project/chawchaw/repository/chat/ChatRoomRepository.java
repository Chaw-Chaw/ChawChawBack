package com.project.chawchaw.repository.chat;

import com.project.chawchaw.dto.chat.ChatRoomDto;
import com.project.chawchaw.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface
ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

}
