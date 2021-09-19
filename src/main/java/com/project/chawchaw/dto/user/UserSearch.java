package com.project.chawchaw.dto.user;



import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserSearch {
    private String name;
    private String hopeLanguage;
    private String language;
    private String order;
    @NotNull
    private Boolean isFirst;
//    private Long userId;
    private String school;
    Set<Long> excludes=new HashSet<>();
    private Long lastUserId;
}
