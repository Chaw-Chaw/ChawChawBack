package com.project.chawchaw.repository.user;

import com.project.chawchaw.dto.user.UserDto;
import com.project.chawchaw.dto.user.UserSearch;
import com.project.chawchaw.dto.user.UsersDto;
import com.project.chawchaw.entity.*;
import com.project.chawchaw.repository.UserLanguageRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.project.chawchaw.entity.QCountry.*;
import static com.project.chawchaw.entity.QLanguage.*;
import static com.project.chawchaw.entity.QUser.user;
import static com.project.chawchaw.entity.QUserCountry.userCountry;
import static com.project.chawchaw.entity.QUserHopeLanguage.*;
import static com.project.chawchaw.entity.QUserLanguage.*;
import static org.springframework.util.StringUtils.hasText;


public class UserRepositoryImpl implements  UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Autowired
    UserLanguageRepository userLanguageRepository;



    public UserRepositoryImpl(EntityManager em){this.queryFactory=new JPAQueryFactory(em);}

//    @Override
//    public Slice<UsersDto> users(Long lastUserId, Pageable pageable, UserSearch userSearch,String school) {
//        List<UsersDto> usersDtos = queryFactory.select(Projections.constructor(UsersDto.class, user.id, user.imageUrl, user.content,
//                                user.regDate, user.views, user.country.size())).from(userCountry, userLanguage)
//                                .join(userCountry.country, country)
//
//                                .join(userLanguage.user, user)
//                                .join(userCountry.user, user).where(
////                                        countryEq(userSearch.getCountry())
//                                         languageEq(userSearch.getLanguage())
//                        , nameEq(userSearch.getName())
//                        , user.school.eq(school)).fetch();
//
//
//        return null;

//




//    }

    QLanguage language2=new QLanguage("language2");

    @Override
    public List<UsersDto> usersList(UserSearch userSearch) {


        int limit=3;
        if (userSearch.getIsFirst()){
            limit=6;
        }



        List<UsersDto> usersList = queryFactory.select(Projections.constructor(UsersDto.class, user.id, user.name ,user.imageUrl, user.content,

                user.regDate, user.views,user.toLikes.size(),user.repCountry,user.repLanguage,user.repHopeLanguage)).distinct().from(userLanguage)

                .join(userLanguage.language,language)
                .join(userLanguage.user,user)
                .join(user.hopeLanguage,userHopeLanguage)
                .join(userHopeLanguage.hopeLanguage,language2)

                .where(
                        hopeLanguageEq(userSearch.getHopeLanguage())
                        , languageEq(userSearch.getLanguage())
                        , nameEq(userSearch.getName())
                        ,user.school.eq(userSearch.getSchool())

                        ,excludeId(userSearch.getExcludes())
                        , user.role.eq(ROLE.USER)


                ).orderBy(
                        searchOrder(userSearch.getOrder())
                ).offset(0)

                .limit(limit)

                .fetch();
       return usersList;

    }



    public OrderSpecifier<?> searchOrder(String order) {
        if (hasText(order)) {
            if (order.equals("like")) return user.toLikes.size().desc();
            else if (order.equals("view")) return user.views.desc();

            else return user.regDate.desc();
        }
        else {
            return Expressions.numberTemplate(Double.class, "function('rand')").asc();
        }
    }

    private BooleanExpression excludeId(Set<Long> excludes) {
        if(excludes!=null&&!excludes.isEmpty()){
            return user.id.notIn(excludes);
        }
        return null;
    }

    private BooleanExpression hopeLanguageEq(String hope) {
        return hasText(hope) ? language2.abbr.eq(hope) : null;
    }
    private BooleanExpression schoolEq(String school) {
        return hasText(school) ? user.school.eq(school) : null;
    }


    private BooleanExpression nameEq(String name) {

        return hasText(name) ? user.name.eq(name) : null;
    }

//    private BooleanExpression hopeLanguageEq(String hopeLanguage) {
//        return  hasText(hopeLanguage)?user.hopeLanguage.eq(hopeLanguage):null;
//    }
    private BooleanExpression languageEq(String lang) {

        return hasText(lang) ? language.abbr.eq(lang) : null;
    }
//
//    private BooleanExpression countryEq(String country) {
//        return  hasText(country)?user.country.eq(country):null;
//    }
}
