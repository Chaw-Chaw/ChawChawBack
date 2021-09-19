package com.project.chawchaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlock is a Querydsl query type for Block
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBlock extends EntityPathBase<Block> {

    private static final long serialVersionUID = -55293032L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlock block = new QBlock("block");

    public final QUser fromUser;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final QUser toUser;

    public QBlock(String variable) {
        this(Block.class, forVariable(variable), INITS);
    }

    public QBlock(Path<? extends Block> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlock(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlock(PathMetadata metadata, PathInits inits) {
        this(Block.class, metadata, inits);
    }

    public QBlock(Class<? extends Block> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fromUser = inits.isInitialized("fromUser") ? new QUser(forProperty("fromUser")) : null;
        this.toUser = inits.isInitialized("toUser") ? new QUser(forProperty("toUser")) : null;
    }

}

