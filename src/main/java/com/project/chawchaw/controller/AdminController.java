package com.project.chawchaw.controller;

import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.config.response.DefaultResponseVo;
import com.project.chawchaw.config.response.ResponseMessage;
import com.project.chawchaw.dto.admin.AdminUserSearch;
import com.project.chawchaw.dto.admin.UserUpdateByAdminDto;
import com.project.chawchaw.dto.user.UserRequestDto;
import com.project.chawchaw.dto.user.UserUpdateDto;
import com.project.chawchaw.service.S3Service;
import com.project.chawchaw.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Service s3Service;
    public static final String CLOUD_FRONT_DOMAIN_NAME = "d3t4l8y7wi01lo.cloudfront.net";
    @Value("${file.defaultImage}")
    private String defaultImage;
    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @DeleteMapping(value = "/admin/users")
    public ResponseEntity deleteUsersByAdmin(@RequestHeader("Authorization") String token, @RequestBody UserRequestDto userRequestDto) {

        Long adminId = Long.valueOf(jwtTokenProvider.getUserPk(token));
        userService.deleteUserByAdmin(adminId, userRequestDto.getUserId());
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.DELETE_USER_SUCCESS, true), HttpStatus.OK);

    }

    @PostMapping(value = "/admin/users/profile")
    public ResponseEntity updateUserByAdmin(@RequestHeader("Authorization") String token, @RequestBody UserUpdateByAdminDto userRequestDto) {
        Long adminId = Long.valueOf(jwtTokenProvider.getUserPk(token));

        if(userService.updateUserByAdmin(userRequestDto)){

            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.UPDATE_USER_SUCCESS, true), HttpStatus.OK);
        }else{
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SET_REP, false), HttpStatus.OK);
        }




    }

    @GetMapping(value = "/admin/users")
    public ResponseEntity getUsersByAdmin(@RequestHeader("Authorization") String token,
                                          @RequestBody AdminUserSearch adminUserSearch, @NotNull Pageable pageable) {

        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.READ_USER_SUCCESS, true
                , userService.usersByAdmin(adminUserSearch, pageable)), HttpStatus.OK);


    }

    @GetMapping(value = "/admin/users/{id}")
    public ResponseEntity userDetailByAdmin(@RequestHeader("Authorization") String token,
                                            @PathVariable("id") Long id) {

        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.READ_USER_SUCCESS, true
                , userService.detailUserByAdmin(id)), HttpStatus.OK);


    }

    @PostMapping(value = "/admin/users/image")
    public ResponseEntity profileImageUpload(@RequestBody MultipartFile file, @RequestHeader("Authorization") String token,
                                             @RequestBody UserRequestDto requestDto) {

        try {
            String imageUrl = s3Service.profileImageUpload(file, requestDto.getUserId());
            if (imageUrl.isEmpty()) {

                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_UPLOAD_FAIL, false), HttpStatus.OK);


            } else {
                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_UPLOAD_SUCCESS, true,
                        imageUrl), HttpStatus.OK);

            }
        } catch (Exception e) {

            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_UPLOAD_FAIL, false), HttpStatus.OK);
        }


    }
    @DeleteMapping(value = "admin//users/image")
    public ResponseEntity profileImageDelete( @RequestHeader("Authorization") String token,@RequestBody UserRequestDto requestDto) {

        if (s3Service.deleteImage(requestDto.getUserId())) {
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_DELETE_SUCCESS, true,"https://"+CLOUD_FRONT_DOMAIN_NAME+"/"+defaultImage), HttpStatus.OK);
        } else {
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_DELETE_FAIL, false), HttpStatus.OK);

        }
    }
}
