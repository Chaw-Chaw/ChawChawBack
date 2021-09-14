package com.project.chawchaw.service.chat;

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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
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
        List<ChatMessageDto> chatMessageByRoomId = chatMessageRepository.findChatMessageByRoomId(room.getRoomId());


        //then
        assertThat(chatMessageByRoomId.get(0).getMessageType()).isEqualTo(MessageType.ENTER);
        assertThat(chatMessageByRoomId.get(0).getSenderId()).isEqualTo(user1.getId());
        assertThat(chatRoomRepository.findById(room.getRoomId()).isPresent()).isTrue();
        assertThat(chatRoomUserRepository.findByChatRoomUserByUserId(user1.getId()).get(0).getChatRoom().getId()).isEqualTo(room.getRoomId());
        assertThat(chatRoomUserRepository.findByChatRoomUserByUserId(user2.getId()).get(0).getChatRoom().getId()).isEqualTo(room.getRoomId());
    }


}