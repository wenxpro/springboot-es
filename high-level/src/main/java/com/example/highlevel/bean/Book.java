package com.example.highlevel.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "at")
@Data
@AllArgsConstructor
public class Book {
    private Integer id;
    private String bookname;
    private String author;

}