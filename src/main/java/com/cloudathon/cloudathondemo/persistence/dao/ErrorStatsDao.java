package com.cloudathon.cloudathondemo.persistence.dao;

import com.cloudathon.cloudathondemo.model.ErrorStatsRequest;
import com.cloudathon.cloudathondemo.persistence.entity.ErrorStats;
import com.cloudathon.cloudathondemo.persistence.entity.TCM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ErrorStatsDao extends JpaRepository<ErrorStats, ErrorStatsRequest> {

    @Query("SELECT tcm,resourceName FROM ErrorStats  WHERE tcm = ?1 and resourceName = ?2")
    public List<ErrorStats> fetchErrorStatsByTCMAndResource(String tcm,String resourceName);
}
