package com.project.chawchaw.dto.social;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FaceBookProfile {
    private String id;
    private String name;
    private String email;
    private Picture picture;


    @Getter
    @Setter
    @ToString
     public class Picture{
        private Data data;

     }

    @Getter
    @Setter
    @ToString
    public class Data{
        private String url;

    }


}
