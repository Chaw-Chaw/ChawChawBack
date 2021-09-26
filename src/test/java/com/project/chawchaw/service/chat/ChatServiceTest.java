package com.project.chawchaw.service.chat;

import com.google.gson.Gson;
import com.project.chawchaw.dto.chat.ChatDto;
import com.project.chawchaw.dto.chat.ChatMessageDto;
import com.project.chawchaw.dto.chat.ChatRoomDto;
import com.project.chawchaw.dto.chat.MessageType;
import com.project.chawchaw.dto.user.UserUpdateDto;
import com.project.chawchaw.entity.Country;
import com.project.chawchaw.entity.Language;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.chat.ChatMessageRepository;
import com.project.chawchaw.repository.chat.ChatRoomRepository;
import com.project.chawchaw.repository.chat.ChatRoomUserRepository;
import com.project.chawchaw.repository.user.UserRepository;
import com.project.chawchaw.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.persistence.EntityManager;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
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


    @LocalServerPort Integer port;
    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;
    static final String WEBSOCKET_TOPIC = "/queue/chat/";

    @BeforeEach
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

        User user1 = User.createUser("11", "11", "11", "11", "11", "11", "11");
        User user2 = User.createUser("22", "22", "22", "22", "22", "22", "22");

        em.persist(user1);
        em.persist(user2);
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

        userService.userProfileUpdate(userUpdateDto,user1.getId());
        userService.userProfileUpdate(userUpdateDto,user2.getId());


        /**
         * stomp 설정
         * **/

        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    /**
     * 채팅방생성 user1->user2
     */
    @Test
    public void createChatRoom()throws Exception{
       //given
        User user1 = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
        User user2 = userRepository.findByEmail("22").orElseThrow(UserNotFoundException::new);

       //when
        ChatRoomDto room = chatService.createRoom(user2.getId(), user1.getId());
        em.flush(); em.clear();
        List<ChatMessageDto> chatMessageByRoomId = chatMessageRepository.findChatMessageByRoomId(room.getRoomId(),null);


        //then
        assertThat(chatMessageByRoomId.get(0).getMessageType()).isEqualTo(MessageType.ENTER);
        assertThat(chatMessageByRoomId.get(0).getSenderId()).isEqualTo(user1.getId());
        assertThat(chatRoomRepository.findById(room.getRoomId()).isPresent()).isTrue();
        assertThat(chatRoomUserRepository.findByChatRoomUserByUserId(user1.getId()).get(0).getChatRoom().getId()).isEqualTo(room.getRoomId());
        assertThat(chatRoomUserRepository.findByChatRoomUserByUserId(user2.getId()).get(0).getChatRoom().getId()).isEqualTo(room.getRoomId());
    }




    /**
     * 채팅메세지 발송시 소켓통신 으로 수신되는지
     * user1->user2**/
    @Test
    public void sendChatMessage()throws Exception{
       //given
        User user1 = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
        User user2 = userRepository.findByEmail("22").orElseThrow(UserNotFoundException::new);
        ChatRoomDto room = chatService.createRoom(user2.getId(), user1.getId());
       //when
        StompHeaders headers = new StompHeaders();
//        headers.add("token", sender.getToken());
        StompSession session = stompClient
                .connect(getWsPath(), new WebSocketHttpHeaders() ,headers, new StompSessionHandlerAdapter() {})
                .get(15, SECONDS);
        session.subscribe(WEBSOCKET_TOPIC + user2.getId(), new DefaultStompFrameHandler());

        // when
//        MessageCreateRequestDto requestDto = new MessageCreateRequestDto(sender.getId(), receiver.getId(), "MESSAGE TEST");
        ChatMessageDto chatMessageDto=new ChatMessageDto(MessageType.TALK, room.getRoomId(), user1.getId(),user1.getName(),"message",null,null,false);
//        MessageDto messageDto = messageService.createMessage(requestDto);
        chatService.enterChatRoom(room.getRoomId());
        chatService.publish(chatService.getTopic(room.getRoomId()),chatMessageDto);

        // then
        String jsonResult = blockingQueue.poll(15, SECONDS);
        Map<String, String> result = gson.fromJson(jsonResult, new HashMap().getClass());
        assertThat(result.get("message")).isEqualTo(chatMessageDto.getMessage());

       //then
    }
    private String getWsPath() {
        return String.format("ws://localhost:%d/ws", port);
    }

    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer(new String((byte[]) o));
        }
    }


}