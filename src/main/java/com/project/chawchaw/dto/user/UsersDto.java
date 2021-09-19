package com.project.chawchaw.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UsersDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String content;
    private LocalDateTime createDate;
    private Long view;
    private int follows;
    private String repCountry;
    private String repLanguage;
    private String repHopeLanguage;

    public UsersDto(Long id,String name, String imageUrl){
        this.id=id;
        this.imageUrl=imageUrl;
        this.name=name;

    }

}
