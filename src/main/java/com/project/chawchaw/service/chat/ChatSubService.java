package com.project.chawchaw.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chawchaw.dto.chat.ChatMessageDto;
import com.project.chawchaw.entity.ChatRoomUser;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.repository.chat.ChatMessageRepository;
import com.project.chawchaw.repository.chat.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class ChatSubService implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {

            System.out.println("일단왔따");

            // redis에서 발행된 데이터를 받아 deserialize
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            // ChatMessage 객채로 맵핑

            ChatMessageDto roomMessage = objectMapper.readValue(publishMessage, ChatMessageDto.class);


            List<ChatRoomUser> chatRoomUserList = chatRoomUserRepository.findByRoomId(roomMessage.getRoomId());

            // Websocket 구독자에게 채팅 메시지 Send
            for(ChatRoomUser chatRoomUser:chatRoomUserList) {
                User user= chatRoomUser.getUser();
                if (!roomMessage.getSenderId().equals(user.getId())) {
//
                    if(chatMessageRepository.getRoomSession(user.getEmail())==roomMessage.getRoomId()){
                        roomMessage.setIsRead(true);
                    }
                    else {
                        roomMessage.setIsRead(false);
                    }
                    if(user.getBlockList().isEmpty()) messagingTemplate.convertAndSend("/queue/chat/" + user.getId(), roomMessage);
                    else if(!user.getBlockList().stream().map(b->b.getToUser().getId()).collect(Collectors.toSet()).contains(roomMessage.getSenderId())) {

                        messagingTemplate.convertAndSend("/queue/chat/" + user.getId(), roomMessage);
                    }
                }
//
            }
            chatMessageRepository.createChatMessage(roomMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
