package com.project.chawchaw.service;

import com.project.chawchaw.entity.document.PopularLanguage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import javax.annotation.Resource;


@SpringBootTest
class ElasticSearchServiceTest {

//    @Autowired
//    ElasticSearchService elasticSearchService;
    @Resource
    ElasticsearchRepository elasticsearchRepository;
    @Test
    public void createPopularLanguage()throws Exception{
       //given
        elasticsearchRepository.save(PopularLanguage.createPopularLanguage("en"));

       //when


       //then
    }


}