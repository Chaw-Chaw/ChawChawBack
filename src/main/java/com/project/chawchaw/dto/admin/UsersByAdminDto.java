package com.project.chawchaw.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersByAdminDto {
    private Long id;
    private String name;
    private String school;
    private String email;
    private String repCountry;
    private String repLanguage;
    private String repHopeLanguage;
    private Long likes;
    private Long views;
    private String days;

}
