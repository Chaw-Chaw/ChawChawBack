package com.project.chawchaw.dto.admin;



import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class AdminUserSearch {
    private String name;
    private String hopeLanguage;
    private String language;
    private String order;
    private String sort;
    private String country;
    private String school;
    private Integer pageNo;
}
