package com.boot.repository;

import com.boot.model.Utility;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ESLowLevelRepo {

    private RestClient client;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("classpath:server_status_query.json")
    private Resource server_status_query;

    @Autowired
    public ESLowLevelRepo(RestClient client) {
        this.client = client;
    }

    @Value("${elasticsearch.host}")
    private String elasticSearchHost;

    @Value("${elasticsearch.host}")
    private String elasticSearchPort;

    @Value("${elasticsearch.host}")
    private String elasticSearchUser;

    @Value("${elasticsearch.host}")
    private String elasticSearchPass;

    private HttpEntity entity;

    @Bean(destroyMethod = "close")
    public RestClient client() {
        RestClient client = RestClient.builder(new HttpHost(elasticSearchHost, Integer.parseInt(elasticSearchPort), "http"))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("accept", "application/json"),
                        new BasicHeader("content-type", "application/json")
                })
                .setFailureListener(new RestClient.FailureListener() {
                    @Override
                    public void onFailure(Node node) {
                        logger.error(String.format("Low level rest api client failure! Node:  %s , host: %", node.getName(), node.getHost()));
                    }
                }).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credsProvider());
                    }
                }).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                        return builder.setConnectTimeout(5000).setSocketTimeout(60000);
                    }
                }).build();
        return client;
    }

    @Bean
    public CredentialsProvider credsProvider() {
        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticSearchUser, elasticSearchPass));
        return cp;
    }


    public Response listIndex(String indexName) throws IOException {
        Request req = new Request("Get", String.format("%s/_search", indexName));
        req.addParameter("pretty", "true");
        Response resp = client.performRequest(req);
        return resp;
    }

    public Integer getCountByProcessNameAndUser(String hostname,String process, String userName){
        String queryStr = String.format("beat.hostname: %s AND system.process.name: %s and system.process.username: %s", hostname,process,userName);
        return  getHostCount(queryStr);
    }

    public Integer getCountByCmdLine(String hostname,String process, String cmdLine){
        String queryStr = String.format("beat.hostname: %s AND system.process.cmdline: %s and system.process.cmdline: *%s*", hostname,process,cmdLine);
        return  getHostCount(queryStr);
    }

    public Integer getHostCount(String queryString) {

        try {
            String queryFormat = Utility.asString(server_status_query);
            String query_string = String.format(queryFormat, queryString);
            entity = new NStringEntity(query_string, ContentType.APPLICATION_JSON);
            Request req = new Request("Get", String.format("%s/_search", entity));
            req.addParameter("pretty", "true");
            Response resp = client.performRequest(req);
            String entityString = EntityUtils.toString(resp.getEntity());

            JSONObject jsonObject = new JSONObject(entityString);
            jsonObject = jsonObject.getJSONObject("aggregations");
            jsonObject = jsonObject.getJSONObject("1");
            Integer result = jsonObject.getInt("value");
            return result;

        } catch (Exception e) {
            logger.error("Error");
            return -100;
        }
    }

}
