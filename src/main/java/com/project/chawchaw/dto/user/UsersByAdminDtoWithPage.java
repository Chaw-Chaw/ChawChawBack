package com.project.chawchaw.dto.user;

import com.project.chawchaw.dto.admin.UsersByAdminDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsersByAdminDtoWithPage {
    List<UsersByAdminDto>contents;
    private Integer totalCnt;
    private Integer startPage;
    private Integer endPage;
    private Integer curPage;
    private Boolean isNext;
    private Boolean isPrevious;

}
