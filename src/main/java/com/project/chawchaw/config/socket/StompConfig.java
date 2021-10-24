package com.project.chawchaw.config.socket;

import com.project.chawchaw.config.jwt.JwtTokenProvider;
import com.project.chawchaw.entity.ROLE;
import com.project.chawchaw.entity.User;
import com.project.chawchaw.exception.UserNotFoundException;
import com.project.chawchaw.repository.chat.ChatMessageRepository;
import com.project.chawchaw.repository.user.UserRepository;
import com.project.chawchaw.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;

import static org.springframework.util.StringUtils.*;

@Component
@RequiredArgsConstructor

public class StompConfig implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (hasText(token) && token.startsWith("Bearer")) {
                token=token.replace("Bearer ","");
            }
            else{
                return null;
            }

            Long userId = Long.valueOf(jwtTokenProvider.getUserPk(token));

            User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

            if(!user.getRole().equals(ROLE.USER)) return null;

            accessor.setUser(jwtTokenProvider.getAuthentication(token));

            chatMessageRepository.createRoomSession(user.getEmail());

        }
        else if(StompCommand.DISCONNECT == accessor.getCommand()){
            if (accessor.getUser()!=null){
                chatMessageRepository.deleteRoomSession(accessor.getUser().getName());
            }
        }
        return message;
    }




}