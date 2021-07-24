package com.project.chawchaw.service;

import com.project.chawchaw.entity.CustomUserDetails;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService{



        private final UserRepository userRepository;



        @Override
        public UserDetails loadUserByUsername(String userPk) {
        User user = userRepository.findById(Long.valueOf(userPk)).orElseThrow(UserNotFoundException::new);
        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                new SimpleGrantedAuthority(user.getRole().toString()));
    }


}

