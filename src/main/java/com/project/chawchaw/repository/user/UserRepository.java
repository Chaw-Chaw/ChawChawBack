package com.project.chawchaw.repository.user;

import com.project.chawchaw.dto.user.UserCountBySchoolDto;
import com.project.chawchaw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>,UserRepositoryCustom {
    Optional<User> findUserByEmailAndProvider(String email, String provider);

    @Query("select u from User u where u.email=:email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select new com.project.chawchaw.dto.user.UserCountBySchoolDto(u.school,count(u))" +
            " from User u group by u.school order by count(u) desc")
    List<UserCountBySchoolDto> getUserCountBySchool();

//    @Query("select u from User u join fetch u.country c join fetch u.language l join fetch u.hopeLanguage hl join fetch c.country join fetch l.language where u.id=:userId")
//    Optional<User>findByUserWithLanguageAndCountry(@Param("userId")Long userId);

//    @Query("select u from User u where ")


//    @Query("select u from User u where u")





}

