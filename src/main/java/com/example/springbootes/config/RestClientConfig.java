package com.example.springbootes.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.time.Duration;

@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.elasticsearch.rest.uris}")
    private String hostAndPort;

    @Value("${spring.elasticsearch.rest.username}")
    private String username;

       @Value("${spring.elasticsearch.rest.password}")
    private String password;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(hostAndPort)
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(3))
                .withBasicAuth(username, password)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    // use the ElasticsearchEntityMapper
    @Bean
    @Override
    public EntityMapper entityMapper() {
        ElasticsearchEntityMapper entityMapper = new ElasticsearchEntityMapper(elasticsearchMappingContext(),
                new DefaultConversionService());
        entityMapper.setConversions(elasticsearchCustomConversions());

        return entityMapper;
    }

}
