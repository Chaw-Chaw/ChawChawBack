package com.project.chawchaw.service;

import com.project.chawchaw.dto.like.LikeAlarmDto;
import com.project.chawchaw.dto.like.LikeType;
import com.project.chawchaw.entity.Like;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.BlockAlreadyExistException;
import com.project.chawchaw.exception.LikeAlreadyException;
import com.project.chawchaw.exception.LikeNotFoundException;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.BlockRepository;
import com.project.chawchaw.repository.like.LikeAlarmRepository;
import com.project.chawchaw.repository.like.LikeRepository;
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
public class LikeService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final LikeAlarmRepository likeAlarmRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final BlockRepository blockRepository;


    public void like(Long toUserId, Long fromUserId) {

        if(likeRepository.findByLike(fromUserId,toUserId).isPresent()){
            throw new LikeAlreadyException();
        }
        if(blockRepository.isBlockWithUserId(toUserId,fromUserId).isPresent()){
            throw new BlockAlreadyExistException();
        }
        User fromUser = userRepository.findById(fromUserId).orElseThrow(UserNotFoundException::new);
        User toUser = userRepository.findById(toUserId).orElseThrow(UserNotFoundException::new);
        likeRepository.save(Like.createLike(fromUser, toUser));
        LikeAlarmDto likeAlarmDto = new LikeAlarmDto(LikeType.LIKE, fromUser.getName(), LocalDateTime.now().withNano(0));
        likeAlarmRepository.createLikeAlarm(likeAlarmDto,toUserId);
        messagingTemplate.convertAndSend("/queue/like/" + toUserId, likeAlarmDto);
    }

    public void unLike(Long toUserId, Long fromUserId) {


        if(blockRepository.isBlockWithUserId(toUserId,fromUserId).isPresent()){
            throw new BlockAlreadyExistException();
        }
        User fromUser = userRepository.findById(fromUserId).orElseThrow(UserNotFoundException::new);
        Like like = likeRepository.findByLike(fromUserId, toUserId).orElseThrow(LikeNotFoundException::new);
        likeRepository.delete(like);
        LikeAlarmDto likeAlarmDto = new LikeAlarmDto(LikeType.UNLIKE, fromUser.getName(), LocalDateTime.now().withNano(0));
        likeAlarmRepository.createLikeAlarm(likeAlarmDto,toUserId);
        messagingTemplate.convertAndSend("/queue/like/" + toUserId, likeAlarmDto);
    }
    public List<LikeAlarmDto> getLikeAlarm(Long toUserId){
        User toUser = userRepository.findById(toUserId).orElseThrow(UserNotFoundException::new);
        return likeAlarmRepository.getLikeAlarmByUserId(toUserId,toUser.getLastLogOut());

    }
}