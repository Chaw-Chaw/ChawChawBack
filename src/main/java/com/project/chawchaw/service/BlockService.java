package com.project.chawchaw.service;

import com.project.chawchaw.dto.user.UsersDto;
import com.project.chawchaw.entity.Block;
import com.project.chawchaw.entity.Follow;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.BlockAlreadyExistException;
import com.project.chawchaw.exception.BlockNotFoundException;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.BlockRepository;
import com.project.chawchaw.repository.follow.FollowRepository;
import com.project.chawchaw.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;

    /**
     * 유저 차단
     * 차단시 유저 양쪽 팔로우 모두 삭제제**/
   @Transactional
    public void createBlock(Long fromUserId,Long toUserId){
        if(blockRepository.isBlock(fromUserId, toUserId).isPresent()){
            throw new BlockAlreadyExistException();
        }
       for (Follow follow : followRepository.findByFollowWithUserId(fromUserId, toUserId)) {
           followRepository.delete(follow);
       }


        User fromUser = userRepository.findById(fromUserId).orElseThrow(UserNotFoundException::new);
        User toUser = userRepository.findById(toUserId).orElseThrow(UserNotFoundException::new);
        Block block = Block.createBlock(fromUser, toUser);
        blockRepository.save(block);

    }

    @Transactional
    public void deleteBlock(Long fromUserId,Long toUserId){
        Block block = blockRepository.isBlock(fromUserId, toUserId).orElseThrow(BlockNotFoundException::new);
        blockRepository.delete(block);
    }

    /**
     * 차단 유저 목록 조회**/
    public List<UsersDto> getBlockList(Long userId){
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return user.getBlockList().stream().map(b->new UsersDto(b.getToUser().getId(), b.getToUser().getName(),b.getToUser().getImageUrl() )).collect(Collectors.toList());
    }





}
