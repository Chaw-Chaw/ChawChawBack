package com.project.chawchaw.controller;

import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.config.response.DefaultResponseVo;
import com.project.chawchaw.config.response.ResponseMessage;
import com.project.chawchaw.dto.admin.AdminUserSearch;
import com.project.chawchaw.dto.admin.UserUpdateByAdminDto;
import com.project.chawchaw.dto.user.UserRequestDto;
import com.project.chawchaw.dto.user.UserUpdateDto;
import com.project.chawchaw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor

public class AdminController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @DeleteMapping(value = "/admin/users")
    public ResponseEntity deleteUsersByAdmin(@RequestHeader("Authorization")String token, @RequestBody UserRequestDto userRequestDto){

        Long adminId = Long.valueOf(jwtTokenProvider.getUserPk(token));
        userService.deleteUserByAdmin(adminId,userRequestDto.getUserId());
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.DELETE_USER_SUCCESS, true), HttpStatus.OK);

    }
    @PostMapping(value = "/admin/users/profile")
    public ResponseEntity updateUserByAdmin( @RequestHeader("Authorization")String token, @RequestBody UserUpdateByAdminDto userRequestDto){
        Long adminId = Long.valueOf(jwtTokenProvider.getUserPk(token));
        userService.updateUserByAdmin(userRequestDto);
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.UPDATE_USER_SUCCESS, true), HttpStatus.OK);


    }
    @GetMapping(value = "/admin/users")
    public ResponseEntity getUsersByAdmin(@RequestHeader("Authorization")String token,
                                          @RequestBody AdminUserSearch adminUserSearch, @NotNull Pageable pageable){


        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.READ_USER_SUCCESS, true
        ,userService.usersByAdmin(adminUserSearch,pageable)), HttpStatus.OK);


    }

}
