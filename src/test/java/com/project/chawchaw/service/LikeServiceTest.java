package com.project.chawchaw.service;

import com.project.chawchaw.entity.Like;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.like.LikeRepository;
import com.project.chawchaw.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class LikeServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LikeService likeService;

    @Autowired
    LikeRepository likeRepository;

    @BeforeEach
    public void beforeEach()throws Exception{

        User user1 = User.createUser("11", "11", "11", "11", "11", "11", "11");
        User user2 = User.createUser("22", "22", "22", "22", "22", "22", "22");

        em.persist(user1);
        em.persist(user2);
    }

    /**
     * user1 -> user2팔로우
     * **/
    @Test
    public void like()throws Exception{
        //given
        User user1= userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
        User user2= userRepository.findByEmail("22").orElseThrow(UserNotFoundException::new);
        //when
        likeService.like(user2.getId(),user1.getId());
        Optional<Like> byFollow = likeRepository.findByLike(user1.getId(), user2.getId());

        //then
        assertThat(byFollow.isPresent()).isTrue();
        assertThat(user2.getToLikes().get(0).getFromUser().getId()).isEqualTo(user1.getId());
        assertThat(user2.getToLikes().get(0).getToUser().getId()).isEqualTo(user2.getId());
        assertThat(user2.getToLikes().size()).isEqualTo(1);

    }
    @Test
    public void unlike()throws Exception{
       //given
        User user1= userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
        User user2= userRepository.findByEmail("22").orElseThrow(UserNotFoundException::new);
        likeService.like(user2.getId(),user1.getId());
       //when
       likeService.unLike(user2.getId(), user1.getId());
        Optional<Like> byFollow = likeRepository.findByLike(user1.getId(), user2.getId());
       //then
        assertThat(byFollow.isPresent()).isFalse();
        assertThat(user2.getToLikes().isEmpty()).isTrue();
        assertThat(user2.getToLikes().size()).isEqualTo(0);
    }
}