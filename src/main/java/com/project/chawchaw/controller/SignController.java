package com.project.chawchaw.controller;

import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.config.response.DefaultResponseVo;
import com.project.chawchaw.config.response.ResponseMessage;
import com.project.chawchaw.dto.mail.MailRequestDto;
import com.project.chawchaw.dto.social.FaceBookProfile;
import com.project.chawchaw.dto.social.KakaoProfile;
import com.project.chawchaw.dto.social.SocialLoginResponseDto;
import com.project.chawchaw.dto.user.*;
import com.project.chawchaw.config.auth.CustomUserDetails;
import com.project.chawchaw.exception.LoginFailureException;
import com.project.chawchaw.service.*;
import com.project.chawchaw.service.chat.ChatService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@RequiredArgsConstructor
@RestController
public class SignController {


    private final SignService signService;
    private final ResponseService responseService;
    private final KakaoService kakaoService;
    private final FaceBookService faceBookService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;
    private final S3Service s3Service;







    @PostMapping("/mail/send") // 이메일 인증 코드 보내기
    public ResponseEntity emailAuth(@RequestBody @Valid MailRequestDto mailRequestDto, HttpServletRequest request) throws Exception {
        HttpSession session=request.getSession();
        String email=mailRequestDto.getEmail();

        session.setAttribute(email,signService.sendSimpleMessage(email));
        session.setMaxInactiveInterval(180);
        System.out.println(session.getAttribute(email));

        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SEND_MAIL,true),HttpStatus.OK);
    }


    //인증 번호를 db에 저장할지 따로 쿠키에 저장할지


    @PostMapping("/mail/verification") // 이메일 인증 코드 검증
    public ResponseEntity verifyCode(@RequestBody @Valid MailRequestDto mailRequestDto, HttpServletRequest request) {

        HttpSession session=request.getSession();
        Object attribute = session.getAttribute(mailRequestDto.getEmail());

        if (attribute==null) {

            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.VERIFICATION_MAIL_FAIL,false),HttpStatus.OK);
        }
        else {
          if(attribute.toString().equals(mailRequestDto.getVerificationNumber())){
              session.invalidate();
              return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.VERIFICATION_MAIL_SUCCESS,true),HttpStatus.OK);

          }
          else{
              return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.VERIFICATION_MAIL_FAIL,false),HttpStatus.OK);
          }
        }
    }





    @PostMapping(value = "/users/signup")
    public ResponseEntity signup(@RequestBody @Valid UserSignUpRequestDto requestDto){
        if(requestDto.getProvider()==null||requestDto.getProvider().isEmpty()){
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CREATED_USER_FAIL,true),HttpStatus.OK);
        }


            if(requestDto.getProvider().equals("kakao")||requestDto.getProvider().equals("facebook")) {
                requestDto.setPassword(requestDto.getEmail());

                signService.signup(requestDto);
                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CREATED_USER,true),HttpStatus.CREATED);
            }
            else{
                signService.signup(requestDto);
                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CREATED_USER,true),HttpStatus.CREATED);
            }


    }



    @GetMapping(value = "/users/email/duplicate/{email}")
    public ResponseEntity emailDuplicate(@PathVariable("email") String email){
       return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.EMAIL_DUPLICATE,signService.userCheck(email)),HttpStatus.OK);


    }


