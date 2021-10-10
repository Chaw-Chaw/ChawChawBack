package com.project.chawchaw.entity.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

//인덱스명 무조건 소문자
@Document(indexName = "popularlanguage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PopularLanguage {
    @Id
    private String id;
    private String abbr;
    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
    private LocalDateTime regDate;



    public static PopularLanguage createPopularLanguage(String abbr){
        PopularLanguage popularHopeLanguage=new PopularLanguage();
        popularHopeLanguage.id= UUID.randomUUID().toString();
        popularHopeLanguage.abbr=abbr;
        popularHopeLanguage.regDate=LocalDateTime.now().withNano(0);
        return popularHopeLanguage;
    }


}
