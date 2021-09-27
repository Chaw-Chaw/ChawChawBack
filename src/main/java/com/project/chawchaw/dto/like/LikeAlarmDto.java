package com.project.chawchaw.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeAlarmDto implements Serializable {
    private LikeType likeType;
    private String name;
    private LocalDateTime regDate;
}
