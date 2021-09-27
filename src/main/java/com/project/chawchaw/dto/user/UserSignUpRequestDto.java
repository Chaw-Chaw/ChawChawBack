package com.project.chawchaw.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class UserSignUpRequestDto {

    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$")
    private String email;
    @Pattern(regexp = "^.*(?=^.{8,15}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$")
    private String password;
    @Length(max = 20)
    private String name;
    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@([-_.]?[0-9a-zA-Z])+\\.ac\\.kr$")
    private String web_email;
    @NotBlank
    @Length(max = 20)
    private String school;
    private String imageUrl;

    @Length(max = 8)
    private String provider;
//    private String content;
//    private List<String> country=new ArrayList<>();
//    private List<String> language=new ArrayList<>();
//    private List<String> hopeLanguage=new ArrayList<>();
//    private String facebookUrl;
//    private String instagramUrl;
//    private String repCountry;
//    private String repLanguage;
//    private String repHopeLanguage;

}
