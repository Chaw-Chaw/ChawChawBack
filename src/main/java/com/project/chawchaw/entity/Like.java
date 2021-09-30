package com.project.chawchaw.entity;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userFrom_id")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userTo_id")
    private User toUser;

    private LocalDateTime regDate;

    public static Like createLike(User fromUser, User toUser){
        Like like =new Like();
        like.fromUser=fromUser;
        like.toUser=toUser;
        like.regDate=LocalDateTime.now().withNano(0);
        toUser.getToLikes().add(like);
        return like;

    }



}
