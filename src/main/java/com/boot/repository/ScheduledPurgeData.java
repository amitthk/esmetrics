package com.boot.repository;

import com.boot.model.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;


class ScheduledPurgeDataService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedRate = 600000)
    public void purgeDataToDb()
    {
        try{


        }
        catch (Exception exc){
            logger.error(Utility.printStackTrace(exc));
        }
    }
}
