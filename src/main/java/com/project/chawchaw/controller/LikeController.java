package com.project.chawchaw.controller;

import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.config.response.DefaultResponseVo;
import com.project.chawchaw.config.response.ResponseMessage;
import com.project.chawchaw.dto.user.UserRequestDto;
import com.project.chawchaw.service.LikeService;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController

public class LikeController {

    private final LikeService likeService;
    private final JwtTokenProvider jwtTokenProvider;



    @PostMapping("/like")
    public ResponseEntity like(@RequestBody UserRequestDto userRequestDto , @RequestHeader(value ="Authorization")String token){

        Long fromUserId = Long.valueOf(jwtTokenProvider.getUserPk(token));
        likeService.like(userRequestDto.getUserId(),fromUserId);
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LIKE_SUCCESS,true),HttpStatus.CREATED);

    }
    @DeleteMapping("like")
    public ResponseEntity unLike(@RequestBody UserRequestDto userRequestDto,@RequestHeader(value="Authorization")String token){
        Long fromUserId = Long.valueOf(jwtTokenProvider.getUserPk(token));
        likeService.unLike(userRequestDto.getUserId(),fromUserId);
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.UNLIKE_SUCCESS,true),HttpStatus.CREATED);

    }
}
