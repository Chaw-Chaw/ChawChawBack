package com.project.chawchaw.repository;

import com.project.chawchaw.dto.elasticSearch.PopularHopeLanguage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchRepository extends ElasticsearchRepository<PopularHopeLanguage ,String> {
}
