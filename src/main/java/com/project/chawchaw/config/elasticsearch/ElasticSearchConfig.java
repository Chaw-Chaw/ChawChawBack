package com.project.chawchaw.config.elasticsearch;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


@Configuration
@ComponentScan(basePackages = { "com.project.chawchaw.service" })
@EnableElasticsearchRepositories(basePackages = "com.project.chawchaw.repository")
public class ElasticSearchConfig {
    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200") .build();
        return RestClients.create(clientConfiguration).rest();
//     return new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("localhost", 9200, "http"),
//                        new HttpHost("localhost", 9201, "http")));
    }

    @Bean public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }

//    public void hi()throws Exception{
//            GlobalAggregationBuilder globalAggregationBuilder = AggregationBuilders
//                    .global("agg")
//                    .subAggregation(AggregationBuilders.terms("groupbylanguage").field("abbr"));
//
//
//            SearchRequest searchRequest = new SearchRequest();
//            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//            searchSourceBuilder.aggregation(globalAggregationBuilder);
//            searchRequest.source(searchSourceBuilder);
//
//            SearchResponse search = client().search(searchRequest, RequestOptions.DEFAULT);
//
//            Global abbr = search.getAggregations().get("abbr");
//        System.out.print(abbr.getName()+"   ");
//      System.out.println(abbr.getDocCount());
//
//
//    }


}

