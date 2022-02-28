package com.cloudathon.cloudathondemo;

import com.cloudathon.cloudathondemo.models.TcmEvent;
import com.cloudathon.cloudathondemo.persistence.entity.TCM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@Log4j2
@Service
public class RedisService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String TCM_CACHE = "TCM_ERRORSTATS_CACHE";
    private HashOperations<String, String, String> hashOperations;


    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public TCM findTcm(String  tcmId) throws JsonProcessingException {
        log.info("received tcm id to query redis {}", tcmId);

        String redisValue;
        //hashOperations = redisTemplate.opsForValue();
        if (hashOperations.hasKey(TCM_CACHE, tcmId)) {
            redisValue = hashOperations.get(TCM_CACHE, tcmId);
        } else {
            log.info("no tcm id found in redis");
            return null;
        }
        TcmEvent tcmEvent = objectMapper.readValue(redisValue, TcmEvent.class);

        TCM tcm = mapTcmEventToTCM(tcmEvent);
        //String redisValue = ops.ent.getTcmId());
        log.info("value returned from redis is {}", redisValue);

        log.info("event processed successfully");
        return tcm;

    }

    private TCM mapTcmEventToTCM(TcmEvent tcmEvent) {
        TCM tcm = new TCM();
        tcm.setTcm(tcmEvent.getTcmId());
        tcm.setSubmitterId(tcmEvent.getSubmitter());
        tcm.setEonId(tcmEvent.getEonId());
        return tcm;
    }

}


