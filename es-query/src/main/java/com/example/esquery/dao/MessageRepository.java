package com.example.esquery.dao;

import com.example.esquery.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.Future;

public interface MessageRepository extends ElasticsearchRepository<Message, String> {

    //根据发送者返回消息列表，Pageable除了可以支持分页，也支持了排序
    //{“bool” : {“must” : {“field” : {“name” : “?”}}}}
    Page<Message> findBySender(String sender, Pageable pageable);

    Page<Message> findByTitle(String title, Pageable pageable);

    //通过Future异步获取数据，Top就是我们取数据集的第一条
    @Async
    Future<Message> findTopByMsg(String msg, Sort sort);

    //复合查询条件，必须同时满足
    //{“bool” : {“must” : [ {“field” : {“title” : “?”}}, {“field” : {“msg” : “?”}} ]}}
    Page<Message> findByTitleAndMsg(String title, String msg, Pageable pageable);

    //复合查询条件，满足其中之一
    //{“bool” : {“should” : [ {“field” : {“title” : “?”}}, {“field” : {“msg” : “?”}} ]}}
    List<Message> findByTitleOrMsg(String title, String msg);
}