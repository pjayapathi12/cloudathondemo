package com.cloudathon.cloudathondemo.persistence.dao;

import com.cloudathon.cloudathondemo.model.ErrorStatsRequest;
import com.cloudathon.cloudathondemo.persistence.entity.ErrorStats;
import com.cloudathon.cloudathondemo.persistence.entity.TCM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ErrorStatsDao extends JpaRepository<ErrorStats, ErrorStatsRequest> {

    @Query(value = "SELECT * FROM ERROR_STATS  WHERE TCM = ?1 and RESOURCE_NAME = ?2",nativeQuery = true)
    public List<Object[]> fetchErrorStatsByTCMAndResource(String tcm,String resourceName);
}
