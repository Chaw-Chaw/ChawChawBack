package com.project.chawchaw.config;



import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException,
            ServletException {

        Object exception = request.getAttribute("exception");
        System.out.println(exception.toString());
        if(exception.equals("expiredException")){
            response.sendRedirect("/exception/entrypoint/expired");
        }
        else
        response.sendRedirect("/exception/entrypoint");
    }

}