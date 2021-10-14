package com.project.chawchaw.dto.block;

import com.project.chawchaw.entity.User;
import lombok.Getter;
import lombok.Setter;


/**
 * 수정이 필요 **/
@Getter
@Setter
public class BlockUserDto {
    private Long userId;
    private String imageUrl;
    private String name;

    public BlockUserDto(User user){
        this.userId=user.getId();
        this.name=user.getName();
        this.imageUrl=user.getImageUrl();
    }
}
