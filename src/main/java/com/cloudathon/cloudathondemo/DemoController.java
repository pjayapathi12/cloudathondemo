package com.cloudathon.cloudathondemo;

import com.cloudathon.cloudathondemo.model.ErrorStatsRequest;
import com.cloudathon.cloudathondemo.model.TCMRequest;
import com.cloudathon.cloudathondemo.persistence.dao.ErrorStatsDao;
import com.cloudathon.cloudathondemo.persistence.dao.TCMDao;
import com.cloudathon.cloudathondemo.persistence.entity.ErrorStats;
import com.cloudathon.cloudathondemo.persistence.entity.TCM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DemoController {

    @Autowired
    private EmployeeRepository repository;
    @Autowired
    private TCMDao tcmDao;
    private ErrorStatsDao errorStatsDao;

    @PostMapping("/employee")
    public Employee addEmployee(@RequestBody Employee employee) {
        return repository.save(employee);
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return repository.findAll();
    }

    @GetMapping("/test")
    public String test() {
        return "Test Successful with Azure spring boot web app deployment and setup";
    }

    @GetMapping("/getTCMDetails/{tcm}")
    public TCM getTCMDetails(@PathVariable String tcm) {
        return tcmDao.findByTcm(tcm);
    }

    @GetMapping("/testGetTCMDetails")
    public TCM getTestTCMDetails() {
        return tcmDao.findByTcm("TCM1");
    }

    @PostMapping("/getDetailedView")
    public List<ErrorStats> getDetailedView(@RequestBody ErrorStatsRequest request) {
        return errorStatsDao.fetchErrorStatsByTCMAndResource(request.getTcm(), request.getRresourceName());
    }

}
