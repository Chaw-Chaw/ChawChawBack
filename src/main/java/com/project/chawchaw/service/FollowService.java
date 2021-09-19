package com.project.chawchaw.service;

import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.dto.follow.FollowAlarmDto;
import com.project.chawchaw.dto.follow.FollowType;
import com.project.chawchaw.entity.Follow;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.BlockAlreadyExistException;
import com.project.chawchaw.exception.FollowAlreadyException;
import com.project.chawchaw.exception.FollwNotFoundException;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.BlockRepository;
import com.project.chawchaw.repository.follow.FollowAlarmRepository;
import com.project.chawchaw.repository.follow.FollowRepository;
import com.project.chawchaw.repository.chat.ChatMessageRepository;
import com.project.chawchaw.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final FollowAlarmRepository followAlarmRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final BlockRepository blockRepository;


    public void follow(Long toUserId, Long fromUserId) {

        if(followRepository.findByFollow(fromUserId,toUserId).isPresent()){
            throw new FollowAlreadyException();
        }
        if(blockRepository.isBlockWithUserId(toUserId,fromUserId).isPresent()){
            throw new BlockAlreadyExistException();
        }
        User fromUser = userRepository.findById(fromUserId).orElseThrow(UserNotFoundException::new);
        User toUser = userRepository.findById(toUserId).orElseThrow(UserNotFoundException::new);
        followRepository.save(Follow.createFollow(fromUser, toUser));
        FollowAlarmDto followAlarmDto = new FollowAlarmDto(FollowType.FOLLOW, fromUser.getName(), LocalDateTime.now().withNano(0));
        followAlarmRepository.createFollowAlarm(followAlarmDto,toUserId);
        messagingTemplate.convertAndSend("/queue/follow/" + toUserId,followAlarmDto );
    }

    public void unFollow(Long toUserId, Long fromUserId) {


        User fromUser = userRepository.findById(fromUserId).orElseThrow(UserNotFoundException::new);
        Follow follow = followRepository.findByFollow(fromUserId, toUserId).orElseThrow(FollwNotFoundException::new);
        followRepository.delete(follow);
        FollowAlarmDto followAlarmDto = new FollowAlarmDto(FollowType.UNFOLLOW, fromUser.getName(), LocalDateTime.now().withNano(0));
        followAlarmRepository.createFollowAlarm(followAlarmDto,toUserId);
        messagingTemplate.convertAndSend("/queue/follow/" + toUserId,followAlarmDto );
    }
    public List<FollowAlarmDto> getFollowAlarm(Long toUserId){
        User toUser = userRepository.findById(toUserId).orElseThrow(UserNotFoundException::new);
        return followAlarmRepository.getFollowAlarmByUserId(toUserId,toUser.getLastLogOut());

    }
}