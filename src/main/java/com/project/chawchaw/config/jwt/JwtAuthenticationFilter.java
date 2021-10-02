package com.project.chawchaw.config.jwt;

import com.project.chawchaw.config.logging.ReadableRequestWrapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// import 생략

public class JwtAuthenticationFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Request로 들어오는 Jwt Token의 유효성을 검증(jwtTokenProvider.validateToken)하는 filter를 filterChain에 등록합니다.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if(token == null || !token.startsWith("Bearer")) {
            request.setAttribute("exception","entrypointException");
            filterChain.doFilter(request, response);
            return;
        }
        token=token.replace("Bearer ","");



        if(token != null && jwtTokenProvider.validateTokenWithRequest(token,request)) {


//
//            ReadableRequestWrapper wrapper = new ReadableRequestWrapper((HttpServletRequest)request);
//            filterChain.doFilter(wrapper, response);
            System.out.println("===============================zzzzz");

            Authentication auth = jwtTokenProvider.getAuthentication(token);


            //강제로 세션접근

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
//
        filterChain.doFilter(request, response);
    }
}