package com.cloudathon.cloudathondemo.service;

import com.cloudathon.cloudathondemo.models.*;
import com.cloudathon.cloudathondemo.persistence.dao.ErrorStatsDao;
import com.cloudathon.cloudathondemo.persistence.entity.ErrorStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SummaryViewResponseService {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private DetailedViewService detailedViewService;
    @Autowired
    private ErrorStatsDao errorStatsDao;

    public List<SummaryViewResponse> getSummaryView(String tcm, String resourceName){
        List<SummaryViewResponse> summaryViewResponseList = new ArrayList<>();
        try {

            List<String> resourceNameList = errorStatsDao.fetchAllResourceNames(tcm);
            log.info("Resource Name List Size {}", resourceNameList.size());
            for (int rIndex = 0; rIndex < resourceNameList.size(); rIndex++) {
                String rName = resourceNameList.get(rIndex).toString();
                log.info("Compute Summary View Response for ResourceName {}", rName);
                List<ErrorStats> errorStatsList = detailedViewService.getDetailedView(tcm, rName);
                log.info("Error Stats List Size for ResourceName {} is {} ", rName, errorStatsList.size());
                SummaryViewResponse summaryViewResponse = new SummaryViewResponse();
                HashMap envCountMap = new HashMap<>();
                String envCountMapKey = "";
                for (int i = 0; i < errorStatsList.size(); i++) {
                    String errorType = errorStatsList.get(i).getErrorType();
                    String dataStr = errorStatsList.get(i).getData();
                    log.info("dataStr  {} ", dataStr);
                    dataStr = "{\"data\":" + dataStr ;
                    log.info("dataStr  {}  adding prefix ", dataStr);
                    dataStr =  dataStr + "}" ;
                    log.info("dataStr  {} after adding postfix ", dataStr);
                    DetailedViewResponseData data = mapper.readValue(dataStr, DetailedViewResponseData.class);
                    List<DetailedViewResponseEnvItem> envList = data.getData();
                    int dataCount = envList.size();
                    for(int j =0; j< dataCount ; j++){
                        String env = envList.get(j).getEnv();
                        Integer envTotal = envList.get(j).getTotal();
                        envCountMapKey = env + "_" + errorType;
                        if(envCountMap.containsKey(envCountMapKey)){
                            Integer existingCount = (Integer)envCountMap.get(envCountMapKey);
                            log.info("Key {} Found with count {}",envCountMapKey,existingCount);
                            envCountMap.put(envCountMapKey,existingCount+envTotal);

                        }else {
                            envCountMap.put(envCountMapKey,envTotal);
                            log.info("Adding Key {}  with count {}",envCountMapKey,envTotal);
                        }

                    }
                }
                summaryViewResponse.setResourceName(rName);
                List<SummaryViewStatsItem> summaryViewStatsItemList = new ArrayList<>();
                SummaryViewStatsItem prodSummaryViewStatsItem = new SummaryViewStatsItem();
                prodSummaryViewStatsItem.setEnv("PROD");
                List<SummaryViewStatsErrorItem> prodErrorList = new ArrayList<>();


                SummaryViewStatsItem qaSummaryViewStatsItem = new SummaryViewStatsItem();
                qaSummaryViewStatsItem.setEnv("QA");
                List<SummaryViewStatsErrorItem> qaErrorList = new ArrayList<>();

                Set<String> keyList = envCountMap.keySet();
                Iterator<String> i = keyList.iterator();
                while(i.hasNext()){

                    String currentKey = i.next();
                    log.info("Checking Key {}",currentKey);
                    SummaryViewStatsErrorItem errorItem = new SummaryViewStatsErrorItem();
                    if(currentKey.contains("prod")){
                        String[] array = currentKey.split("_");
                        log.info("Checking error type value in Key {}",array[1]);
                        errorItem.setErrorType(array[1]);
                        errorItem.setErrorCount((Integer) envCountMap.get(currentKey));
                        prodErrorList.add(errorItem);
                    }
                    else if(currentKey.contains("qa")){
                        String[] array = currentKey.split("_");
                        log.info("Checking error type value in Key {}",array[1]);
                        errorItem.setErrorType(array[1]);
                        errorItem.setErrorCount((Integer) envCountMap.get(currentKey));
                        qaErrorList.add(errorItem);
                    }


                }
                prodSummaryViewStatsItem.setError(prodErrorList);
                qaSummaryViewStatsItem.setError(qaErrorList);
                summaryViewStatsItemList.add(prodSummaryViewStatsItem);
                summaryViewStatsItemList.add(qaSummaryViewStatsItem);
                summaryViewResponse.setSummaryViewStats(summaryViewStatsItemList);
                summaryViewResponseList.add(summaryViewResponse);


            }

        }catch(Exception e){
            log.info("Exception:{}",e);
        }
        return summaryViewResponseList;
    }

}
