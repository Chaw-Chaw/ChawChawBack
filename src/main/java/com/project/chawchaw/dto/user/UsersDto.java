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
    private String imageUrl;
    private String content;
    private String country;
    private String language;
    private String hopeLanguage;
    private LocalDateTime createDate;
    private Long view;
    private int follows;


}
