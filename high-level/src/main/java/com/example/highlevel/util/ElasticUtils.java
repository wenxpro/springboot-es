package com.example.highlevel.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElasticUtils {

  private static final Logger LOGGER = LogManager.getLogger(ElasticUtils.class);

  @Autowired
  RestHighLevelClient client;
//  private Sniffer sniffer;

//  @Value("${es.host}")
//  private String esHost;
//  @Value("${es.port}")
//  private int esPort;
//
//  @Value("${es.username}")
//  private String username;
//  @Value("${es.password}")
//  private String password;
//  @Value("${es.needAuth}")
//  private boolean needAuth;

//  public void init() {
//    if (needAuth) {
//      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//      credentialsProvider.setCredentials(AuthScope.ANY,
//          new UsernamePasswordCredentials(username, password));  //es账号密码（默认用户名为elastic）
//      client = new RestHighLevelClient(RestClient.builder(new HttpHost(esHost, esPort, "http"))
//          .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//            @Override
//            public HttpAsyncClientBuilder customizeHttpClient(
//                HttpAsyncClientBuilder httpClientBuilder) {
//              httpClientBuilder.disableAuthCaching();
//              return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//            }
//          })); // 高可用版本，看填写多个服务器地址
//    } else {
//      RestClientBuilder builder = RestClient.builder(new HttpHost(esHost, esPort, "http"))
//          .setRequestConfigCallback(requestConfigBuilder ->
//              requestConfigBuilder.setConnectTimeout(10000).setSocketTimeout(30000));
//      client = new RestHighLevelClient(builder);
//    }
//

//
//    //十秒刷新并更新一次节点
//    sniffer = Sniffer.builder(client.getLowLevelClient())
//        .setSniffAfterFailureDelayMillis(10000)
//        .build();
//  }

//  public void close() {
//    if (client != null) {
//      try {
//        sniffer.close();
//        client.close();
//      } catch (Exception e) {
//        // IGNORE
//      }
//    }
//  }

  public List<JSONObject> searchLike(String indexName, Map<String, Object> condition, int page,
                                     int pageSize) throws IOException {
    if (!checkExistIndex(indexName)) {
      return Lists.newArrayList();
    }
    SearchRequest searchRequest = new SearchRequest(indexName);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

    if (condition != null) {
      for (String k : condition.keySet()) {
        TermQueryBuilder termQuery = QueryBuilders.termQuery(k, condition.get(k));
        boolQueryBuilder.must(termQuery);
      }
    }

    sourceBuilder.query(boolQueryBuilder);
    // 分页
    sourceBuilder.from((page - 1) * pageSize);
    sourceBuilder.size(pageSize);
    //匹配度从高到低
    sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
    //sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC)); //根据自己的需求排序

    searchRequest.source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

    RestStatus restStatus = searchResponse.status();
    if (restStatus != RestStatus.OK) {
      LOGGER.error(restStatus);
      return null;
    }
    SearchHits hits = searchResponse.getHits();
    List<JSONObject> matchRsult = Lists.newArrayList();
    for (SearchHit hit : hits.getHits()) {
      matchRsult.add(JSONObject.parseObject(hit.getSourceAsString()));
    }
    return matchRsult;
  }

  public List<JSONObject> searchFullIndex(String indexName, int page, int pageSize, Object content,
      String... fields) throws IOException {
    if (!checkExistIndex(indexName)) {
      return Lists.newArrayList();
    }
    SearchRequest searchRequest = new SearchRequest(indexName);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

//        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(fieldName, keyword).fuzziness(Fuzziness.AUTO); //模糊匹配
    MultiMatchQueryBuilder matchQueryBuilder = new MultiMatchQueryBuilder(content, fields);

    sourceBuilder.query(matchQueryBuilder);
    // 分页
    sourceBuilder.from((page - 1) * pageSize);
    sourceBuilder.size(pageSize);
    //匹配度从高到低
    sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
    //sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC)); //根据自己的需求排序

    searchRequest.source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

    RestStatus restStatus = searchResponse.status();
    if (restStatus != RestStatus.OK) {
      LOGGER.error(restStatus);
      return null;
    }
    SearchHits hits = searchResponse.getHits();
    List<JSONObject> matchRsult = Lists.newArrayList();
    for (SearchHit hit : hits.getHits()) {
      matchRsult.add(JSONObject.parseObject(hit.getSourceAsString()));
    }
    return matchRsult;
  }

  public List<JSONObject> searchFullIndexFilterUserId(String indexName, int userId, int page,
      int pageSize, Object content, String... fields) throws IOException {
    if (!checkExistIndex(indexName)) {
      return Lists.newArrayList();
    }
    SearchRequest searchRequest = new SearchRequest(indexName);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

    TermQueryBuilder termQuery = QueryBuilders.termQuery("userId", userId);
    boolQueryBuilder.must(termQuery);

//        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(fieldName, keyword).fuzziness(Fuzziness.AUTO); //模糊匹配
    MultiMatchQueryBuilder matchQueryBuilder = new MultiMatchQueryBuilder(content, fields);
    boolQueryBuilder.must(matchQueryBuilder);

    sourceBuilder.query(boolQueryBuilder);
    // 分页
    sourceBuilder.from((page - 1) * pageSize);
    sourceBuilder.size(pageSize);
    //匹配度从高到低
    sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
    //sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC)); //根据自己的需求排序

    searchRequest.source(sourceBuilder);
    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

    RestStatus restStatus = searchResponse.status();
    if (restStatus != RestStatus.OK) {
      LOGGER.error(restStatus);
      return null;
    }
    SearchHits hits = searchResponse.getHits();
    List<JSONObject> matchRsult = Lists.newArrayList();
    for (SearchHit hit : hits.getHits()) {
      matchRsult.add(JSONObject.parseObject(hit.getSourceAsString()));
    }
    return matchRsult;
  }

  public void deleteDocument(String indexName, String id) throws IOException {
    DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
    client.delete(deleteRequest, RequestOptions.DEFAULT);
  }

  public JSONObject getDocument(String indexName, String id) throws IOException {
    GetRequest request = new GetRequest(indexName, id);
    GetResponse response = client.get(request, RequestOptions.DEFAULT);
    if (!response.isExists()) {
      return null;
    } else {
      return JSONObject.parseObject(response.getSourceAsString());
    }
  }

  public void addDocument(String indexName, String id, String jsonStr) throws IOException {
    IndexRequest indexRequest = new IndexRequest(indexName).id(id)
        .source(jsonStr, XContentType.JSON);
    client.index(indexRequest, RequestOptions.DEFAULT);
  }

  public void updateDocument(String indexName, String id, String jsonStr) throws IOException {
    UpdateRequest updateRequest = new UpdateRequest(indexName, id);
    updateRequest.doc(jsonStr, XContentType.JSON);
    client.update(updateRequest, RequestOptions.DEFAULT);
  }

  public void updateDocument(String indexName, String id, JSONObject updatedFields)
      throws IOException {
    UpdateRequest updateRequest = new UpdateRequest(indexName, id);
    for (String k : updatedFields.keySet()) {
      updateRequest.doc(k, updatedFields.get(k));
    }
    client.update(updateRequest, RequestOptions.DEFAULT);
  }

  public boolean checkExistIndex(String indexName) throws IOException {
    GetIndexRequest request = new GetIndexRequest(indexName);
    return client.indices().exists(request, RequestOptions.DEFAULT);
  }

  public void deleteIndex(String indexName) throws IOException {
    DeleteIndexRequest request = new DeleteIndexRequest(indexName);
    client.indices().delete(request, RequestOptions.DEFAULT);
  }

  public boolean createIndex(String indexName) throws IOException {
    CreateIndexRequest request = new CreateIndexRequest(indexName);
    JSONObject setting = new JSONObject();
    setting.put("index.analysis.analyzer.default.type", "ik_smart");
    request.settings(setting.toString(), XContentType.JSON);
    CreateIndexResponse createIndexResponse = client.indices()
        .create(request, RequestOptions.DEFAULT);
    if (!createIndexResponse.isAcknowledged()) {
      return false;
    }
    return true;
  }

  // 批量插入
//    private static void batchInsert(List<Product> products) throws IOException {
//        BulkRequest request = new BulkRequest();
//
//        for (Product product : products) {
//            Map<String,Object> m  = product.toMap();
//            IndexRequest indexRequest= new IndexRequest(indexName, String.valueOf(product.getId())).source(m);
//            request.add(indexRequest);
//        }
//
//        client.bulk(request);
//        System.out.println("批量插入完成");
//    }

  public static void main2(String[] args) throws Exception {
//		boolean a = checkExistIndex("gooking");
//		System.out.println("checkExistIndex:" + a);
//		if (a) {
//			deleteIndex("gooking");
//			System.out.println("删除索引 gooking");
//		} else {
//			createIndex("gooking");
//			a = checkExistIndex("gooking");
//			System.out.println("checkExistIndex:" + a);
//		}

//    	String id = "testid12";
//    	JSONObject json = new JSONObject();
//    	json.put("a", 123);
//    	json.put("b", "测试");
//    	json.put("country", "我爱北京天安门");
//    	addDocument("gooking", id, json.toString());
//    	json.put("a", 888);
//    	json.put("cvcvcv", 000);
//    	updateDocument("gooking", id, json.toString());
//    	JSONObject b = getDocument("gooking", id);
//    	System.out.println("getDocument:" + b);

//    	deleteDocument("gooking", id);

//    	Map<String, Object> condition = Maps.newHashMap();
//    	condition.put("country", "北京");
//    	List<Map<String, Object>> c = searchLike("gooking", condition, 1, 50);
//    	System.out.println(c);

//    	List<Map<String, Object>> d = searchFullIndex("gooking", 1, 50, "天安门", "country", "b");
//    	System.out.println(d);
  }

}