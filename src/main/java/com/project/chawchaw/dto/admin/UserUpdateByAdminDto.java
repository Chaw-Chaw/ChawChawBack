package com.project.chawchaw.dto.admin;


import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserUpdateByAdminDto {
    private Long userId;

    @NotNull
    private List<String> country;
    @NotNull
    private List<String> language;
    @NotNull
    private List<String> hopeLanguage;
    @NotNull
    @Length(max = 2000)
    private String content;
    @NotNull
    @Length(max = 255)
    private String facebookUrl;
    @NotNull
    @Length(max = 255)
    private String instagramUrl;

    private String imageUrl;
    @NotBlank
    @Length(max = 255)
    private String repCountry;
    @NotBlank
    @Length(max = 2)
    private String repLanguage;
    @NotBlank
    @Length(max = 2)
    private String repHopeLanguage;

}
