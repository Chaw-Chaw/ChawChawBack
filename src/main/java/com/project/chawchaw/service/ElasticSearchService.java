package com.project.chawchaw.service;

import com.project.chawchaw.config.elasticsearch.ElasticSearchConfig;
import com.project.chawchaw.entity.document.PopularLanguage;
import com.project.chawchaw.repository.ElasticSearchRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {
    private final ElasticSearchRepository elasticSearchRepository;
    private RestHighLevelClient restHighLevelClient;
    private BulkProcessor bulkProcessor;


//    public List<PopularLanguage> getLanguageGroupBy()throws Exception{
//        Map<String,Integer>map=new HashMap<>();
//
//        for (PopularLanguage popularLanguage : elasticSearchRepository.findAll()) {
//            if(map.containsKey())
//
//        }
//
//
//    }
    public void hi()throws Exception{

    this.restHighLevelClient=new RestHighLevelClient(RestClient.builder(HttpHost.create("http://127.0.0.1:9200")));
    TermsAggregationBuilder field = AggregationBuilders
            .terms("field").field("abbr");

    SearchRequest searchRequest = new SearchRequest("_doc");
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.matchAllQuery());
    searchSourceBuilder.aggregation(field);
    searchRequest.source(searchSourceBuilder);

    SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);


    System.out.println(search.getHits().getTotalHits());
    Terms result = search.getAggregations().get("abbr");
    for (Terms.Bucket entry : result.getBuckets()) {
        System.out.println(entry.getKey());
        System.out.println(entry.getDocCount());
    }


}



}