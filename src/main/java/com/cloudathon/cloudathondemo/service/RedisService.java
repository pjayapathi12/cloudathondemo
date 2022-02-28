package com.cloudathon.cloudathondemo.service;

import com.cloudathon.cloudathondemo.models.TcmEvent;
import com.cloudathon.cloudathondemo.persistence.entity.ErrorStats;
import com.cloudathon.cloudathondemo.persistence.entity.TCM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @SneakyThrows
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

    @SneakyThrows
    public List<TCM> findAllTcm() throws JsonProcessingException {
        log.info("received tcm id to query redis {}");

        List<TCM> tcms = new ArrayList<>();

        Set<String> keys = hashOperations.keys(TCM_CACHE);
        if (keys.isEmpty()) {
            log.info("no tcms found in redis");
            return null;
        }
        keys.stream().forEach(x -> {
            String redisValue = hashOperations.get(TCM_CACHE, x);
            try {
                TcmEvent tcmEvent = objectMapper.readValue(redisValue, TcmEvent.class);
                TCM tcm = mapTcmEventToTCM(tcmEvent);
                tcms.add(tcm);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

        //String redisValue = ops.ent.getTcmId());
        log.info("value returned from redis is {}", tcms);

        log.info("event processed successfully");
        return tcms;

    }


    @SneakyThrows
    public List<ErrorStats> findDetailResource(String tcmId, String resourceName) throws JsonProcessingException {
        log.info("received tcm id to query redis {}", tcmId);

        List<ErrorStats> result = new ArrayList<>();
        String redisValue;
        //hashOperations = redisTemplate.opsForValue();
        if (hashOperations.hasKey(TCM_CACHE, tcmId)) {
            redisValue = hashOperations.get(TCM_CACHE, tcmId);
        } else {
            log.info("no tcm id found in redis");
            return null;
        }
        TcmEvent tcmEvent = objectMapper.readValue(redisValue, TcmEvent.class);

        result = mapTcmEventToErrorStats(tcmEvent, resourceName);
        //String redisValue = ops.ent.getTcmId());
        log.info("value returned from redis is {}", redisValue);

        log.info("event processed successfully");
        return result;

    }

    private List<ErrorStats> mapTcmEventToErrorStats(TcmEvent tcmEvent, String resourceName) throws JsonProcessingException {
        List<ErrorStats> result = new ArrayList<>();

        if (tcmEvent == null || tcmEvent.getResources() == null || tcmEvent.getResources().isEmpty())
            return null;

        TcmEvent.TcmResource tcmResource = tcmEvent.getResources().stream().filter(x -> x.getResourceName() != null && x.getResourceName().equalsIgnoreCase(resourceName))
                .findAny().orElse(null);

        for ( TcmEvent.ErrorStat e : tcmResource.getErrorStats() ) {
            ErrorStats errorStats = new ErrorStats();
            errorStats.setErrorType(e.getErrorType());
            errorStats.setErrorName(e.getErrorName());
            errorStats.setTcm(tcmEvent.getTcmId());
            errorStats.setResourceName(tcmResource.getResourceName());
            errorStats.setJiraStatus(e.getJiraStatus());
            errorStats.setJira(e.getJira());
            errorStats.setData(objectMapper.writeValueAsString(e.getErrorData()));
            result.add(errorStats);
        }
        return result;

    }

    private TCM mapTcmEventToTCM(TcmEvent tcmEvent) {
        TCM tcm = new TCM();
        tcm.setTcm(tcmEvent.getTcmId());
        tcm.setSubmitterId(tcmEvent.getSubmitter());
        tcm.setEonId(tcmEvent.getEonId());
        return tcm;
    }


}


