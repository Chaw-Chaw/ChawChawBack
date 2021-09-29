package com.project.chawchaw.dto.user;

import com.project.chawchaw.entity.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class UserProfileDto {

    private Long id;
    private String email;
    private String name;
    private String web_email;
    private String school;
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
    private String role;

    public UserProfileDto(User user){
        this.id=user.getId();
        this.email=user.getEmail();
        this.name=user.getName();
        this.web_email=user.getWeb_email();
        this.school=user.getSchool();
        this.imageUrl=user.getImageUrl();
        this.content=user.getContent();
        this.facebookUrl=user.getFacebookUrl();
        this.instagramUrl=user.getInstagramUrl();
        List<String> country=new ArrayList<>();
        List<String> language=new ArrayList<>();
        List<String> hopeLanguage=new ArrayList<>();
        user.getHopeLanguage().stream().forEach(hl->{
            if(!hl.getRep())hopeLanguage.add(hl.getHopeLanguage().getAbbr());

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
//        this.hopeLanguage=user.getHopeLanguage().stream().map(hl->hl.getHopeLanguage().getName()).collect(Collectors.toList());
//        this.country=user.getCountry().stream().map(c->c.getCountry().getName()).collect(Collectors.toList());
//        this.language=user.getLanguage().stream().map(l->l.getLanguage().getName()).collect(Collectors.toList());

        this.role=user.getRole().toString();
    }
}
