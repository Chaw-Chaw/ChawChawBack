package com.project.chawchaw.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;

    private String provider;

    private String refreshToken;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String web_email;
    @Column(nullable = false)
    private String school;
    @Column(nullable = false)
    private String imageUrl;


    private String content;

    private String facebookUrl;

    private String instagramUrl;

    private LocalDateTime lastLogOut;


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<UserCountry> country = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<UserLanguage> language=new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<UserHopeLanguage> hopeLanguage=new ArrayList<>();

    /**
     * 회원탈퇴 기능 추가시
     * CASECADE
     * LIKE,BLOCK **/
    @OneToMany(mappedBy = "toUser")
    private List<Like> toLikes =new ArrayList<>();

    @OneToMany(mappedBy = "fromUser")
    private List<Block> blockList=new ArrayList<>();

    private String repCountry;

    private String repLanguage;

    private String repHopeLanguage;


//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
//    private List<Follow>follows=new ArrayList<>();

    private LocalDateTime regDate;

    private Long views=0L;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    public static User createUser(String email,String name,String provider,String password,
                                  String web_email,String school,String imageUrl
//                                  String content, List<UserCountry>country,List<UserLanguage> language,List<UserHopeLanguage> hopeLanguage,
//                                  String facebookUrl,String instagramUrl,UserCountry repCountry,UserLanguage repLanguage,UserHopeLanguage repHopeLanguage
    ){


        User user=new User();
        user.imageUrl=imageUrl;
        user.name=name;
        user.email=email;
        user.password=password;
        user.role=ROLE.GUEST;
        if(email.equals("admin@chawchaw.kr"))user.role=ROLE.ADMIN;
        user.provider=provider;
        user.web_email=web_email;
        user.school=school;
        user.regDate=LocalDateTime.now().withNano(0);

//        user.content=content;
//        for(int i=0;i<country.size();i++){
//            country.get(i).addUser(user);
//        }
//        for(int i=0;i<language.size();i++){
//            language.get(i).addUser(user);
//        }
//        for(int i=0;i<hopeLanguage.size();i++){
//            hopeLanguage.get(i).addUser(user);
//        }
//        user.facebookUrl=facebookUrl;
//        user.instagramUrl =instagramUrl;
//        repCountry.addUser(user);
//        repHopeLanguage.addUser(user);
//        repLanguage.addUser(user);
//        user.repCountry=repCountry.getCountry().getName();
//        user.repLanguage=repLanguage.getLanguage().getAbbr();
//        user.repHopeLanguage=repHopeLanguage.getHopeLanguage().getAbbr();

        return user;

    }

//유효성 체크

    public void changeRefreshToken(String refreshToken) {

        this.refreshToken=refreshToken;
    }
    public void addView(){
        this.views+=1;
    }

    public void changeContent(String content){
        if(content!=null)
        this.content=content;

    }
    public void changeFaceBookUrl(String url){
        if(url!=null)
        this.facebookUrl=url;
    }
    public void changeInstagramUrl(String url){
        if(url!=null)
        this.instagramUrl=url;
    }
    public void changeImageUrl(String url){
        if(url!=null)
        this.imageUrl=url;
    }
    public void changeRep(String repCountry,String repLanguage,String repHopeLanguage){
        if(repCountry!=null&&repLanguage!=null&&repHopeLanguage!=null)
        this.repLanguage=repLanguage;
        this.repCountry=repCountry;
        this.repHopeLanguage=repHopeLanguage;
    }

    public void changeRole() {
        this.role=ROLE.USER;
    }
    public void changeLastLogOut(){
         this.lastLogOut=LocalDateTime.now().withNano(0);


    }
}
