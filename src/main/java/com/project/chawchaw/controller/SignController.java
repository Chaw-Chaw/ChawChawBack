package com.project.chawchaw.controller;

import com.project.chawchaw.config.JwtTokenProvider;
import com.project.chawchaw.config.response.DefaultResponseVo;
import com.project.chawchaw.config.response.ResponseMessage;
import com.project.chawchaw.dto.social.FaceBookProfile;
import com.project.chawchaw.dto.social.KakaoProfile;
import com.project.chawchaw.dto.social.SocialLoginRequestDto;
import com.project.chawchaw.dto.social.SocialLoginResponseDto;
import com.project.chawchaw.dto.user.UserLoginRequestDto;
import com.project.chawchaw.dto.user.UserLoginResponseDto;
import com.project.chawchaw.dto.user.UserSignUpByProviderRequestDto;
import com.project.chawchaw.dto.user.UserSignUpRequestDto;
import com.project.chawchaw.exception.LoginFailureException;
import com.project.chawchaw.response.CommonResult;
import com.project.chawchaw.response.SingleResult;
import com.project.chawchaw.service.*;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@RequiredArgsConstructor
@RestController
@Api(tags = {"1.sign"})
public class SignController {


    private final SignService signService;
    private final ResponseService responseService;
    private final KakaoService kakaoService;
    private final FaceBookService faceBookService;
    private final UserService userService;






    @ApiOperation(value = "메일 인증번호 전송", notes = "가입 할 이메일에 인증코드 전송")
    @PostMapping("/mail/send") // 이메일 인증 코드 보내기
    public ResponseEntity emailAuth(@RequestParam String email, HttpServletRequest request) throws Exception {
        HttpSession session=request.getSession();
        session.setAttribute(email,signService.sendSimpleMessage(email));
        session.setMaxInactiveInterval(60);

        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SEND_MAIL,true),HttpStatus.OK);
    }


    //인증 번호를 db에 저장할지 따로 쿠키에 저장할지
    @ApiOperation(value = "인증번호 판별", notes = "입력 받은 인증번호를 판별한다.")

    @PostMapping("/mail/verification") // 이메일 인증 코드 검증
    public ResponseEntity verifyCode(@RequestParam String verificationNumber,@RequestParam String email,HttpServletRequest request) {
       HttpSession session=request.getSession();
        Object attribute = session.getAttribute(email);
        if (attribute==null) {

            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.VERIFICATION_MAIL_FAIL,false),HttpStatus.OK);
        }
        else {
          if(attribute.toString().equals(verificationNumber)){
              session.invalidate();
              return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.VERIFICATION_MAIL_SUCCESS,true),HttpStatus.OK);

          }
          else{
              return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.VERIFICATION_MAIL_FAIL,false),HttpStatus.OK);
          }
        }
    }


    @ApiOperation(value = "회원가입",notes = "회원가입")
    @PostMapping(value = "/users/signup")
    public ResponseEntity signup(@ModelAttribute @Valid UserSignUpRequestDto requestDto){
        signService.signup(requestDto);
       return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CREATED_USER,true),HttpStatus.CREATED);

    }


    @ApiOperation(value = "중복회원조회",notes = "중복회원조회")
    @GetMapping(value = "/users/email/duplicate/{email}")
    public ResponseEntity emialDuplicate(@PathVariable("email") String email){
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
            @ModelAttribute UserLoginRequestDto requestDto,HttpServletResponse response) {

        try {

            if (requestDto.getProvider() != null) {

                if (requestDto.getProvider().equals("kakao") && requestDto.getCode() != null) {
                    String token = kakaoService.getKakaoTokenInfo(requestDto.getCode()).getAccess_token();
                    KakaoProfile kakaoProfile = kakaoService.getKakaoProfile(token);
                    String email = kakaoProfile.getEmail();
                    if (signService.validUserWithProvider(email, requestDto.getProvider())) {
                        UserLoginResponseDto loginDto = signService.loginByProvider(email, requestDto.getProvider());

                        response.addHeader("Authorization", "Bearer " + loginDto.getToken());
                        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_SUCCESS, true,userService.userProfile(loginDto.getId())), HttpStatus.OK);
                    } else {
                        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SOCIAL_LOGIN_FAIL, false,
                                new SocialLoginResponseDto(kakaoProfile.getEmail(), kakaoProfile.getName(), kakaoProfile.getImageUrl(), kakaoProfile.getProvider())), HttpStatus.OK);
                    }
                } else if (requestDto.getProvider().equals("facebook") && requestDto.getEmail() != null && requestDto.getAccessToken() != null) {
                    FaceBookProfile faceBookProfile = faceBookService.getFaceBookProfile(requestDto.getAccessToken(), requestDto.getEmail());
                    String email = faceBookProfile.getEmail();
                    if (signService.validUserWithProvider(email, requestDto.getProvider())) {
                        UserLoginResponseDto loginDto = signService.loginByProvider(email, requestDto.getProvider());
                        response.addHeader("Authorization", "Bearer " + loginDto.getToken());
                        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_SUCCESS, true,userService.userProfile(loginDto.getId())), HttpStatus.OK);
                    } else {
                        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SOCIAL_LOGIN_FAIL, false,
                                new SocialLoginResponseDto(faceBookProfile.getEmail(), faceBookProfile.getName(), faceBookProfile.getImageUrl(), faceBookProfile.getProvider())), HttpStatus.OK);
                    }
                } else {

                    return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.SOCIAL_LOGIN_CONNECT_FAIL, false), HttpStatus.OK);

                }

            }
            else{
              if(requestDto.getEmail()!=null&&requestDto.getPassword()!=null){

                  UserLoginResponseDto loginDto = signService.login(requestDto);
                  response.addHeader("Authorization","Bearer "+loginDto.getToken());
                    return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_SUCCESS,true,userService.userProfile(loginDto.getId())),HttpStatus.OK);

                }
              else{
                  return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_FAIL, false), HttpStatus.OK);
              }
            }
        }
        catch(LoginFailureException e){
            return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_FAIL, false), HttpStatus.OK);
        }
        catch (Exception e){
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


    @ApiOperation(value = "로그아웃", notes = "로그아웃을 한다")
    @PostMapping(value = "/users/logout")
    public CommonResult logout(@RequestHeader(value="X-AUTH-TOKEN") String token) {

        signService.logoutMember(token);
        return responseService.getSuccessResult();
    }
    @ApiOperation(value = "회원탈퇴", notes = "회원탈퇴를 한다")
    @DeleteMapping (value = "/users")
    public ResponseEntity userDelete(@RequestHeader(value="Authorization") String token) {

        signService.userDelete(token);
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.DELETE_USER,true),HttpStatus.OK);
    }


}