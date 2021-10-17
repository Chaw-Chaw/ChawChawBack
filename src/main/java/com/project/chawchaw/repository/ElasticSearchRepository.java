package com.project.chawchaw.repository;

import com.project.chawchaw.entity.document.PopularLanguage;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.ml.job.results.Bucket;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<PopularLanguage,String> {


//    @Query("{\"size\" : \"0\" , \"aggs\" : {\"groupbylanguage\" : {\"terms\" : { \"field\" : \"abbr\" }}}}")
//    @Query("select * from PopularLanguage where abbr=:ko")
    List<PopularLanguage>findByAbbr(String abbr);
//    List<PopularLanguage>







}
