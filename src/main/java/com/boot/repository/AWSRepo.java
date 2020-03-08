package com.boot.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component
public class AWSRepo {

    @Value("${app.http_proxy}")
    String appHttpProxy;


    public String getRdsStatus(String dbHost, String region) throws IOException, TimeoutException, InterruptedException{
        Map<String, String> envMap = new HashMap<>();
        envMap.put("http_proxy",appHttpProxy);
        envMap.put("AWS_DEFAULT_REGION",region);
        String[] cmds = new String[]{"aws","rds","describe-db-instances","--db-instance-identifier",dbHost,"--query","'DBInstances[0].DBInstanceStatus'"};
        //https://www.programcreek.com/java-api-examples/?api=org.zeroturnaround.exec.ProcessExecutor
        String resp = new ProcessExecutor().command(cmds).environment(envMap)
                .readOutput(true).execute().outputUTF8();
        return resp;
    }
}
