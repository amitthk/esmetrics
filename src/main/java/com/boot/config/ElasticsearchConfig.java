package com.boot.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchConfig {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${elasticsearch.host}")
    private String elasticSearchHost;

    @Value("${elasticsearch.host}")
    private String elasticSearchPort;

    @Value("${elasticsearch.host}")
    private String elasticSearchUser;

    @Value("${elasticsearch.host}")
    private String elasticSearchPass;

    @Bean(destroyMethod = "close")
    public RestClient client(){
        RestClient client = RestClient.builder(new HttpHost(elasticSearchHost, Integer.parseInt(elasticSearchPort),"http"))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("accept","application/json"),
                        new BasicHeader("content-type","application/json")
                })
                .setFailureListener(new RestClient.FailureListener() {
                    @Override
                    public void onFailure(Node node){
                        logger.error(String.format("Low level rest api client failure! Node:  %s , host: %", node.getName(), node.getHost()));
                    }
                }).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credsProvider());
                    }
                }).build();
        return client;
    }

    @Bean
    private CredentialsProvider credsProvider() {
        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(elasticSearchUser,elasticSearchPass));
        return cp;
    }
}
