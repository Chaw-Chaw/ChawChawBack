package com.project.chawchaw.repository;

import com.project.chawchaw.entity.document.PopularLanguage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<PopularLanguage,String> {



}
