package com.project.chawchaw.dto.user;

import com.project.chawchaw.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class UserDto {


    private String name;
    private String imageUrl;
    private String content;
    private List<String> country;
    private List<String> language;
    private List<String> hopeLanguage;
    private String facebookUrl;
    private String instagramUrl;
    private String repCountry;
    private String repLanguage;
    private String repHopeLanguage;
    private Long views;
    private Long likes;
    private Boolean isLike;
    private LocalDateTime days;

 public UserDto(User user){
     this.name=user.getName();
     this.imageUrl=user.getImageUrl();
     this.content=user.getContent();
     this.facebookUrl=user.getFacebookUrl();
     this.instagramUrl=user.getInstagramUrl();
     this.views=user.getViews();
     this.likes=Long.valueOf(user.getToLikes().size());
     List<String> country=new ArrayList<>();
     List<String> language=new ArrayList<>();
     List<String> hopeLanguage=new ArrayList<>();

     user.getHopeLanguage().stream().forEach(hl->{
         if(!hl.getRep()){
//
             hopeLanguage.add(hl.getHopeLanguage().getAbbr());}

     });
     user.getLanguage().stream().forEach(l->{
         if(!l.getRep())language.add(l.getLanguage().getAbbr());

     });
     user.getCountry().stream().forEach(c->{
         if(!c.getRep())country.add(c.getCountry().getName());

     });
     this.repCountry=user.getRepCountry();
     this.repLanguage=user.getRepLanguage();
     this.repHopeLanguage=user.getRepHopeLanguage();
     this.country=country;
     this.hopeLanguage=hopeLanguage;
     this.language=language;

     this.days=user.getRegDate();

 }

}
