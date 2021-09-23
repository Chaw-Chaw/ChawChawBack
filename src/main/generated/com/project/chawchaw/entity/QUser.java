package com.project.chawchaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 691525472L;

    public static final QUser user = new QUser("user");

    public final ListPath<Block, QBlock> blockList = this.<Block, QBlock>createList("blockList", Block.class, QBlock.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final ListPath<UserCountry, QUserCountry> country = this.<UserCountry, QUserCountry>createList("country", UserCountry.class, QUserCountry.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final StringPath facebookUrl = createString("facebookUrl");

    public final ListPath<UserHopeLanguage, QUserHopeLanguage> hopeLanguage = this.<UserHopeLanguage, QUserHopeLanguage>createList("hopeLanguage", UserHopeLanguage.class, QUserHopeLanguage.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final StringPath instagramUrl = createString("instagramUrl");

    public final ListPath<UserLanguage, QUserLanguage> language = this.<UserLanguage, QUserLanguage>createList("language", UserLanguage.class, QUserLanguage.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> lastLogOut = createDateTime("lastLogOut", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath provider = createString("provider");

    public final StringPath refreshToken = createString("refreshToken");

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final StringPath repCountry = createString("repCountry");

    public final StringPath repHopeLanguage = createString("repHopeLanguage");

    public final StringPath repLanguage = createString("repLanguage");

    public final EnumPath<ROLE> role = createEnum("role", ROLE.class);

    public final StringPath school = createString("school");

    public final ListPath<Like, QLike> toLikes = this.<Like, QLike>createList("toLikes", Like.class, QLike.class, PathInits.DIRECT2);

    public final NumberPath<Long> views = createNumber("views", Long.class);

    public final StringPath web_email = createString("web_email");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

