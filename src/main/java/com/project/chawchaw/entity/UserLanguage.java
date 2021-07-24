package com.project.chawchaw.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="language_id")
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    public static UserLanguage createUserLanguage(Language language) {
        UserLanguage userLanguage=new UserLanguage();
        userLanguage.language=language;
        return userLanguage;
    }

    public void addUser(User user) {
        this.user=user;
        user.getLanguage().add(this);
    }
}
