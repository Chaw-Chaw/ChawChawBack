package com.project.chawchaw.repository;

import com.project.chawchaw.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {



    @Modifying
    @Query("delete from Block b where b.toUser.id=:userId or b.fromUser.id=:userId")
    int deleteBlockByUserId(@Param("userId")Long userId);


    @Query("select b from Block b join fetch b.toUser tu join fetch" +
            " b.fromUser fu where fu.id=:fromUserId and tu.id=:toUserId")
    Optional<Block>isBlock(@Param("fromUserId")Long fromUserId,@Param("toUserId")Long toUserId);

    @Query("select b from Block b join fetch b.toUser tu join fetch" +
            " b.fromUser fu where (fu.id=:userId1 and tu.id=:userId2) or (fu.id=:userId2 and tu.id=:userId1)")
    Optional<Block>isBlockWithUserId(@Param("userId1")Long userId1,@Param("userId2")Long userId2);

    @Query("select b from Block b join fetch"+
            " b.fromUser fu where fu.id=:fromUserId and fu.id=:fromUserId")
    List<Block> findBlockByFromUserId(@Param("fromUserId")Long fromUserId);
}
