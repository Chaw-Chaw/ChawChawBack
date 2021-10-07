package com.project.chawchaw.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String content;
    private String regDate;
    private Long view;
    private Long likes;
    private String repCountry;
    private String repLanguage;
    private String repHopeLanguage;

    public UsersDto(Long id,String name, String imageUrl){
        this.id=id;
        this.imageUrl=imageUrl;
        this.name=name;

    }

}
