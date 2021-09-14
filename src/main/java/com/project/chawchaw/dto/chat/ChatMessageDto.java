package com.project.chawchaw.dto.chat;


import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageDto implements Serializable {


    @NotNull
    private MessageType messageType; // 메시지 타입
    @NotNull
    private Long roomId; // 방번호
    @NotNull
    private Long senderId;
    @NotBlank
    @Length(max = 20)
    private String sender; // 메시지 보낸사람
    @NotBlank
    @Length(max = 2000)
    private String message; // 메시지

    private String imageUrl;
    private LocalDateTime regDate;

    private Boolean isRead;
}
