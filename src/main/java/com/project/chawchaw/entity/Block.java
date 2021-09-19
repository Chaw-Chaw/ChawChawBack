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
public class Block {

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

    public static Block createBlock(User fromUser,User toUser){
        Block block=new Block();
        fromUser.getBlockList().add(block);
        block.fromUser=fromUser;
        block.toUser=toUser;

        block.regDate=LocalDateTime.now().withNano(0);
        return block;

    }



}
