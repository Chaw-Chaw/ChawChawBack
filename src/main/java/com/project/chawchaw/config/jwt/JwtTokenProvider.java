package com.project.chawchaw.config.jwt;

import io.jsonwebtoken.*;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

// import 생략

@RequiredArgsConstructor
@Component
public class JwtTokenProvider { // JWT 토큰을 생성 및 검증 모듈


    @Value("spring.jwt.secret")
    private String secretKey;

    private final RedisTemplate redisTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private long tokenValidMilisecond=1000L * 60 * 30; // 30분만 토큰 유효

    private long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24 * 30; // 30일

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Jwt 토큰 생성
    public String createToken(String userPk) {
        Claims claims = Jwts.claims().setSubject(userPk);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

//    public String createToken(String userPk) {
//        Claims claims = Jwts.claims().setSubject(userPk);
//        Date now = new Date();
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }

    // jwt refresh 토큰 생성
    public String createRefreshToken(String userPk) {
        String uuid=UUID.randomUUID().toString();
        Claims claims = Jwts.claims().setSubject(userPk+"_"+uuid);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Jwt 토큰으로 인증 정보를 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Jwt 토큰에서 회원 구별 정보 추출
    public String getUserPk(String token) {
        //
        token=token.replace("Bearer ","");

        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        } catch(ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public String getUserPkByRefreshToken(String token) {
        //
        try {
            String subject = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
            return subject.split("_")[0];
        } catch(ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }



    public Duration getRemainingSeconds(String jwtToken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
        long seconds = (claims.getBody().getExpiration().getTime() - claims.getBody().getIssuedAt().getTime()) / 1000;
        return Duration.ofSeconds(seconds < 0 ? 0 : seconds);
    }


    // Request의 Header에서 token 파싱 : "X-AUTH-TOKEN: jwt토큰"
    public String resolveToken(HttpServletRequest req) {
        return req.getHeader("Authorization");
    }
    // Jwt 토큰의 유효성 + 만료일자 확인
    public Boolean validateToken(String jwtToken) throws Exception{

//            if(isLoggedOut(jwtToken)) return false;
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());


    }

    public Long getAccessTokenExpiration(){
        return this.tokenValidMilisecond;
    }
    public Long getRefreshTokenExpiration(){
        return this.refreshTokenValidMillisecond;
    }

    public boolean validateTokenWithRequest(String jwtToken, ServletRequest request) {
        try {

//            if(isLoggedOut(jwtToken)) return false;
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());

        } catch(ExpiredJwtException e) {
            request.setAttribute("exception","expiredException");
            return false;
        } catch (Exception e) {
            request.setAttribute("exception","entrypointException");
            return false;
        }

    }



    public boolean validateTokenExceptExpiration(String jwtToken, ServletRequest request) {
        try {

//            if(isLoggedOut(jwtToken)) return false;
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return claims.getBody().getExpiration().before(new Date());
        } catch(ExpiredJwtException e) {

            return true;
        } catch (Exception e) {

            return false;
        }

    }
    public boolean isLoggedOut(String jwtToken) {
        return redisTemplate.opsForValue().get(jwtToken) != null;
    }

    public void setTokenValidMillisecond(long tokenValidMillisecond) {
        this.tokenValidMilisecond = tokenValidMillisecond;
    }

    public void setRefreshTokenValidMillisecond(long refreshTokenValidMillisecond) {
        this.refreshTokenValidMillisecond = refreshTokenValidMillisecond;
    }
}
