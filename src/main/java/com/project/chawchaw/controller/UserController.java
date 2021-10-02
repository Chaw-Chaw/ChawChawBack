package com.project.chawchaw.controller;


import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.config.response.DefaultResponseVo;
import com.project.chawchaw.config.response.ResponseMessage;
import com.project.chawchaw.dto.alarm.AlarmDto;
import com.project.chawchaw.dto.user.*;
import com.project.chawchaw.service.BlockService;
import com.project.chawchaw.service.LikeService;
import com.project.chawchaw.service.S3Service;
import com.project.chawchaw.service.UserService;

import com.project.chawchaw.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final S3Service s3Service;
    private final LikeService likeService;
    private final BlockService blockService;

    @Value("${file.path}")
    private String fileRealPath;

    @Value("${file.defaultImage}")
    private String defaultImage;

    private final ChatService chatService;

    public static final String CLOUD_FRONT_DOMAIN_NAME = "d3t4l8y7wi01lo.cloudfront.net";



    @GetMapping(value = "/users/{userId}")
    public ResponseEntity userDetail(@PathVariable("userId") Long userId,
//                                     @AuthenticationPrincipal CustomUserDetails customUserDetails

    @RequestHeader("Authorization")String token){


        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.READ_USER_SUCCESS,true,
                userService.detailUser(userId, Long.valueOf(jwtTokenProvider.getUserPk(token))))
                ,HttpStatus.OK);

    }

    @GetMapping(value = "/users/profile")
    public ResponseEntity<UserProfileDto> userProfile(@RequestHeader("Authorization")String token){
        logger.info(token);
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.READ_USER_SUCCESS,true,
                userService.userProfile(Long.valueOf(jwtTokenProvider.getUserPk(token)))),HttpStatus.OK);


    }

    @PostMapping(value = "/users/profile")
    public ResponseEntity userProfileUpdate( @RequestBody @Valid UserUpdateDto userUpdateDto,
                                            @RequestHeader("Authorization")String token){


        if(userService.userProfileUpdate(userUpdateDto,Long.valueOf(jwtTokenProvider.getUserPk(token)))) {

            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.UPDATE_USER_SUCCESS, true), HttpStatus.OK);
        }else{
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SET_REP, false), HttpStatus.OK);
        }

    }
    @GetMapping(value = "/users")
    public ResponseEntity users(@ModelAttribute @Valid UserSearch userSearch, @RequestHeader("Authorization")String token, HttpServletRequest request){

        Cookie[] cookies = request.getCookies();

        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("exclude")&&!cookie.getValue().isEmpty()) {
                    String[] exclude = cookie.getValue().split("/");
                    for (int i = 0; i < exclude.length; i++) {
                        userSearch.getExcludes().add(Long.parseLong(exclude[i]));
                    }
                }
            }
        }
        System.out.println( userService.users(userSearch,Long.valueOf(jwtTokenProvider.getUserPk(token))).isEmpty());
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.READ_USER_SUCCESS,true,
                userService.users(userSearch,Long.valueOf(jwtTokenProvider.getUserPk(token)))),HttpStatus.OK);

    }

//    @PostMapping(value = "/users/image")
//    public ResponseEntity profileImageUpload(@RequestBody MultipartFile file,@RequestHeader("Authorization") String token){
//
//            String imageUrl = userService.fileUpload(file, Long.valueOf(jwtTokenProvider.getUserPk(token)));
//            if(imageUrl.isEmpty()){
//
//                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_UPLOAD_FAIL, false), HttpStatus.OK);
//
//
//            }
//            else {
//                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_UPLOAD_SUCCESS, true,
//                        imageUrl), HttpStatus.OK);
//
//            }
        @PostMapping(value = "/users/image")
        public ResponseEntity profileImageUpload(@RequestBody MultipartFile file,@RequestHeader("Authorization") String token){

        try {
            String imageUrl = s3Service.profileImageUpload(file,Long.valueOf(jwtTokenProvider.getUserPk(token)));
            if (imageUrl.isEmpty()) {

                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_UPLOAD_FAIL, false), HttpStatus.OK);


            } else {
                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_UPLOAD_SUCCESS, true,
                        imageUrl), HttpStatus.OK);

            }
        }catch (Exception e){

            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_UPLOAD_FAIL, false), HttpStatus.OK);
        }


