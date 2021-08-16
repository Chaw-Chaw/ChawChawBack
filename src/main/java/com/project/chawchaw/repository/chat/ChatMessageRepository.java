package com.project.chawchaw.repository.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.$Gson$Preconditions;
import com.project.chawchaw.dto.chat.ChatMessageDto;
import com.project.chawchaw.dto.chat.ChatRoomDto;
import com.project.chawchaw.service.chat.ChatSubService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ChatMessageRepository {
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final ChatSubService redisSubscriber;
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
//    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomDto> opsHashChatRoom;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;

    //==========
    private final RedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;


    public void createChatMessage(ChatMessageDto chatMessageDto){
        String key = chatMessageDto.getRoomId().toString() + "_" + UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(key,chatMessageDto);
        redisTemplate.expire(key,7, TimeUnit.DAYS);
    }
    //최근 20개조회
    public List<ChatMessageDto> findChatMessageByRoomId(Long roomId){
        Set<String> keys = redisTemplate.keys(roomId.toString()+"_"+"*");

        List<ChatMessageDto>chatMessageDtos=new ArrayList<>();
        for(String key:keys){

            ChatMessageDto chatMessageDto = objectMapper.convertValue(redisTemplate.opsForValue().get(key), ChatMessageDto.class);
            chatMessageDtos.add(chatMessageDto);
        }

       Collections.sort(chatMessageDtos, (c1,c2)-> {

          return c2.getRegDate().compareTo(c1.getRegDate());
       });

        return chatMessageDtos.stream().limit(20).collect(Collectors.toList());

    }


//    @PostConstruct
//    private void init() {
//        opsHashChatRoom = redisTemplate.opsForHash();
//        topics = new HashMap<>();
//    }

//    public List<ChatRoomDto> findAllRoom() {
//        return opsHashChatRoom.values(CHAT_ROOMS);
//
//    }
//
//    public ChatRoomDto findRoomById(String id) {
//        return opsHashChatRoom.get(CHAT_ROOMS, id);
//    }


    //채팅방 생성
//    public ChatRoomDto createChatRoom(String name) {
//        ChatRoomDto chatRoom = ChatRoomDto.create(name);
//        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
//        return chatRoom;
//    }

   //채팅방 입장

}