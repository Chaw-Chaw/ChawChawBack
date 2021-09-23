package com.project.chawchaw.dto.user;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginResponseDto {

    private UserProfileDto profile;
    private UserTokenResponseDto token;
    private List<Long> blockIds;
}
