package com.project.chawchaw.repository.user;

import com.project.chawchaw.dto.admin.AdminUserSearch;
import com.project.chawchaw.dto.admin.UsersByAdminDto;
import com.project.chawchaw.dto.user.*;
import com.project.chawchaw.entity.*;
import com.project.chawchaw.repository.UserLanguageRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Set;

import static com.project.chawchaw.entity.QCountry.*;
import static com.project.chawchaw.entity.QLanguage.*;
import static com.project.chawchaw.entity.QUser.user;
import static com.project.chawchaw.entity.QUserCountry.userCountry;
import static com.project.chawchaw.entity.QUserHopeLanguage.*;
import static com.project.chawchaw.entity.QUserLanguage.*;
import static com.querydsl.jpa.JPAExpressions.*;
import static org.springframework.util.StringUtils.hasText;


public class UserRepositoryImpl implements  UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Autowired
    UserLanguageRepository userLanguageRepository;


    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

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

    QLanguage language2 = new QLanguage("language2");

    @Override
    public List<UsersDto> usersList(UserSearch userSearch) {


        int limit = 3;
        if (userSearch.getIsFirst()) {
            limit = 6;
        }


        List<UsersDto> usersList = queryFactory.select(Projections.constructor(UsersDto.class, user.id, user.name, user.imageUrl, user.content,

                user.regDate.stringValue(), user.views, user.toLikes.size().longValue(), user.repCountry, user.repLanguage, user.repHopeLanguage)).distinct().from(userLanguage)

                .join(userLanguage.language, language)
                .join(userLanguage.user, user)
                .join(user.hopeLanguage, userHopeLanguage)
                .join(userHopeLanguage.hopeLanguage, language2)

                .where(
                        hopeLanguageEq(userSearch.getHopeLanguage())
                        , languageEq(userSearch.getLanguage())
                        , nameEq(userSearch.getName())
                        , user.school.eq(userSearch.getSchool())

                        , excludeId(userSearch.getExcludes())
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
        } else {
            return Expressions.numberTemplate(Double.class, "function('rand')").asc();
        }
    }

    private BooleanExpression excludeId(Set<Long> excludes) {
        if (excludes != null && !excludes.isEmpty()) {
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

        return hasText(name) ? user.name.contains(name) : null;
    }

    private BooleanExpression languageEq(String lang) {

        return hasText(lang) ? language.abbr.eq(lang) : null;
    }


    /**
     * admin 전체회원 조회
     * **/

    QUser user1 = new QUser("user1");
    QUser user2= new QUser("user2");
    QUser user3= new QUser("user3");
    @Override
    public Page<UsersByAdminDto> usersListByAdmin(AdminUserSearch adminUserSearch, Pageable pageable) {
        QueryResults<UsersByAdminDto> usersByAdminDtoQueryResults = queryFactory.select(Projections.constructor(UsersByAdminDto.class, user.id, user.name, user.school, user.email,

                user.repCountry, user.repLanguage, user.repHopeLanguage, user.toLikes.size().longValue(), user.views, user.regDate.stringValue()))
                .from(user).where(
                        userLanguageEq(adminUserSearch.getLanguage()),
                        userHopeLanguageEq(adminUserSearch.getHopeLanguage()),
                        userCountryEq(adminUserSearch.getCountry()),
                        schoolEq(adminUserSearch.getSchool()),
                        nameEq(adminUserSearch.getName())

                ).orderBy(
                        searchOrderByAdmin(adminUserSearch.getOrder(), adminUserSearch.getSort()))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();

        return new PageImpl<>(usersByAdminDtoQueryResults.getResults(),pageable,usersByAdminDtoQueryResults.getTotal());





    }
    private BooleanExpression userLanguageEq(String languageEq) {

        return hasText(languageEq) ?  user.in(
                select(user3).from(userLanguage)
                .join(userLanguage.language ,language)
                .join(userLanguage.user,user3)
                .where(language.abbr.eq(languageEq))) : null;
    }
    private BooleanExpression userHopeLanguageEq(String hopeLanguageEq) {
        return hasText(hopeLanguageEq) ?  user.in(
                select(user1).from(userHopeLanguage)
                        .join(userHopeLanguage.hopeLanguage , language)
                        .join(userHopeLanguage.user,user1)
                        .where(language.abbr.eq(hopeLanguageEq))):null;
    }
    private BooleanExpression userCountryEq(String countryEq) {
        return hasText(countryEq) ? user.in(
                select(user2).from(userCountry)
                        .join(userCountry.country , country)
                        .join(userCountry.user,user2)
                        .where(country.name.eq(countryEq))):null;

    }
    private OrderSpecifier<?> searchOrderByAdmin(String order, String sort) {
        if (hasText(order)&&hasText(sort)) {
            if (order.equals("like")) {
                if(sort.equals("desc")) return user.toLikes.size().desc();
                else return user.toLikes.size().asc();
            }
            else if (order.equals("view")){
                if(sort.equals("desc"))return user.views.desc();
                else return user.views.asc();
            }
            else if (order.equals("name")) {
                if(sort.equals("desc"))return user.name.desc();
                else return user.name.asc();
            }
            else {
                if(sort.equals("desc"))return user.regDate.desc();
                else return user.regDate.asc();
            }
        }
        else {

            return user.regDate.desc();
        }
    }

}



