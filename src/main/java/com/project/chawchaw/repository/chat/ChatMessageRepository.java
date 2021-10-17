package com.project.chawchaw.repository.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.$Gson$Preconditions;
import com.project.chawchaw.dto.chat.ChatMessageDto;
import com.project.chawchaw.dto.chat.ChatRoomDto;
import com.project.chawchaw.dto.chat.MessageType;
import com.project.chawchaw.service.chat.ChatSubService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ChatMessageRepository {
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
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
       String  key = chatMessageDto.getRoomId().toString() + "_" + UUID.randomUUID().toString();
        if(chatMessageDto.getMessageType().equals(MessageType.IMAGE)){
            String fileName=chatMessageDto.getMessage().split("/net")[1];
            key = chatMessageDto.getRoomId().toString()+":image"+ "_" + UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(key,chatMessageDto);

        }
        else{
            redisTemplate.opsForValue().set(key,chatMessageDto);
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }


    }



    /**
     * roomId로 챗메시지 조회**/
    public List<ChatMessageDto> findChatMessageByRoomId(Long roomId, LocalDateTime exitDate){
        Set<String> keys = redisTemplate.keys(roomId.toString()+"_"+"*");

        List<ChatMessageDto>chatMessageDtos=new ArrayList<>();
        for(String key:keys) {

            ChatMessageDto chatMessageDto = objectMapper.convertValue(redisTemplate.opsForValue().get(key), ChatMessageDto.class);
            if (exitDate == null) {
                chatMessageDtos.add(chatMessageDto);
            }
            else{
                if(chatMessageDto.getRegDate().isAfter(exitDate)){
                    chatMessageDtos.add(chatMessageDto);
                }
            }
        }

       Collections.sort(chatMessageDtos, Comparator.comparing(ChatMessageDto::getRegDate));
//               (c1,c2)-> {
//
//          return c2.getRegDate().compareTo(c1.getRegDate());
//       });

        return chatMessageDtos.stream()
//                .limit(20)
                .collect(Collectors.toList());

    }
    /**
     * roomId
     * 메세지 조회 차단 시간**/

    public List<ChatMessageDto> findChatMessageByRoomIdWithBlock(Long roomId, LocalDateTime exitDate,LocalDateTime blockDate){
        Set<String> keys = redisTemplate.keys(roomId.toString()+"_"+"*");

        List<ChatMessageDto>chatMessageDtos=new ArrayList<>();
        for(String key:keys) {

            ChatMessageDto chatMessageDto = objectMapper.convertValue(redisTemplate.opsForValue().get(key), ChatMessageDto.class);
            if (exitDate == null) {
                chatMessageDtos.add(chatMessageDto);
            }
            else{
                if(chatMessageDto.getRegDate().isAfter(exitDate)&&chatMessageDto.getRegDate().isBefore(blockDate)){
                    chatMessageDtos.add(chatMessageDto);
                }
            }
        }

        Collections.sort(chatMessageDtos, Comparator.comparing(ChatMessageDto::getRegDate));
//               (c1,c2)-> {
//
//          return c2.getRegDate().compareTo(c1.getRegDate());
//       });

        return chatMessageDtos.stream()
//                .limit(20)
                .collect(Collectors.toList());

    }



    public void deleteByRoomId(Long roomId) {
        Set<String> keys = redisTemplate.keys(roomId.toString()+"_"+"*");
        redisTemplate.delete(keys);
    }
    public List<String> getImageByRoomId(Long roomId) {
        Set<String> keys = redisTemplate.keys(roomId.toString()+":image"+"_"+"*");
        List<String>fileNameList=new ArrayList<>();
        for(String key:keys){
            ChatMessageDto chatMessageDto = objectMapper.convertValue(redisTemplate.opsForValue().get(key), ChatMessageDto.class);
            fileNameList.add(chatMessageDto.getMessage().split("/net")[1]);
        }
        return fileNameList;
    }

    public void moveChatRoom(Long userId,Long roomId,String email)throws Exception{
        if(redisTemplate.opsForValue().get("session::"+"_"+email)==null){
            throw new Exception();
        }
        redisTemplate.opsForValue().set("session::"+email,roomId);
        Set<String> keys = redisTemplate.keys(roomId.toString() + "_" + "*");
        for(String key:keys){
            ChatMessageDto chatMessageDto = objectMapper.convertValue(redisTemplate.opsForValue().get(key), ChatMessageDto.class);
            if(chatMessageDto.getIsRead().equals(false)&&!chatMessageDto.getSenderId().equals(userId)){
                chatMessageDto.setIsRead(true);
                redisTemplate.opsForValue().set(key,chatMessageDto);
            }

        }

    }
    public Long getRoomSession(String email){
       return objectMapper.convertValue(redisTemplate.opsForValue().get("session::"+email),Long.class);
    }

    public void createRoomSession(String email){
        if(redisTemplate.opsForValue().get("session::"+email)==null)
        redisTemplate.opsForValue().set("session::"+email,-1L);
    }
    public void deleteRoomSession(String email){
        redisTemplate.delete("session::"+email);
    }


    /**채팅방 퇴장 여부**/
//    public void createChatRoomUserIsExit(Long chatRoomUserId,Boolean isExit){
//        redisTemplate.opsForValue().set("isExit::"+chatRoomUserId.toString(),false);
//    }
//
//    public Boolean getChatRoomUserIsExit(Long chatRoomUserId){
//         return (Boolean)redisTemplate.opsForValue().get("isExit::" + chatRoomUserId.toString());
//    }
//
//    public void deleteChatRoomUserIsExit(List<Long> chatRoomUserIdList){
//        for(Long chatRoomUserId:chatRoomUserIdList)
//        redisTemplate.delete("isExit::" + chatRoomUserId.toString());
//    }

}