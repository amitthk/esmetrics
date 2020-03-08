package com.boot.repository;

import com.boot.model.HostSummary;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class InMemRepository {

    private HashMap<Long, HostSummary> lstHosts;
    private HostRepoRedisImpl hostRepoRedis;

    @Autowired
    public InMemRepository(HostRepoRedisImpl hostRepoRedisImpl){
        this.hostRepoRedis = hostRepoRedisImpl;
        //
    }

}