//    @ApiOperation(value = "로그인",notes = "로그인")
//    @PostMapping(value = "/login")
//    public ResponseEntity login(@ModelAttribute UserLoginRequestDto requestDto
//    , HttpServletResponse response){
//
//        String token = signService.login(requestDto).getToken();
//        response.addHeader("Authorization","Bearer "+token);
//        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_SUCCESS,true,signService.getImageUrl(token)),HttpStatus.OK);
//
//
//
//
//    }


    @PostMapping(value = "/login")
    public ResponseEntity login(
            @RequestBody UserLoginRequestDto requestDto,HttpServletResponse response) {
        try {

            /**
             * provider basic이 아닐 때**/
            if (requestDto.getProvider().equals("kakao")||requestDto.getProvider().equals("facebook")) {

                if (requestDto.getProvider().equals("kakao") && requestDto.getKakaoToken() != null) {
                    String token = kakaoService.getKakaoTokenInfo(requestDto.getKakaoToken()).getAccess_token();
                    KakaoProfile kakaoProfile = kakaoService.getKakaoProfile(token);
                    String email = kakaoProfile.getEmail();
                    if (signService.validUserWithProvider(email, requestDto.getProvider())) {
                        UserLoginResponseDto userLoginResponseDto = signService.loginByProvider(email, requestDto.getProvider());

                        Cookie cookie = new Cookie("RefreshToken",userLoginResponseDto.getToken().getRefreshToken());
                        response.addCookie(cookie);

                        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_SUCCESS, true,userLoginResponseDto), HttpStatus.OK);
                    }
                    else {
                        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SOCIAL_LOGIN_FAIL, false,
                                new SocialLoginResponseDto(kakaoProfile.getEmail(), kakaoProfile.getName(), kakaoProfile.getImageUrl(), kakaoProfile.getProvider())), HttpStatus.OK);
                    }
                }

                else if (requestDto.getProvider().equals("facebook") && requestDto.getFacebookId() != null
                        && requestDto.getFacebookToken() != null) {

                    FaceBookProfile faceBookProfile = faceBookService.getFaceBookProfile(requestDto.getFacebookToken(), requestDto.getFacebookId());
                    String email = faceBookProfile.getEmail();
                    if (signService.validUserWithProvider(email, requestDto.getProvider())) {
                        UserLoginResponseDto userLoginResponseDto = signService.loginByProvider(email, requestDto.getProvider());

                        Cookie cookie = new Cookie("RefreshToken",userLoginResponseDto.getToken().getRefreshToken());
                        response.addCookie(cookie);

                        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_SUCCESS, true,userLoginResponseDto), HttpStatus.OK);
                    }
                    else {
                        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SOCIAL_LOGIN_FAIL, false,
                                new SocialLoginResponseDto(faceBookProfile.getEmail(), faceBookProfile.getName(), faceBookProfile.getImageUrl(), faceBookProfile.getProvider())), HttpStatus.OK);
                    }
                }
                else {

                    return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SOCIAL_LOGIN_CONNECT_FAIL, false), HttpStatus.OK);

                }

            }
            else if(requestDto.getProvider().equals("basic")){
              if(requestDto.getEmail()!=null&&requestDto.getPassword()!=null){

                  UserLoginResponseDto userLoginResponseDto = signService.login(requestDto);


                  Cookie cookie = new Cookie("RefreshToken",userLoginResponseDto.getToken().getRefreshToken());
                  response.addCookie(cookie);

                  return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_SUCCESS, true,userLoginResponseDto), HttpStatus.OK);
                }
              else{
                  return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_FAIL, false), HttpStatus.OK);
              }
            }

            else {
                return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SOCIAL_LOGIN_CONNECT_FAIL, false), HttpStatus.OK);
            }
        }
        catch(LoginFailureException e){
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_FAIL, false), HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SOCIAL_LOGIN_CONNECT_FAIL, false), HttpStatus.OK);
        }



    }
//    @ApiOperation(value = "소셜 계정 가입", notes = "소셜 계정 회원가입을 한다.")
//    @PostMapping(value = "/users/signup/{provider}")
//    public ResponseEntity signUpByProvider(@PathVariable("provider") String provider,
//
//                                         @ModelAttribute UserSignUpByProviderRequestDto requestDto) {
//
//          signService.signUpByProvider(requestDto,provider);
//
//          return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CREATED_USER,true),HttpStatus.CREATED);
//    }





    @PostMapping(value = "/users/auth/refresh")
    public ResponseEntity refreshToken(HttpServletRequest request,HttpServletResponse response) {

        String refreshToken=null;
        Cookie[] cookies=request.getCookies();
//        Cookie refreshTokenCookie=null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("RefreshToken")&&!cookie.getValue().isEmpty()) {
//                    refreshTokenCookie=cookie;
                    refreshToken = cookie.getValue();
                }
            }
        }
        if(refreshToken==null){
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.REFRESH_TOKEN_FAIL,false),HttpStatus.UNAUTHORIZED);
        }


        try{
            UserTokenDto tokenDto = signService.refreshToken(refreshToken);
            UserTokenResponseDto userTokenResponseDto=new UserTokenResponseDto("JWT",tokenDto.getAccessToken(),tokenDto.getRefreshToken(),
                    jwtTokenProvider.getAccessTokenExpiration(),
                    jwtTokenProvider.getRefreshTokenExpiration());
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.REFRESH_TOKEN_SUCCESS,true,userTokenResponseDto),HttpStatus.OK);
    }
        catch (ExpiredJwtException e){
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.EXPIRED_REFRESH_TOKEN,false),HttpStatus.UNAUTHORIZED);
        }

        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.REFRESH_TOKEN_FAIL,false),HttpStatus.UNAUTHORIZED);
        }
    }


    @DeleteMapping (value = "/users")
    public ResponseEntity userDelete(
//            @RequestHeader(value="Authorization") String token
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {

        signService.userDelete(customUserDetails.getId());
        chatService.deleteChatRoomByUserId(customUserDetails.getId());
        s3Service.deleteImage(customUserDetails.getId());


        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.DELETE_USER_SUCCESS,true),HttpStatus.OK);
    }


}