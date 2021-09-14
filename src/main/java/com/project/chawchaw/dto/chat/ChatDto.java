package com.project.chawchaw.dto.chat;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class ChatDto {
    private Long roomId;
    private List<String> participantNames=new ArrayList<>();
    private List<Long> participantIds=new ArrayList<>();
    private List<String> participantImageUrls=new ArrayList<>();
    private List<ChatMessageDto> messages=new ArrayList<>();
}
