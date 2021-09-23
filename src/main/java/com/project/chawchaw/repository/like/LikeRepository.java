package com.project.chawchaw.repository.like;

import com.project.chawchaw.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {
    @Query(value = "select l from Like l where l.fromUser.id =:fromUserId and l.toUser.id=:toUserId ")
    Optional<Like> findByLike(@Param("fromUserId")Long fromUserId, @Param("toUserId")Long toUserId);

    @Query(value = "select l from Like l join fetch l.fromUser fu join fetch l.toUser tu " +
            "where (tu.id =:userId1 and fu.id=:userId2) or (tu.id =:userId2 and fu.id=:userId1)")
    List<Like> findByLikeWithUserId(@Param("userId1")Long userId1, @Param("userId2")Long userId2);


//    @Query("select count(l) from Like l where l.toUser.id=:toUserId")
//    int countFollow(@Param("toUserId")Long toUserId);

    @Modifying
    @Query("delete from Like l where l.toUser.id=:userId or l.fromUser.id=:userId")
    int deleteLikeByUserId(@Param("userId")Long userId);
}
