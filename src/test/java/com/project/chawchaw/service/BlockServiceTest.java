package com.project.chawchaw.service;

import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Table;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BlockServiceTest {


    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BlockService blockService;

    @BeforeEach
    public void beforeEach()throws Exception{
        User user1 = User.createUser("11", "11", "11", "11", "11", "11", "11");
        User user2 = User.createUser("22", "22", "22", "22", "22", "22", "22");

        em.persist(user1);
        em.persist(user2);
    }

    /**
     * user1->user2 차단
     * **/
    @Test
    public void createBlock()throws Exception{
       //given
        User user1 = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
        User user2 = userRepository.findByEmail("22").orElseThrow(UserNotFoundException::new);
        //when
        blockService.createBlock(user1.getId(),user2.getId());
        em.flush(); em.clear();
        User findUser = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
       //then
        assertThat(findUser.getBlockList().get(0).getToUser().getId()).isEqualTo(user2.getId());

    }
    @Test
    public void deleteBlock()throws Exception{
       //given
        User user1 = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
        User user2 = userRepository.findByEmail("22").orElseThrow(UserNotFoundException::new);
        //when
        blockService.createBlock(user1.getId(),user2.getId());
        em.flush(); em.clear();

        blockService.deleteBlock(user1.getId(), user2.getId());
        em.flush(); em.clear();
        User findUser = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
       //when

       //then
        assertThat(findUser.getBlockList().isEmpty()).isTrue();
    }
}