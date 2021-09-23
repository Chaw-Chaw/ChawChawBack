package com.project.chawchaw.service;



import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.dto.user.*;
import com.project.chawchaw.entity.*;
import com.project.chawchaw.exception.*;
import com.project.chawchaw.repository.chat.ChatMessageRepository;
import com.project.chawchaw.repository.like.LikeAlarmRepository;
import com.project.chawchaw.repository.like.LikeRepository;
import com.project.chawchaw.repository.ViewRepository;
import com.project.chawchaw.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class SignService {


    private final JavaMailSender emailSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate redisTemplate;
    private final LikeRepository likeRepository;
    private final ViewRepository viewRepository;
    private final LikeAlarmRepository likeAlarmRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Value("${file.defaultImage}")
    private String defaultImage;



    private MimeMessage createMessage(String to,String code)throws Exception{
        logger.info("보내는 대상 : "+ to);
//        logger.info("인증 번호 : " + ePw);
        MimeMessage  message = emailSender.createMimeMessage();


        message.addRecipients(Message.RecipientType.TO, to); //보내는 대상
        message.setSubject("chawchaw 확인 코드: " + code); //제목

        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 가입 창이 있는 브라우저 창에 입력하세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += code;
        msg += "</td></tr></tbody></table></div>";
        msg += "<a href=\"\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">chawchaw</a>";

        message.setText(msg, "utf-8", "html"); //내용
        message.setFrom(new InternetAddress("fpdlwjzlr@gmail.com","chawchaw")); //보내는 사람

        return message;
    }

    // 인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    public String sendSimpleMessage(String to)throws Exception {
        String code=createKey();
        MimeMessage message = createMessage(to,code);


        try{
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return code;
    }


    @Transactional
    public void signup(UserSignUpRequestDto requestDto){



//

        if(validUserWithProvider(requestDto.getEmail(),requestDto.getProvider())){

//            throw new DataConversionException("s");
            throw new UserAlreadyExistException();

        }

        String imageUrl=defaultImage;
        if(requestDto.getImageUrl()!=null){
            imageUrl=requestDto.getImageUrl();

        }

        //basic 추가?
        userRepository.save(User.createUser(requestDto.getEmail(),requestDto.getName(),requestDto.getProvider(),passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getWeb_email(),requestDto.getSchool(),imageUrl
//                ,requestDto.getContent(), countryList, languageList,hopeLanguageList,requestDto.getFacebookUrl(),requestDto.getInstagramUrl(),userRepCountry,userRepLanguage,userRepHopeLanguage
        ));
    }




    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto requestDto){

        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(LoginFailureException::new);
        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())){
            throw new LoginFailureException();
        }

        return getUserLoginResponseDto(user);


    }

    private UserLoginResponseDto getUserLoginResponseDto(User user) {
        user.changeRefreshToken(jwtTokenProvider.createRefreshToken(String.valueOf(user.getId())));
        UserTokenResponseDto userTokenResponseDto = new UserTokenResponseDto("JWT", jwtTokenProvider.createToken(String.valueOf(user.getId())), user.getRefreshToken(),
                jwtTokenProvider.getAccessTokenExpiration(),
                jwtTokenProvider.getRefreshTokenExpiration());

        List<Long> blockIds = user.getBlockList().stream().map(b -> b.getToUser().getId()).collect(Collectors.toList());
        return new UserLoginResponseDto(new UserProfileDto(user), userTokenResponseDto, blockIds);
    }


    @Transactional
    public UserLoginResponseDto loginByProvider(String email, String provider) {


        User user = userRepository.findUserByEmailAndProvider(email, provider).orElseThrow(UserNotFoundException::new);

//
        return getUserLoginResponseDto(user);
//

    }

//


    public Boolean validUserWithProvider(String email, String provider) {
        if(userRepository.findUserByEmailAndProvider(email, provider).isPresent())

        {
            return true;
        }
        return false;
    }

//    private void validUser(String email) {
//        if(userRepository.findByEmail(email).isPresent())
//            throw new UserAlreadyExistException();
//    }

    public boolean userCheck(String email){
        if(userRepository.findByEmail(email).isPresent())
           return true;
        else return false;
    }
//    public String getImageUrl(String token){
//        Long id=Long.valueOf(jwtTokenProvider.getUserPk(token));
//        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
//        return user.getImageUrl();
//    }



    @Transactional
    public void logout(String token) {
//        redisTemplate.opsForValue().set( "token:" + token, "v", jwtTokenProvider.getRemainingSeconds(token));
        User user = userRepository.findById(Long.valueOf(jwtTokenProvider.getUserPk(token))).orElseThrow(UserNotFoundException::new);
        user.changeRefreshToken("invalidate");

}
    @Transactional
    public UserTokenDto refreshToken(String refreshToken)throws Exception{

        jwtTokenProvider.validateToken(refreshToken);

        User user = userRepository.findById(Long.valueOf(jwtTokenProvider.getUserPkByRefreshToken(refreshToken))).orElseThrow(UserNotFoundException::new);
        if(!refreshToken.equals(user.getRefreshToken())){
            throw new AccessDeniedException("");
        }

//        user.changeRefreshToken(jwtTokenProvider.createRefreshToken(String.valueOf(user.getId())));
        return new UserTokenDto(user.getId(),jwtTokenProvider.createToken(String.valueOf(user.getId())),user.getRefreshToken());
    }





    //스프링 시큐리티 세션 처리 해야될듯
    @Transactional
    public void userDelete(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        likeRepository.deleteLikeByUserId(user.getId());
        viewRepository.deleteView(user.getId());
        userRepository.delete(user);

    }


}
