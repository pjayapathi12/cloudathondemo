package com.cloudathon.cloudathondemo.service;

import com.cloudathon.cloudathondemo.model.ErrorStatsRequest;
import com.cloudathon.cloudathondemo.persistence.dao.ErrorStatsDao;
import com.cloudathon.cloudathondemo.persistence.entity.ErrorStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DetailedViewService {
    @Autowired
    private ErrorStatsDao errorStatsDao;

    public List<ErrorStats> getDetailedView(String tcm,String resourceName){

        List<Object[]> objList = errorStatsDao.fetchErrorStatsByTCMAndResource(tcm,resourceName);
        List<ErrorStats> errorStats = new ArrayList<>();
        if(null != objList && !objList.isEmpty()) {
            log.info("DB Result Size {}", objList.size());


            objList.forEach(obj ->
                    errorStats.add(
                            new ErrorStats(obj[0].toString(),
                                    obj[6].toString(),
                                    obj[1].toString(),
                                    obj[4].toString(),
                                    obj[2].toString(),
                                    obj[5].toString(),
                                    obj[3].toString())));

            log.info("Service Response Size:{}, First Result{}", errorStats.size(), errorStats.get(0));
        }
        return errorStats;

    }
}
