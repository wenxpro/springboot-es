package com.example.highlevel;

import com.alibaba.fastjson.JSONObject;
import com.example.highlevel.bean.Book;
import com.example.highlevel.dao.Bookrepository;
import com.example.highlevel.util.ElasticUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class HighLevelApplicationTests {

    @Autowired
    Bookrepository bookrepository;

    @Autowired
    ElasticUtils elasticUtils;

    @Test
    void contextLoads() {
        Book book=new Book(1,"西游记","吴承恩");
        bookrepository.save(book);
    }

    @Test
    void testRepositories(){
        //查询
        //Elasticsearch Repositories提供and,by等一大堆关键字来连接JAVABEAN属性，我们写接口，他自动变成为实现类。
        List<Book> bookById = bookrepository.findBookById(1);
        System.out.println(bookById.get(0));
    }

    @Test
    void testUtil() throws IOException {
        String index = "test-index";
        elasticUtils.createIndex(index);

    }

    @Test
    void testUtilAdd() throws IOException {
        String index = "test-index";
        elasticUtils.addDocument(index,"001","{\"name\":\"阿萨的那家店哈登记户口是的金卡贷记卡大家看到\"}");
        elasticUtils.addDocument(index,"002","{\"name\":\"呵呵泥潭阿玛大数据库打死你健康大数据南科大\"}");
    }

    @Test
    void testUtilGet() throws IOException {
        String index = "test-index";
        JSONObject jsonObject = elasticUtils.getDocument(index,"001");
        System.out.println(jsonObject.toJSONString());
    }

    @Test
    void testUtilSearch() throws IOException {
        String index = "test-index";
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("name","阿");
        List<JSONObject> jsonObjects = elasticUtils.searchLike(index,objectMap,1,10);
        jsonObjects.forEach(jsonObject -> System.out.println(jsonObject.toJSONString()));

    }

}
