package com.project.chawchaw.repository.follow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chawchaw.dto.chat.ChatMessageDto;
import com.project.chawchaw.dto.chat.MessageType;
import com.project.chawchaw.dto.follow.FollowAlarmDto;
import com.project.chawchaw.dto.follow.FollowType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Repository
public class FollowAlarmRepository {
    private final RedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    public void createFollowAlarm(FollowAlarmDto followAlarmDto,Long toUserId){
      String key =toUserId.toString()+":follow"+ "_" + UUID.randomUUID().toString();


      if(key!=null)
          redisTemplate.opsForValue().set(key,followAlarmDto);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    public List<FollowAlarmDto> getFollowAlarmByUserId(Long toUserId, LocalDateTime lastLogOut){
        List<FollowAlarmDto>followAlarmDtos=new ArrayList<>();
        if(lastLogOut==null){
            return followAlarmDtos;
        }
        Set<String> keys = redisTemplate.keys(toUserId.toString()+":follow"+"_"+"*");


        for(String key:keys){

            FollowAlarmDto followAlarmDto = objectMapper.convertValue(redisTemplate.opsForValue().get(key), FollowAlarmDto.class);
            if(followAlarmDto.getRegDate().isAfter(lastLogOut))
                followAlarmDtos.add(followAlarmDto);
        }

        Collections.sort(followAlarmDtos, (c1, c2)-> {

            return  c2.getRegDate().compareTo(c1.getRegDate());
        });

        return followAlarmDtos.stream()

                .collect(Collectors.toList());

    }

}
