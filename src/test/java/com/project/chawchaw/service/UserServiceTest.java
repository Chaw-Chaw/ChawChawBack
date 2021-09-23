package com.project.chawchaw.service;

import com.project.chawchaw.dto.user.UserDto;
import com.project.chawchaw.dto.user.UserSearch;
import com.project.chawchaw.dto.user.UserUpdateDto;
import com.project.chawchaw.dto.user.UsersDto;
import com.project.chawchaw.entity.*;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.CountryRepository;
import com.project.chawchaw.repository.LanguageRepository;
import com.project.chawchaw.repository.user.UserRepository;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional



class UserServiceTest {


    @Autowired
    EntityManager em;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    LikeService likeService;


    @BeforeEach
    public void beforeEach()throws Exception{
        Language language1=Language.createLanguage("한국어","ko");
        Country country1=Country.createCountry("한국");
        em.persist(language1);
        em.persist(country1);
        Language language2=Language.createLanguage("일본어","jp");
        Country country2=Country.createCountry("일본");
        em.persist(language2);
        em.persist(country2);
        Language language3=Language.createLanguage("영어","en");
        Country country3=Country.createCountry("미국");
        em.persist(country3);
        em.persist(language3);
        Language language4=Language.createLanguage("불어","fr");
        Country country4=Country.createCountry("프랑스");
        em.persist(country4);
        em.persist(language4);

        User user1 = User.createUser("11", "11", "11", "11", "11", "서울시립대학교", "11");
        User user2 = User.createUser("22", "22", "22", "22", "22", "서울시립대학교", "22");
        User user3 = User.createUser("33", "33", "33", "33", "33", "서울시립대학교", "33");
        User user4 = User.createUser("44", "44", "44", "44", "44", "서울시립대학교", "44");
        User user5 = User.createUser("55", "55", "55", "55", "55", "서울시립대학교", "55");
        User user6 = User.createUser("66", "66", "66", "66", "66", "서울시립대학교", "66");
        User user7 = User.createUser("77", "77", "77", "77", "77", "서울시립대학교", "77");
        User user8 = User.createUser("88", "88", "88", "88", "88", "서울시립대학교", "88");
        User user9 = User.createUser("99", "99", "99", "99", "99", "서울시립대학교", "99");
        User user10 = User.createUser("1010", "1010", "1010", "1010", "1010", "서울시립대학교", "1010");
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);
        em.persist(user5);
        em.persist(user6);
        em.persist(user7);
        em.persist(user8);
        em.persist(user9);
        em.persist(user10);
        //
//        List<UserCountry>user1c=new ArrayList<>();
//        List<UserLanguage>user1l=new ArrayList<>();
//        List<UserHopeLanguage>user1h=new ArrayList<>();
//
//        user1c.add(UserCountry.createUserCountry(country1));
//        user1c.add(UserCountry.createUserCountry(country2));
//        user1l.add(UserLanguage.createUserLanguage(language1));
//        user1l.add(UserLanguage.createUserLanguage(language2));
//
//        user1h.add(UserHopeLanguage.createUserHopeLanguage(language1));
//
//
//
//
//
//        em.persist(User.createUser("1",null,null,null,null,"단국대",null,"ggggggg",user1c,user1l,user1h,null,null));
//
//
//        List<UserCountry> user2c=new ArrayList<>();
//        List<UserLanguage>user2l=new ArrayList<>();
//        List<UserHopeLanguage>user2h=new ArrayList<>();
//        user2h.add(UserHopeLanguage.createUserHopeLanguage(language1));
//        user2c.add(UserCountry.createUserCountry(country3));
//        user2l.add(UserLanguage.createUserLanguage(language3));
//        user2c.add(UserCountry.createUserCountry(country4));
//        user2l.add(UserLanguage.createUserLanguage(language4));
//        em.persist(User.createUser("2",null,null,null,null,"단국대",null,"ggggggg",user2c,user2l,user2h,null,null));
//
//
//        List<UserCountry>user3c=new ArrayList<>();
//        List<UserLanguage>user3l=new ArrayList<>();
//        List<UserHopeLanguage>user3h=new ArrayList<>();
//        user3h.add(UserHopeLanguage.createUserHopeLanguage(language1));
//        user3c.add(UserCountry.createUserCountry(country1));
//        user3l.add(UserLanguage.createUserLanguage(language1));
//        user3c.add(UserCountry.createUserCountry(country3));
//        user3l.add(UserLanguage.createUserLanguage(language3));
//        em.persist(User.createUser("3",null,null,null,null,"단국대",null,"ggggggg",user3c,user3l,user3h,null,null));





    }
    //전체 회원조회
    @Test
    public void users()throws Exception{
        //given
        User user1 = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
        User user2 = userRepository.findByEmail("22").orElseThrow(UserNotFoundException::new);
        User user3 = userRepository.findByEmail("33").orElseThrow(UserNotFoundException::new);
        User user4 = userRepository.findByEmail("44").orElseThrow(UserNotFoundException::new);
        User user5 = userRepository.findByEmail("55").orElseThrow(UserNotFoundException::new);
        User user6 = userRepository.findByEmail("66").orElseThrow(UserNotFoundException::new);
        User user7 = userRepository.findByEmail("77").orElseThrow(UserNotFoundException::new);
        User user8 = userRepository.findByEmail("88").orElseThrow(UserNotFoundException::new);
        User user9 = userRepository.findByEmail("99").orElseThrow(UserNotFoundException::new);
        User user10 = userRepository.findByEmail("1010").orElseThrow(UserNotFoundException::new);
        List<String>user1c=new ArrayList<>();
        user1c.add("미국");
        user1c.add("프랑스");
        List<String>user1l=new ArrayList<>();
        user1l.add("jp");
        user1l.add("en");
        List<String>user1h=new ArrayList<>();
        user1h.add("fr");
        UserUpdateDto userUpdateDto=new UserUpdateDto(user1c,user1l,user1h,"",
                "facebook","insta","https://" + "d3t4l8y7wi01lo.cloudfront.net" + "/" + "defaultImage_233500392.png","한국","ko","en");

        userService.userProfileUpdate(userUpdateDto, user1.getId());
        userService.userProfileUpdate(userUpdateDto, user2.getId());
        userService.userProfileUpdate(userUpdateDto, user3.getId());
        userService.userProfileUpdate(userUpdateDto, user4.getId());
        userService.userProfileUpdate(userUpdateDto, user5.getId());
        userService.userProfileUpdate(userUpdateDto, user6.getId());
        userService.userProfileUpdate(userUpdateDto, user7.getId());
        userService.userProfileUpdate(userUpdateDto, user8.getId());
        userService.userProfileUpdate(userUpdateDto, user9.getId());
        userService.userProfileUpdate(userUpdateDto, user10.getId());
        em.flush(); em.clear();
        //when
        UserSearch userSearch=new UserSearch();
        userSearch.setSchool("서울시립대");
        userSearch.setLanguage("jp");
        userSearch.setIsFirst(true);
        userSearch.setOrder("view");
        userSearch.getExcludes().add(user2.getId());
        userSearch.getExcludes().add(user3.getId());

        //조회수
        userService.detailUser(user7.getId(),user1.getId());

        List<UsersDto> users = userService.users(userSearch, user1.getId());
        List<Long>usersId=new ArrayList<>();
        users.stream().forEach(u->usersId.add(u.getId()));
        //then
        assertThat(users.size()).isEqualTo(6);
        assertThat(usersId.contains(user2.getId())).isFalse();
        assertThat(usersId.contains(user3.getId())).isFalse();
        assertThat(users.get(0).getId()).isEqualTo(user7.getId());

    }

    @Test
    public void detail()throws Exception{
       //given

        User user1 = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
        User user2 = userRepository.findByEmail("22").orElseThrow(UserNotFoundException::new);
        List<String>user1c=new ArrayList<>();
        user1c.add("미국");
        user1c.add("프랑스");
        List<String>user1l=new ArrayList<>();
        user1l.add("jp");
        user1l.add("en");
        List<String>user1h=new ArrayList<>();
        user1h.add("fr");
        UserUpdateDto userUpdateDto=new UserUpdateDto(user1c,user1l,user1h,"user",
                "facebook","insta","https://" + "d3t4l8y7wi01lo.cloudfront.net" + "/" + "defaultImage_233500392.png","한국","ko","en");

        userService.userProfileUpdate(userUpdateDto, user1.getId());
        userService.userProfileUpdate(userUpdateDto, user2.getId());


        //when

        //팔로우 여부 확인
        likeService.like(user1.getId(), user2.getId());
        em.flush(); em.clear();

        //조회수 중복확인
        UserDto userDto1 = userService.detailUser(user1.getId(), user2.getId());
        UserDto userDto = userService.detailUser(user1.getId(), user2.getId());




        //then

        assertThat(userDto.getContent()).isEqualTo(user1.getContent());
        assertThat(userDto.getCountry().get(0)).isEqualTo(user1.getCountry().get(0).getCountry().getName());
        assertThat(userDto.getCountry().get(1)).isEqualTo(user1.getCountry().get(1).getCountry().getName());
        assertThat(userDto.getFacebookUrl()).isEqualTo(user1.getFacebookUrl());
        assertThat(userDto.getInstagramUrl()).isEqualTo(user1.getInstagramUrl());
        assertThat(userDto.getName()).isEqualTo(user1.getName());
        assertThat(userDto.getLikes()).isEqualTo(user1.getToLikes().size());
        assertThat(userDto.getViews()).isEqualTo(1);
        assertThat(userDto.getIsLike()).isTrue();
    }

    @Test
    public void userUpdate()throws Exception{
       //given
        User user1 = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);

        //when
        List<String>user1c=new ArrayList<>();
        user1c.add("미국");
        user1c.add("프랑스");
        List<String>user1l=new ArrayList<>();
        user1l.add("jp");
        user1l.add("en");
        List<String>user1h=new ArrayList<>();
        user1h.add("fr");
        UserUpdateDto userUpdateDto=new UserUpdateDto(user1c,user1l,user1h,"user1",
                "facebook","insta","https://" + "d3t4l8y7wi01lo.cloudfront.net" + "/" + "defaultImage_233500392.png","한국","ko","en");

        userService.userProfileUpdate(userUpdateDto,user1.getId());
        em.flush(); em.clear();
        user1 = userRepository.findByEmail("11").orElseThrow(UserNotFoundException::new);
       //then
        assertThat(user1.getContent()).isEqualTo("user1");
        assertThat(user1.getFacebookUrl()).isEqualTo("facebook");
        assertThat(user1.getInstagramUrl()).isEqualTo("insta");
        assertThat(user1.getRepCountry()).isEqualTo("한국");
        assertThat(user1.getRepLanguage()).isEqualTo("ko");
        assertThat(user1.getRepHopeLanguage()).isEqualTo("en");
        assertThat(user1.getImageUrl()).isEqualTo("https://" + "d3t4l8y7wi01lo.cloudfront.net" + "/" + "defaultImage_233500392.png");
        assertThat(user1.getCountry().size()).isEqualTo(3);
        assertThat(user1.getCountry().get(0).getCountry().getName()).isEqualTo("미국");
        assertThat(user1.getCountry().get(1).getCountry().getName()).isEqualTo("프랑스");
        assertThat(user1.getLanguage().get(0).getLanguage().getAbbr()).isEqualTo("jp");
        assertThat(user1.getLanguage().get(1).getLanguage().getAbbr()).isEqualTo("en");
        assertThat(user1.getHopeLanguage().get(0).getHopeLanguage().getAbbr()).isEqualTo("fr");



    }


    @Test
    public void jasyptTest()throws Exception{
        String huk = "";
        String en=null;


        String jae="1234";
        String du="aa236326";
        String nu="1234";



        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setPassword(System.getProperty("jasypt.encryptor.password"));
        jasypt.setAlgorithm("PBEWithMD5AndDES");

        String encryptedText1 = jasypt.encrypt(huk);
        System.out.println("======================jasypt");
//        System.out.println(encryptedText1);
//        System.out.println("dataurl");
        String decryptedText1 = jasypt.decrypt(encryptedText1);
        System.out.println(encryptedText1);
        assertThat(huk).isEqualTo(decryptedText1);

        String encryptedText2 = jasypt.encrypt(en);
        System.out.println("======================slack");
        System.out.println(encryptedText2);
        String decryptedText2 = jasypt.decrypt(encryptedText2);
        System.out.println(decryptedText2);
        assertThat(en).isEqualTo(decryptedText2);

//        String encryptedText3 = jasypt.encrypt(jae);
//        System.out.println("======================재민");
//        System.out.println(encryptedText3);
//        String decryptedText3= jasypt.decrypt(encryptedText3);
//        assertThat(jae).isEqualTo(decryptedText3);
//
//        String encryptedText4 = jasypt.encrypt(du);
//        System.out.println("======================두진");
//        System.out.println(encryptedText4);
//        String decryptedText4 = jasypt.decrypt(encryptedText4);
//        assertThat(du).isEqualTo(decryptedText4);
//
//        String encryptedText5= jasypt.encrypt(nu);
//        System.out.println("======================누가");
//        System.out.println(encryptedText5);
//        String decryptedText5 = jasypt.decrypt(encryptedText5);
//        assertThat(nu).isEqualTo(decryptedText5);








    }

}