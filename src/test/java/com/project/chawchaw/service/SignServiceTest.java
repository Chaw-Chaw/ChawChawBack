package com.project.chawchaw.service;

import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.dto.user.UserLoginRequestDto;
import com.project.chawchaw.dto.user.UserLoginResponseDto;
import com.project.chawchaw.dto.user.UserTokenDto;
import com.project.chawchaw.dto.user.UserSignUpRequestDto;
import com.project.chawchaw.entity.ROLE;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.UserAlreadyExistException;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.user.UserRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SignServiceTest {
    @Autowired
    SignService signService;
    @Autowired
    EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    FaceBookService faceBookService;
   @Test
   public void signUp()throws Exception{
      //given
       UserSignUpRequestDto userSignUpRequestDto=new UserSignUpRequestDto();
       userSignUpRequestDto.setEmail("fpdlwjzlr@naver.com");
       userSignUpRequestDto.setName("김진현");
       userSignUpRequestDto.setPassword("11");
       userSignUpRequestDto.setWeb_email("32141221@dankook.ac.kr");
       userSignUpRequestDto.setSchool("서울시립대학교");

      //when
      signService.signup(userSignUpRequestDto);
      em.flush();em.clear();
      //then
       User user = userRepository.findByEmail(userSignUpRequestDto.getEmail()).orElseThrow(UserNotFoundException::new);
       assertThat(user.getName()).isEqualTo("김진현");
       assertThat(user.getEmail()).isEqualTo("fpdlwjzlr@naver.com");
       assertThat(user.getRole()).isEqualTo(ROLE.GUEST);
       assertThat(user.getSchool()).isEqualTo("서울시립대학교");


   }
   /**일반 로그인 **/
   @Test
   public void login()throws Exception{
      //given
       UserSignUpRequestDto userSignUpRequestDto=new UserSignUpRequestDto();
       userSignUpRequestDto.setEmail("fpdlwjzlr@naver.com");
       userSignUpRequestDto.setName("김진현");
       userSignUpRequestDto.setPassword("11");
       userSignUpRequestDto.setWeb_email("32141221@dankook.ac.kr");
       userSignUpRequestDto.setSchool("서울시립대학교");
       signService.signup(userSignUpRequestDto);
       em.flush(); em.clear();

      //when

       UserLoginRequestDto userLoginRequestDto=new UserLoginRequestDto("fpdlwjzlr@naver.com","11",null,null,null,null);
       UserLoginResponseDto login = signService.login(userLoginRequestDto);
       User user = userRepository.findByEmail(userSignUpRequestDto.getEmail()).orElseThrow(UserNotFoundException::new);
       //then
       assertThat(jwtTokenProvider.validateToken(login.getToken().getAccessToken())).isTrue();
       assertThat(jwtTokenProvider.validateToken(login.getToken().getRefreshToken())).isTrue();
       assertThat(jwtTokenProvider.getAccessTokenExpiration()).isEqualTo(1000L*60*30);
       assertThat(jwtTokenProvider.getUserPk(login.getToken().getAccessToken())).isEqualTo(String.valueOf(user.getId()));


   }


   /**중복 회원 체크**/
   @Test
   public void checkUser()throws Exception{
      //given
       UserSignUpRequestDto userSignUpRequestDto=new UserSignUpRequestDto();
       userSignUpRequestDto.setEmail("fpdlwjzlr@naver.com");
       userSignUpRequestDto.setName("김진현");
       userSignUpRequestDto.setPassword("11");
       userSignUpRequestDto.setWeb_email("32141221@dankook.ac.kr");
       userSignUpRequestDto.setSchool("서울시립대학교");
       signService.signup(userSignUpRequestDto);
       em.flush();em.clear();
      
      //when
       UserSignUpRequestDto userSignUpRequestDto1=new UserSignUpRequestDto();
       userSignUpRequestDto1.setEmail("fpdlwjzlr@naver.com");
       userSignUpRequestDto1.setName("김진현");
       userSignUpRequestDto1.setPassword("11");
       userSignUpRequestDto1.setWeb_email("32141221@dankook.ac.kr");
       userSignUpRequestDto1.setSchool("서울시립대학교");

      
      //then
       Assertions.assertThatThrownBy(()->  signService.signup(userSignUpRequestDto1)).isInstanceOf(UserAlreadyExistException.class);

   }
   @Test
   public void checkUser2()throws Exception{
      //given
       UserSignUpRequestDto userSignUpRequestDto=new UserSignUpRequestDto();
       userSignUpRequestDto.setEmail("fpdlwjzlr@naver.com");
       userSignUpRequestDto.setName("김진현");
       userSignUpRequestDto.setPassword("11");
       userSignUpRequestDto.setWeb_email("32141221@dankook.ac.kr");
       userSignUpRequestDto.setSchool("서울시립대학교");
       signService.signup(userSignUpRequestDto);
       em.flush(); em.clear();

      //when
       boolean b = signService.userCheck("fpdlwjzlr@naver.com");
       //then
       assertThat(b).isTrue();
   }

   /**
    * 리프레시 토큰 
    **/
   @Test
   public void refreshToken1()throws Exception{
      //given

       UserSignUpRequestDto userSignUpRequestDto=new UserSignUpRequestDto();
       userSignUpRequestDto.setEmail("fpdlwjzlr@naver.com");
       userSignUpRequestDto.setName("김진현");
       userSignUpRequestDto.setPassword("11");
       userSignUpRequestDto.setWeb_email("32141221@dankook.ac.kr");
       userSignUpRequestDto.setSchool("서울시립대학교");
       signService.signup(userSignUpRequestDto);
       em.flush(); em.clear();
       UserLoginRequestDto userLoginRequestDto=new UserLoginRequestDto("fpdlwjzlr@naver.com","11",null,null,null,null);
       UserLoginResponseDto login = signService.login(userLoginRequestDto);

       //when

       UserTokenDto userTokenDto = signService.refreshToken(login.getToken().getRefreshToken());

       User user = userRepository.findByEmail("fpdlwjzlr@naver.com").orElseThrow(UserNotFoundException::new);

       //then
       assertThat(user.getRefreshToken()).isEqualTo(userTokenDto.getRefreshToken());
       assertThat(jwtTokenProvider.validateToken(userTokenDto.getAccessToken()));

   }
   /**
    * 잘못된 리프레시 토큰 **/
   
    @Test
    public void refreshToken2()throws Exception{
        //given
        UserSignUpRequestDto userSignUpRequestDto=new UserSignUpRequestDto();
        userSignUpRequestDto.setEmail("fpdlwjzlr@naver.com");
        userSignUpRequestDto.setName("김진현");
        userSignUpRequestDto.setPassword("11");
        userSignUpRequestDto.setWeb_email("32141221@dankook.ac.kr");
        userSignUpRequestDto.setSchool("서울시립대학교");
        signService.signup(userSignUpRequestDto);
        em.flush(); em.clear();
        UserLoginRequestDto userLoginRequestDto=new UserLoginRequestDto("fpdlwjzlr@naver.com","11",null,null,null,null);

        //when
        UserLoginResponseDto login = signService.login(userLoginRequestDto);

        //then
        Assertions.assertThatThrownBy(()->  signService.refreshToken(login.getToken().getRefreshToken()+"11") ).isInstanceOf(AccessDeniedException.class);

    }


    /**
     * 기한 지난 리프레시 토큰 사용**/
    @Test
    public void refreshToken3()throws Exception{
        //given

        jwtTokenProvider.setRefreshTokenValidMillisecond(-1L);
        UserSignUpRequestDto userSignUpRequestDto=new UserSignUpRequestDto();
        userSignUpRequestDto.setEmail("fpdlwjzlr@naver.com");
        userSignUpRequestDto.setName("김진현");
        userSignUpRequestDto.setPassword("11");
        userSignUpRequestDto.setWeb_email("32141221@dankook.ac.kr");
        userSignUpRequestDto.setSchool("서울시립대학교");
        signService.signup(userSignUpRequestDto);
        em.flush(); em.clear();
        UserLoginRequestDto userLoginRequestDto=new UserLoginRequestDto("fpdlwjzlr@naver.com","11",null,null,null,null);

        //when
        UserLoginResponseDto login = signService.login(userLoginRequestDto);
        //then

        Assertions.assertThatThrownBy(()->  signService.refreshToken(login.getToken().getRefreshToken()+"11") ).isInstanceOf(AccessDeniedException.class);

        jwtTokenProvider.setRefreshTokenValidMillisecond(1000L*30*60);
    }
//    @Test
//    public void logout()throws Exception{
//       //given
//
//       //when
//
//       //then
//    }



}