//         String s=userService.fileUpload(file, Long.valueOf(jwtTokenProvider.getUserPk(token)));


//        String fileUri = ServletUriComponentsBuilder.fromPath("https://chawchaw-service.best")
//                .path("/users/image?imageUrl=")
//                .path(fileName)
//                .toUriString();

//        return s;


    }

    /**
     * Aws s3사용으로 필요하지 않음**/

//    @GetMapping("/users/image")
//    public ResponseEntity getUserImage(@RequestParam String imageUrl,@RequestHeader("Authorization")String token) {
//        byte[] result = null;
//        HttpHeaders header = new HttpHeaders();
//
//        try {
//            File file = new File(fileRealPath + File.separator + imageUrl);
//            header.add("Content-Type", Files.probeContentType(file.toPath()));
//            result = FileCopyUtils.copyToByteArray(file);
//
//        } catch (Exception e) {
//            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_GET_FAIL, false), HttpStatus.OK);
//        }
//        return new ResponseEntity(result, header, HttpStatus.OK);
//    }







    @DeleteMapping(value = "/users/image")
    public ResponseEntity profileImageDelete( @RequestHeader("Authorization") String token) {

            if (s3Service.deleteImage(Long.valueOf(jwtTokenProvider.getUserPk(token)))) {
                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_DELETE_SUCCESS, true,"https://"+CLOUD_FRONT_DOMAIN_NAME+"/"+defaultImage), HttpStatus.OK);
            } else {
                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.IMAGE_DELETE_FAIL, false), HttpStatus.OK);

            }
    }

    @GetMapping("/users/alarm")
    public ResponseEntity getAlarm(@RequestHeader("Authorization")String token) {
        Long userId = Long.valueOf(jwtTokenProvider.getUserPk(token));

        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.ALARM_FIND_SUCCESS, true,
                new AlarmDto(chatService.getChatMessageByIsRead(userId),
                        likeService.getLikeAlarm(userId))), HttpStatus.OK);

    }



//    @ApiOperation(value = "프로필 정보 삭제",notes = "자신의 프로필 정보를 삭제한다.")
//    @DeleteMapping (value = "/users/{content}")
//    public CommonResult userProfileDelete(@PathVariable("content")String content, @ModelAttribute UserUpdateDto userUpdateDto,
//                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
//
//        userService.userProfileDelete(content,userUpdateDto,1L);
//        return responseService.getSuccessResult();
//
//    }

    /**
     * 유저 차단 기능**/

    @PostMapping("/users/block")
    public ResponseEntity createUserBlock(@RequestHeader("Authorization")String token, @RequestBody UserRequestDto requestDto) {

        Long userId = Long.valueOf(jwtTokenProvider.getUserPk(token));
        blockService.createBlock(userId,requestDto.getUserId());
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.BLOCK_CREATE_SUCCESS,
                true), HttpStatus.OK);
    }
    @DeleteMapping("/users/block")
    public ResponseEntity deleteUserBlock(@RequestHeader("Authorization")String token, @RequestBody UserRequestDto requestDto) {

        Long userId = Long.valueOf(jwtTokenProvider.getUserPk(token));
        blockService.deleteBlock(userId,requestDto.getUserId());
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.BLOCK_DELETE_SUCCESS,
                true), HttpStatus.OK);
    }

    @GetMapping("/users/block")
    public ResponseEntity getUserBlock(@RequestHeader("Authorization")String token) {
        Long userId = Long.valueOf(jwtTokenProvider.getUserPk(token));
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.BLOCK_FIND_SUCCESS,
                true,blockService.getBlockList(userId)), HttpStatus.OK);

    }




}
