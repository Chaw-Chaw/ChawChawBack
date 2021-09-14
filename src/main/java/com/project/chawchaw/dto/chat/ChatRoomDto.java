package com.project.chawchaw.dto.chat;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto implements Serializable {
    private Long roomId;
    private String name;

//    public static ChatRoomDto create(String name) {
//        ChatRoomDto chatRoom = new ChatRoomDto();
//        chatRoom.roomId = UUID.randomUUID().toString();
//        chatRoom.name = name;
//        return chatRoom;
//    }
}