package com.cloudathon.cloudathondemo.persistence.dao;

import com.cloudathon.cloudathondemo.model.ErrorStatsRequest;
import com.cloudathon.cloudathondemo.persistence.entity.ErrorStats;
import com.cloudathon.cloudathondemo.persistence.entity.TCM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ErrorStatsDao extends JpaRepository<ErrorStats, ErrorStatsRequest> {

    @Query(value = "SELECT * FROM ERRORSTATS  WHERE TCM = ?1 and RESOURCE_NAME = ?2",nativeQuery = true)
    public List<ErrorStats> fetchErrorStatsByTCMAndResource(String tcm,String resourceName);

    @Query(value = "SELECT * FROM ERRORSTATS  WHERE TCM = ?1",nativeQuery = true)
    public List<ErrorStats> fetchErrorStatsByTCM(String tcm);

    @Query(value = "SELECT DISTINCT RESOURCE_NAME FROM ERRORSTATS  WHERE TCM = ?1",nativeQuery = true)
    public List<String> fetchAllResourceNames(String tcm);

}
