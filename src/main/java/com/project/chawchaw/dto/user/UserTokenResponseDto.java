package com.project.chawchaw.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenResponseDto {
   private String tokenType;
   private String accessToken;
   private String refreshToken;
   private Long expiresIn;
   private Long refreshTokenExpiresIn;
}
