package com.project.chawchaw.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.project.chawchaw.ChawchawApplication;
import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.config.socket.WebSocketConfig;
import com.project.chawchaw.dto.chat.ChatDto;
import com.project.chawchaw.dto.chat.ChatMessageDto;
import com.project.chawchaw.dto.chat.ChatRoomDto;
import com.project.chawchaw.dto.chat.MessageType;
import com.project.chawchaw.dto.user.UserLoginRequestDto;
import com.project.chawchaw.dto.user.UserLoginResponseDto;
import com.project.chawchaw.dto.user.UserSignUpRequestDto;
import com.project.chawchaw.dto.user.UserUpdateDto;
import com.project.chawchaw.entity.Country;
import com.project.chawchaw.entity.Language;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.chat.ChatMessageRepository;
import com.project.chawchaw.repository.chat.ChatRoomRepository;
import com.project.chawchaw.repository.chat.ChatRoomUserRepository;
import com.project.chawchaw.repository.user.UserRepository;
import com.project.chawchaw.service.BlockService;
import com.project.chawchaw.service.SignService;
import com.project.chawchaw.service.UserService;
import io.lettuce.core.dynamic.domain.Timeout;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.persistence.EntityManager;

import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
//@SpringBootTest(classes =ChawchawApplication.class)
class ChatServiceTest {


    @Autowired
    ChatService chatService;

    @Autowired
    EntityManager em;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    ChatRoomUserRepository chatRoomUserRepository;

    @Autowired
    Gson gson;

    @Autowired
    SignService signService;

    @Autowired
    SimpMessageSendingOperations messagingTemplate;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    BlockService blockService;

    //    @LocalServerPort Integer port;
    @LocalServerPort Integer port;
    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;
    static final String WEBSOCKET_TOPIC = "/queue/chat/";


//    @Test
//    @Rollback(value = false)
//    public void dd()throws Exception{
//       //given
//
//       //when
//
//       //then
//    }



//    @BeforeEach
    public void beforeEach()throws Exception{

        Language language1=Language.createLanguage("한국어","ko");
        Country country1=Country.createCountry("한국");
        em.persist(language1);
        em.persist(country1);
        Language language2=Language.createLanguage("일본어","jp");
        Country country2=Country.createCountry("일본");
        em.persist(language2);
        em.persist(country2);
        Language language3=Language.createLanguage("영어","en");
        Country country3=Country.createCountry("미국");
        em.persist(country3);

        em.persist(language3);
        Language language4=Language.createLanguage("불어","fr");
        Country country4=Country.createCountry("프랑스");
        em.persist(country4);
        em.persist(language4);

        UserSignUpRequestDto userSignUpRequestDto1=new UserSignUpRequestDto();
        userSignUpRequestDto1.setEmail("fpdlwjzlr@naver.com");
        userSignUpRequestDto1.setName("김진현");
        userSignUpRequestDto1.setPassword("11");
        userSignUpRequestDto1.setWeb_email("32141221@dankook.ac.kr");
        userSignUpRequestDto1.setSchool("서울시립대학교");

        UserSignUpRequestDto userSignUpRequestDto2=new UserSignUpRequestDto();
        userSignUpRequestDto2.setEmail("fpdlwjzlr@naver.comm");
        userSignUpRequestDto2.setName("김진현");
        userSignUpRequestDto2.setPassword("22");
        userSignUpRequestDto2.setWeb_email("32141221@dankook.ac.kr");
        userSignUpRequestDto2.setSchool("서울시립대학교");

        signService.signup(userSignUpRequestDto1);
        signService.signup(userSignUpRequestDto2);



        //when
        List<String> user1c=new ArrayList<>();
        user1c.add("미국");
        user1c.add("프랑스");
        List<String>user1l=new ArrayList<>();
        user1l.add("jp");
        user1l.add("en");
        List<String>user1h=new ArrayList<>();
        user1h.add("fr");
        UserUpdateDto userUpdateDto=new UserUpdateDto(user1c,user1l,user1h,"user1",
                "facebook","insta","https://" + "d3t4l8y7wi01lo.cloudfront.net" + "/" + "defaultImage_233500392.png","한국","ko","en");

        User user1 = userRepository.findByEmail("fpdlwjzlr@naver.com").orElseThrow(UserNotFoundException::new);
        User user2 = userRepository.findByEmail("fpdlwjzlr@naver.comm").orElseThrow(UserNotFoundException::new);

        userService.userProfileUpdate(userUpdateDto,user1.getId());
        userService.userProfileUpdate(userUpdateDto,user2.getId());

        ChatRoomDto room = chatService.createRoom(user1.getId(), user2.getId());

        blockService.createBlock(user2.getId(),user1.getId());

        em.flush();
        /**
         * stomp 설정
         * **/

        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
//        stompClient.setMessageConverter(new ByteArrayMessageConverter());

    }


    /**
     * 채팅방생성 user1->user2
     */
    @Test
    public void createChatRoom()throws Exception{
       //given
        User user1 = userRepository.findByEmail("fpdlwjzlr@naver.com").orElseThrow(UserNotFoundException::new);
        User user2 = userRepository.findByEmail("fpdlwjzlr@naver.comm").orElseThrow(UserNotFoundException::new);

       //when
        ChatRoomDto room = chatService.createRoom(user2.getId(), user1.getId());
        em.flush();
        List<ChatMessageDto> chatMessageByRoomId = chatMessageRepository.findChatMessageByRoomId(room.getRoomId(),null);


        //then
        assertThat(chatMessageByRoomId.get(0).getMessageType()).isEqualTo(MessageType.ENTER);
        assertThat(chatMessageByRoomId.get(0).getSenderId()).isEqualTo(user1.getId());
        assertThat(chatRoomRepository.findById(room.getRoomId()).isPresent()).isTrue();
        assertThat(chatRoomUserRepository.findByChatRoomUserByUserId(user1.getId()).get(0).getChatRoom().getId()).isEqualTo(room.getRoomId());
        assertThat(chatRoomUserRepository.findByChatRoomUserByUserId(user2.getId()).get(0).getChatRoom().getId()).isEqualTo(room.getRoomId());
    }





}