package com.project.chawchaw.service.chat;

import com.project.chawchaw.dto.chat.ChatDto;
import com.project.chawchaw.dto.chat.ChatMessageDto;

import com.project.chawchaw.dto.chat.ChatRoomDto;
import com.project.chawchaw.dto.chat.MessageType;
import com.project.chawchaw.entity.Block;
import com.project.chawchaw.entity.ChatRoom;
import com.project.chawchaw.entity.ChatRoomUser;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.ChatRoomNotFoundException;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.BlockRepository;
import com.project.chawchaw.repository.chat.ChatMessageRepository;
import com.project.chawchaw.repository.chat.ChatRoomRepository;
import com.project.chawchaw.repository.chat.ChatRoomUserRepository;
import com.project.chawchaw.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    private final RedisMessageListenerContainer redisMessageListener;

    // 구독 처리 서비스
    private final ChatSubService redisSubscriber;
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final BlockRepository blockRepository;
    private Map<Long, ChannelTopic> topics=new HashMap<>();


    @Transactional
    public void publish(ChannelTopic topic, ChatMessageDto message) {

        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

    @Transactional
    public void publishTest(ChannelTopic topic, ChatMessageDto roomMessage) {

            List<ChatRoomUser> chatRoomUserList = chatRoomUserRepository.findByRoomId(roomMessage.getRoomId());
            System.out.println(chatRoomUserList.isEmpty());


        // Websocket 구독자에게 채팅 메시지 Send
            for(ChatRoomUser chatRoomUser:chatRoomUserList) {
                User user= chatRoomUser.getUser();

                if (!roomMessage.getSenderId().equals(user.getId())) {
                    //sender가 차단목록에 없을 때
                    if(user.getBlockList().isEmpty()||!user.getBlockList().stream().map(b->b.getToUser().getId()).collect(Collectors.toSet()).contains(roomMessage.getSenderId())){
                        if(chatMessageRepository.getRoomSession(user.getEmail())!=null&&chatMessageRepository.getRoomSession(user.getEmail())==roomMessage.getRoomId()){
                            roomMessage.setIsRead(true);
                        }
                        else {
                            roomMessage.setIsRead(false);
                        }
                        messagingTemplate.convertAndSend("/queue/chat/" + user.getId(), roomMessage);
                    }
                    else{
                        roomMessage.setIsRead(false);
                    }
                }
//
            }
            chatMessageRepository.createChatMessage(roomMessage);



    }





    //chatroom
    @Transactional
    public ChatRoomDto createRoom(Long toUserId, Long fromUserId){
        User toUser=userRepository.findById(toUserId).orElseThrow(UserNotFoundException::new);
        User fromUser=userRepository.findById(fromUserId).orElseThrow(UserNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.createChatRoom(UUID.randomUUID().toString()));
        chatRoomUserRepository.save(ChatRoomUser.createChatRoomUser(chatRoom,toUser));
        chatRoomUserRepository.save(ChatRoomUser.createChatRoomUser(chatRoom,fromUser));
//        ChatMessageDto chatMessageDto = new ChatMessageDto(MessageType.ENTER,chatRoom.getId(), fromUserId, fromUser.getName(), fromUser.getName() + "님이 입장하셨습니다.",fromUser.getImageUrl(), LocalDateTime.now().withNano(0),false);
//        chatMessageRepository.createChatMessage(chatMessageDto);
//        messagingTemplate.convertAndSend("/queue/chat/room/wait/" + toUserId,chatMessageDto );
//        messagingTemplate.convertAndSend("/queue/alarm/chat/" + toUserId, chatMessageDto);
//          messagingTemplate.convertAndSend("/queue/chat/" + toUserId, chatMessageDto);
//        chatMessageRepository.createChatRoomUserIsExit(toUserId,false);
//        chatMessageRepository.createChatRoomUserIsExit(fromUserId,false);

          return new ChatRoomDto(chatRoom.getId(),chatRoom.getName());

//        ChatRoom chatRoom=null;
////        Optional<ChatRoomUser> findChatRoomUser = chatRoomUserRepository.findByToUserIdAndFromUserId(toUserId, fromUserId);
//        if(!chatRoomUserRepository.isChatRoom(toUserId,fromUserId)) {
//           chatRoom = chatRoomRepository.save(ChatRoom.createChatRoom(UUID.randomUUID().toString()));
//            chatRoomUserRepository.save(ChatRoomUser.createChatRoomUser(chatRoom,toUser));
//            chatRoomUserRepository.save(ChatRoomUser.createChatRoomUser(chatRoom,fromUser));
//            chatMessageRepository.createChatMessage(new ChatMessageDto(chatRoom.getId(),fromUser.getName(),fromUser.getName()+"님이 입장하셨습니다.", LocalDateTime.now().withNano(0)));
//        }
//        else{
//           chatRoom=findChatRoomUser.get().getChatRoom();
//
//        }

//            if(chatRoomUser.getChatRoom().getId()==(chatRoom.getId()))continue;
//            if(chatRoomUser.getFromUser().getId()==(fromUser.getId())){
//                ChatDto chatDto=new ChatDto(chatRoomUser.getChatRoom().getId(),chatRoomUser.getToUser().getName(),chatRoomUser.getToUser().getImageUrl(),
//                        chatMessageRepository.findChatMessageByRoomId(chatRoomUser.getChatRoom().getId()));
//                chatDtos.add(chatDto);
//
//            }
//            else {
//                ChatDto chatDto=new ChatDto(chatRoomUser.getChatRoom().getId(),chatRoomUser.getFromUser().getName(),chatRoomUser.getFromUser().getImageUrl(),
//                        chatMessageRepository.findChatMessageByRoomId(chatRoomUser.getChatRoom().getId()));
//                chatDtos.add(chatDto);
//
//            }
//        }
//        return chatDtos;

    }
    @Transactional
    public ChatRoomDto isChatRoom(Long fromUserId, Long toUserId){
        Optional<ChatRoomUser> chatRoomUser = chatRoomUserRepository.isChatRoom(fromUserId, toUserId);
        if(chatRoomUser.isPresent()){
            ChatRoomUser getChatRoomUser = chatRoomUser.get();
            chatRoomUser.get().changeIsExit(false);
            ChatRoom chatRoom = getChatRoomUser.getChatRoom();

            return new ChatRoomDto(chatRoom.getId(),chatRoom.getName());
        }
        else{
            return null;
        }
    }

    public List<ChatMessageDto> getChatMessageByIsRead(Long id){
        List<ChatMessageDto> chatMessageDto=new ArrayList<>();
//        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        List<ChatRoomUser> chatRoomUserByUserId = chatRoomUserRepository.findByChatRoomUserByUserId(id);

        for(int i=0;i<chatRoomUserByUserId.size();i++){
            ChatRoomUser chatRoomUser1 = chatRoomUserByUserId.get(i);
            List<ChatRoomUser> chatRoomUserByRoomId = chatRoomUserRepository.findByRoomId(chatRoomUser1.getChatRoom().getId());
            for(int j=0;j<chatRoomUserByRoomId.size();j++){
                ChatRoomUser chatRoomUser2 = chatRoomUserByRoomId.get(j);
                if(chatRoomUser2.getUser().getId().equals(id)){
                    chatMessageRepository.findChatMessageByRoomId(chatRoomUser2.getChatRoom().getId(),chatRoomUser2.getExitDate()).stream().forEach(
                            c->{
                                if(!c.getIsRead())chatMessageDto.add(c);
                            }
                    );
                }
            }
        }
        Collections.sort(chatMessageDto, (c1, c2)-> {

            return  c2.getRegDate().compareTo(c1.getRegDate());
        });
        return chatMessageDto;


    }

    public List<ChatDto> getChat(Long userId) {

        //block fetch join 땡겨옴
        List<Block> blockByFromUserId = blockRepository.findBlockByFromUserId(userId);

        //key : toUserId  , value :(block)regDate
        Map<Long,LocalDateTime>blockMap =new HashMap<>();

        blockByFromUserId.stream().forEach(b->{
            blockMap.put(b.getToUser().getId(),b.getRegDate());
        });
        List<ChatDto> chatDtos = new ArrayList<>();
//       chatDtos.add(new ChatDto(chatRoom.getId(),toUser.getId(),toUser.getName(),toUser.getImageUrl(),
//               chatMessageRepository.findChatMessageByRoomId(chatRoom.getId())));
        List<ChatRoomUser> chatRoomUserByUserId = chatRoomUserRepository.findByChatRoomUserByUserId(userId);

        first:
        for (int i = 0; i < chatRoomUserByUserId.size(); i++) {
            ChatRoomUser chatRoomUser1 = chatRoomUserByUserId.get(i);
            List<ChatRoomUser> chatRoomUserByRoomId = chatRoomUserRepository.findByRoomId(chatRoomUser1.getChatRoom().getId());

            Long toUserId=null;
            //chatDto 생성
            ChatDto chatDto = new ChatDto();

            ChatRoom chatRoom = chatRoomUser1.getChatRoom();

            List<ChatMessageDto> chatMessageByRoomId=null;

            for (ChatRoomUser chatRoomUser : chatRoomUserByRoomId) {
                User user = chatRoomUser.getUser();
//                List<ChatMessageDto> chatMessageByRoomId = chatMessageRepository.findChatMessageByRoomId(chatRoom.getId(), chatRoomUser.getExitDate());

                if (user.getId().equals(userId)) {
                    if(toUserId==null){
                        toUserId=chatRoomUserByRoomId.get(1).getUser().getId();
                    }
                    if(blockMap.get(toUserId)!=null)
                    chatMessageByRoomId=chatMessageRepository.findChatMessageByRoomIdWithBlock(chatRoom.getId(), chatRoomUser.getExitDate(),blockMap.get(toUserId));

                    else{
                        chatMessageByRoomId=chatMessageRepository.findChatMessageByRoomId(chatRoom.getId(),chatRoomUser.getExitDate());
                    }

                    if (chatMessageByRoomId.isEmpty() && chatRoomUser.getIsExit().equals(true))
                        continue first;
                }

                else{
                    toUserId=user.getId();
                }

                chatDto.getParticipantNames().add(user.getName());
                chatDto.getParticipantIds().add(user.getId());
                chatDto.getParticipantImageUrls().add(user.getImageUrl());
                chatDto.setMessages(chatMessageByRoomId);

            }

            chatDtos.add(chatDto);
        }

        return chatDtos;
    }
//    public List<ChatDto> getChatByExitDate(Long id,LocalDateTime exitDate){
//        List<ChatDto> chatDtos=new ArrayList<>();
////       chatDtos.add(new ChatDto(chatRoom.getId(),toUser.getId(),toUser.getName(),toUser.getImageUrl(),
////               chatMessageRepository.findChatMessageByRoomId(chatRoom.getId())));
////
//        List<ChatRoomUser> chatRoomUserByUserId = chatRoomUserRepository.findByChatRoomUserByUserId(id);
//
//        for(int i=0;i<chatRoomUserByUserId.size();i++){
//            ChatRoomUser chatRoomUser1 = chatRoomUserByUserId.get(i);
//            List<ChatRoomUser> chatRoomUserByRoomId = chatRoomUserRepository.findByRoomId(chatRoomUser1.getChatRoom().getId());
//            ChatDto chatDto=new ChatDto();
//            ChatRoom chatRoom=chatRoomUser1.getChatRoom();
//            for(int j=0;j<chatRoomUserByRoomId.size();j++){
//                User user = chatRoomUserByRoomId.get(j).getUser();
////                if(!user.getId().equals(id)) {
//                chatDto.getParticipantNames().add(user.getName());
//                chatDto.getParticipantIds().add(user.getId());
//                chatDto.getParticipantImageUrls().add(user.getImageUrl());
////                }
//
//            }
//            chatDto.setMessages(chatMessageRepository.findChatMessageByRoomId(chatRoom.getId()));
//            chatDtos.add(chatDto);
//        }
//
//        return chatDtos;
//
//
//    }
//


    @Transactional
    public void deleteChatRoomByUserId(Long userId){
        List<ChatRoomUser> byChatRoomUserByUserId = chatRoomUserRepository.findByChatRoomUserByUserId(userId);
        for(ChatRoomUser chatRoomUser:byChatRoomUserByUserId){
            Long roomId = chatRoomUser.getChatRoom().getId();
            deleteChatRoom(roomId,userId);
        }
    }


    /**
     채팅방 삭제
     메세지 생성 시간과 퇴장 시간 비교

     채팅방 회원남았을시 false
     채팅방 회원 모두 나갔을시 true


     채팅없이 바로 퇴장한 경우 처리  or 메세지 유효기간 다되서 없을때 처리  **/

    @Transactional
    public Boolean deleteChatRoom(Long roomId,Long userId) {
        List<ChatRoomUser> findChatRoomUser = chatRoomUserRepository.findByRoomId(roomId);
        List<ChatMessageDto> chatMessageByRoomId = chatMessageRepository.findChatMessageByRoomId(roomId, null);
        LocalDateTime regDate=null;
        //채팅 메세지가 있을 때만 처리
        if(!chatMessageByRoomId.isEmpty())
            regDate = chatMessageByRoomId.get(chatMessageByRoomId.size() - 1).getRegDate();

        //상대방 user
        ChatRoomUser chatRoomUser1=null;

        for (ChatRoomUser chatRoomUser : findChatRoomUser) {
            if (!chatRoomUser.getUser().getId().equals(userId)) {
                chatRoomUser1=chatRoomUser;

            } else {
                chatRoomUser.changeIsExit(true);
                chatRoomUser.changeExitDate();
            }
        }

//        if(regDate==null){
//            if()
//        }

        if (chatRoomUser1.getExitDate()!=null&&chatRoomUser1.getExitDate().isBefore(regDate)) {
            chatRoomRepository.delete(findChatRoomUser.get(0).getChatRoom());
            chatMessageRepository.deleteByRoomId(roomId);
            return true;
        }
        return false;
    }




        /**
         * 유저 퇴장여부를 redis안에 넣었을때
         * 메시지를 보낼때마다 쿼리문이 하나씩 더 추가된다.**/

//        for(ChatRoomUser chatRoomUser :findChatRoomUser){
//           if(chatRoomUser.getUser().getId().equals(userId)){
//               chatRoomUser.changeExitDate();
//               chatMessageRepository.createChatRoomUserIsExit(chatRoomUser.getId(),true);
//           }
//
//           if(!chatMessageRepository.getChatRoomUserIsExit(chatRoomUser.getId())){
//               check=1;
//           }
//       }
//
//
//       if(check==0){
//            chatRoomRepository.delete(findChatRoomUser.get(0).getChatRoom());
//            chatMessageRepository.deleteByRoomId(roomId);
//            chatMessageRepository.deleteChatRoomUserIsExit(findChatRoomUser.stream().map(f->f.getId()).collect(Collectors.toList()));
//        }
//        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
//        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
//        chatRoomRepository.delete(chatRoom);
//        chatMessageRepository.deleteByRoomId(roomId);
//        ChatMessageDto message = new ChatMessageDto(
//                MessageType.EXIT, roomId, user.getId(), user.getName(),
//                user.getName() + "님이 퇴장하셨습니다.", user.getImageUrl(),LocalDateTime.now().withNano(0),false);
//
//        for(ChatRoomUser chatRoomUser:chatRoom.getChatRoomUsers()) {
//            Long chatUserId=chatRoomUser.getUser().getId();
//            if(!userId.equals(chatUserId)) {
//                messagingTemplate.convertAndSend("/queue/chat/" + chatUserId, message);
//
//            }
//        }




    public void moveChatRoom(Long userId,Long roomId,String email)throws Exception{
        chatMessageRepository.moveChatRoom(userId,roomId,email);
    }



    public void enterChatRoom(Long roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null) {
            topic = new ChannelTopic(String.valueOf(roomId));
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    public ChannelTopic getTopic(Long roomId) {
        return topics.get(roomId);
    }



}