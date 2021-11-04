package com.project.chawchaw.service;

import com.project.chawchaw.config.elasticsearch.ElasticSearchConfig;
import com.project.chawchaw.entity.document.PopularLanguage;
import com.project.chawchaw.repository.ElasticSearchRepository;
import org.elasticsearch.client.ml.job.results.Bucket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest
class ElasticSearchServiceTest {

//    @Autowired
//    ElasticSearchService elasticSearchService;
    @Resource
    ElasticSearchRepository elasticsearchRepository;
    @Autowired
    ElasticSearchService elasticSearchService;

    @Test
    public void createPopularLanguage()throws Exception{
       //given

        elasticsearchRepository.save(PopularLanguage.createPopularLanguage("ii"));

       //when


       //then
    }

    @Test
    public void findPopularLanguageByUsers()throws Exception{
       //given
//        List<PopularLanguage> byPopularLanguageByUsers = elasticsearchRepository.findByAbbr("en");
//        System.out.println(byPopularLanguageByUsers.size());
         elasticSearchService.hi();
//        System.out.println(language
//        GroupBy.size());



        //when

       //then
    }

}