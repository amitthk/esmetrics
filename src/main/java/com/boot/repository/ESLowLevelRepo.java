package com.boot.repository;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ESLowLevelRepo{

    private RestClient client;

    @Autowired
    public ESLowLevelRepo(RestClient client){
        this.client=client;
    }

    public Response listIndex(String indexName) throws IOException{
        Request req = new Request( "Get", String.format("%s/_search",indexName));
        req.addParameter("pretty","true");
        Response resp = client.performRequest(req);
        return resp;
    }
}
