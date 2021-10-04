package com.project.chawchaw.dto.elasticSearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

@Document(indexName = "popularHopeLanguage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PopularHopeLanguage {
    @Id
    private String id;
    private String abbr;

    public static PopularHopeLanguage createPopularHopeLanguage(String abbr){
        PopularHopeLanguage popularHopeLanguage=new PopularHopeLanguage();
        popularHopeLanguage.abbr=abbr;
        return popularHopeLanguage;
    }


}
