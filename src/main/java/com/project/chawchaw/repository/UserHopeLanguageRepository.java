package com.project.chawchaw.repository;

import com.project.chawchaw.dto.UserLanguageDto;
import com.project.chawchaw.entity.UserHopeLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserHopeLanguageRepository extends JpaRepository<UserHopeLanguage,Long> {

//    @Query("select hl.name from UserHopeLanguage uhl join fetch uhl.hopeLanguage hl where uhl.user.id=:userId")
//    List<String> findUserHopeLanguage(@Param("userId")Long userId);


    @Query("select uhl from UserHopeLanguage uhl where uhl.user.id=:userId and uhl.hopeLanguage.name=:name")
    Optional<UserHopeLanguage> findUserHopeLanguageByUserIdAndCountry(@Param("userId")Long userId, @Param("name")String name);

    @Modifying
    @Query("delete from UserHopeLanguage uhl where uhl.user.id=:userId")
    int deleteByUserId(@Param("userId")Long userId);

    @Query("select new com.project.chawchaw.dto.UserLanguageDto(uhl.hopeLanguage.abbr,count(uhl.hopeLanguage)) " +
            "from UserHopeLanguage uhl group by uhl.hopeLanguage " +
            "order by count (uhl.hopeLanguage) desc ")
    List<UserLanguageDto>getPopularHopeLanguage();
}
