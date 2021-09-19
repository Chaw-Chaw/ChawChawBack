package com.project.chawchaw.repository.follow;

import com.project.chawchaw.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    @Query(value = "select f from Follow f where f.fromUser.id =:fromUserId and f.toUser.id=:toUserId ")
    Optional<Follow>findByFollow(@Param("fromUserId")Long fromUserId,@Param("toUserId")Long toUserId);

    @Query(value = "select f from Follow f join fetch f.fromUser fu join fetch f.toUser tu " +
            "where (tu.id =:userId1 and fu.id=:userId2) or (tu.id =:userId2 and fu.id=:userId1)")
    List<Follow> findByFollowWithUserId(@Param("userId1")Long userId1, @Param("userId2")Long userId2);


    @Query("select count(f) from Follow f where f.toUser.id=:toUserId")
    int countFollow(@Param("toUserId")Long toUserId);

    @Modifying
    @Query("delete from Follow f where f.toUser.id=:userId or f.fromUser.id=:userId")
    int deleteFollowByUserId(@Param("userId")Long userId);
}
