package com.project.chawchaw.controller;
// import 생략...

import com.project.chawchaw.config.auth.CustomUserDetails;
import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.config.response.DefaultResponseVo;
import com.project.chawchaw.config.response.ResponseMessage;
import com.project.chawchaw.dto.chat.ChatMessageDto;
import com.project.chawchaw.dto.chat.ChatRoomDto;
import com.project.chawchaw.dto.user.UserRequestDto;
import com.project.chawchaw.repository.chat.ChatMessageRepository;
import com.project.chawchaw.service.S3Service;
import com.project.chawchaw.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller

public class ChatController {

    private final ChatMessageRepository chatRoomRepository;
    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    private final S3Service s3Service;
    private final ChatMessageRepository chatMessageRepository;

//    //채팅 리스트 화면
//    @GetMapping("/room")
//    public String rooms(Model model) {
//        return "chat/chat";
//    }


    @MessageMapping("/message")
    public void message(@RequestBody @Valid ChatMessageDto message) {


        if (message.getRegDate()==null) {
            message.setRegDate(LocalDateTime.now().withNano(0));
        }

        chatService.enterChatRoom(message.getRoomId());

        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        chatService.publish(chatService.getTopic(message.getRoomId()), message);
    }
    @MessageMapping("/message/test")
    public void messageTest(@RequestBody @Valid ChatMessageDto message) {


        if (message.getRegDate()==null) {
            message.setRegDate(LocalDateTime.now().withNano(0));
        }

        chatService.enterChatRoom(message.getRoomId());

        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        chatService.publishTest(chatService.getTopic(message.getRoomId()), message);
    }

    // 모든 채팅방 목록 반환
//    @GetMapping("/rooms")
//    @ResponseBody
//    public ResponseEntity<List<ChatRoomDto>> room() {
//        return new ResponseEntity(chatService.findAllRoom(), HttpStatus.OK);
//    }
    // 채팅방 생성
    @PostMapping("/chat/room")
    @ResponseBody
    public ResponseEntity createRoom(@RequestBody UserRequestDto requestDto, @RequestHeader("Authorization") String token) {

        Long fromUserId = Long.valueOf(jwtTokenProvider.getUserPk(token));
//        chatRoomUserRepository.isChatRoom(false)

        ChatRoomDto chatRoomDto = chatService.isChatRoom(fromUserId, requestDto.getUserId());
        if(chatRoomDto!=null){
//            chatMessageRepository.createChatRoomUserIsExit(fromUserId,false);
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CHATROOM_FIND_SUCCESS,
                    true,chatRoomDto), HttpStatus.OK);
        }
        else{

            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CHATROOM_CREAT_SUCCESS,
                    true,chatService.createRoom(requestDto.getUserId(), fromUserId)), HttpStatus.CREATED);
        }

    }

    //채팅방 조회
    @GetMapping("/chat")
    @ResponseBody
    public ResponseEntity getChatRoom(@RequestHeader("Authorization") String token) {

        Long userId = Long.valueOf(jwtTokenProvider.getUserPk(token));

        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CHATROOM_FIND_SUCCESS,
                true,chatService.getChat(userId)), HttpStatus.OK);
    }

    @DeleteMapping("/chat/room")
    @ResponseBody
    public ResponseEntity deleteChatRoom(@RequestBody ChatRoomDto chatRoomDto,@RequestHeader("Authorization") String token) {

        Long userId = Long.valueOf(jwtTokenProvider.getUserPk(token));

        if(chatService.deleteChatRoom(chatRoomDto.getRoomId(),userId))s3Service.deleteChatImage(chatMessageRepository.getImageByRoomId(chatRoomDto.getRoomId()));

        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CHATROOM_DELETE_SUCCESS,
                true), HttpStatus.OK);
    }

    @PostMapping("/chat/image")
    @ResponseBody
    public ResponseEntity chatImageUpload(@RequestBody MultipartFile file) {

        try {
            String imageUrl = s3Service.chatImageUpload(file);


            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CHAT_IMAGE_UPLOAD_SUCCESS,
                    true, imageUrl), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CHAT_IMAGE_UPLOAD_FAIL,
                    false), HttpStatus.OK);
        }
    }
    @PostMapping("/chat/room/enter")
    @ResponseBody
    public ResponseEntity changeChatRoom(@RequestBody ChatRoomDto chatRoomDto,@AuthenticationPrincipal CustomUserDetails customUserDetails) {


        try {
            chatService.moveChatRoom(customUserDetails.getId(), chatRoomDto.getRoomId(), customUserDetails.getUsername());
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.MOVE_CHATROOM_SUCCESS,
                    true), HttpStatus.OK);
        }catch (Exception e){

            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.MOVE_CHATROOM_FAIL,
                    false), HttpStatus.OK);
        }
    }


}