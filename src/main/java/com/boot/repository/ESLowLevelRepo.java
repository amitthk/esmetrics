package com.boot.repository;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ESLowLevelRepo{

    private RestClient client;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public ESLowLevelRepo(RestClient client){
        this.client=client;
    }

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
    public CredentialsProvider credsProvider() {
        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(elasticSearchUser,elasticSearchPass));
        return cp;
    }


    public Response listIndex(String indexName) throws IOException{
        Request req = new Request( "Get", String.format("%s/_search",indexName));
        req.addParameter("pretty","true");
        Response resp = client.performRequest(req);
        return resp;
    }
}
