package com.project.chawchaw.dto.mail;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class MailRequestDto {

    private String verificationNumber;
    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@([-_.]?[0-9a-zA-Z])+\\.ac\\.kr$")
    private String email;
}
