package com.example.highlevel.dao;

import com.example.highlevel.bean.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Bookrepository extends ElasticsearchRepository<Book,Integer> {
    //ElasticsearchCrudRepository 已经过时
    List<Book> findBookById(int i);
}