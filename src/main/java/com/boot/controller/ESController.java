package com.boot.controller;

import com.boot.repository.ESLowLevelRepo;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("elasticsearch")
public class ESController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ESLowLevelRepo esLowLevelRepo;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String show_index(@RequestParam(name = "IndexName")String indexName)
	{
		try {

			Response esResponse = esLowLevelRepo.listIndex(indexName);
			String responseBodyText = EntityUtils.toString(esResponse.getEntity());
			return(responseBodyText);
		}catch(Exception exc){
			StringWriter sw = new StringWriter();
			exc.printStackTrace(new PrintWriter(sw));
			String strStackTrace = sw.toString();
			logger.error(strStackTrace);
			return(strStackTrace);
		}
	}

}
