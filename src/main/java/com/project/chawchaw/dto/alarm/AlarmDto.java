package com.project.chawchaw.dto.alarm;

import com.project.chawchaw.dto.chat.ChatMessageDto;
import com.project.chawchaw.dto.like.LikeAlarmDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDto {

    List<ChatMessageDto>messages;
    List<LikeAlarmDto>likes;
}
