package com.project.chawchaw.dto.user;

import com.project.chawchaw.dto.block.BlockUserDto;
import com.project.chawchaw.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserByAdminDto {
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
    private String days;
    private List<BlockUserDto>blockUsers;

    public UserByAdminDto(User user){
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

        this.days=user.getRegDate().toString();

    }
}
