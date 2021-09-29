package com.project.chawchaw.repository.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chawchaw.dto.like.LikeAlarmDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Repository
public class LikeAlarmRepository {
    private final RedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    public void createLikeAlarm(LikeAlarmDto likeAlarmDto, Long toUserId){
      String key ="like::"+toUserId.toString()+ "_" + UUID.randomUUID().toString();


      if(key!=null)
          redisTemplate.opsForValue().set(key, likeAlarmDto);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }
    public void deleteLikeAlarmByUserId(Long userId){
        Set<String> keys = redisTemplate.keys("like::"+userId.toString()+"_"+"*");
        redisTemplate.delete(keys);

    }
    public List<LikeAlarmDto> getLikeAlarmByUserId(Long toUserId, LocalDateTime lastLogOut){
        List<LikeAlarmDto> likeAlarmDtos =new ArrayList<>();
        if(lastLogOut==null){
            return likeAlarmDtos;
        }
        Set<String> keys = redisTemplate.keys("like::"+toUserId.toString()+"_"+"*");


        for(String key:keys){

            LikeAlarmDto likeAlarmDto = objectMapper.convertValue(redisTemplate.opsForValue().get(key), LikeAlarmDto.class);
            if(likeAlarmDto.getRegDate().isAfter(lastLogOut))
                likeAlarmDtos.add(likeAlarmDto);
        }

        Collections.sort(likeAlarmDtos, (c1, c2)-> {

            return  c2.getRegDate().compareTo(c1.getRegDate());
        });

        return likeAlarmDtos.stream()

                .collect(Collectors.toList());

    }

}
