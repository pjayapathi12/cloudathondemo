package com.cloudathon.cloudathondemo;

import com.cloudathon.cloudathondemo.model.ErrorStatsRequest;
import com.cloudathon.cloudathondemo.model.TCMRequest;
import com.cloudathon.cloudathondemo.persistence.dao.ErrorStatsDao;
import com.cloudathon.cloudathondemo.persistence.dao.TCMDao;
import com.cloudathon.cloudathondemo.persistence.entity.ErrorStats;
import com.cloudathon.cloudathondemo.persistence.entity.TCM;
import com.cloudathon.cloudathondemo.service.DetailedViewService;
import com.cloudathon.cloudathondemo.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DemoController {

    @Autowired
    private EmployeeRepository repository;
    @Autowired
    private TCMDao tcmDao;
    @Autowired
    private DetailedViewService detailedViewService;

    @Autowired
    private RedisService redisService;

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
    public TCM getTCMDetails(@PathVariable String tcm, @RequestParam(required = false) boolean redis) throws JsonProcessingException {
        if (redis)
            return redisService.findTcm(tcm);
        return tcmDao.findByTcm(tcm);
    }

    @GetMapping("/getAllTCMs")
    public List<TCM> getTCMDetails(@RequestParam(required = false) boolean redis) throws JsonProcessingException {
        if(redis)
            return redisService.findAllTcm();
        return tcmDao.findAll();
    }

    @GetMapping("/testGetTCMDetails")
    public TCM getTestTCMDetails() {
        return tcmDao.findByTcm("TCM1");
    }



    @PostMapping("/getDetailedView")
    public List<ErrorStats> getDetailedView(@RequestBody ErrorStatsRequest request, @RequestParam(required = false) boolean redis)
            throws JsonProcessingException {
        if (redis)
            return redisService.findDetailResource(request.getTcm(), request.getResourceName());
        return detailedViewService.getDetailedView(request.getTcm(), request.getResourceName());
    }

    @GetMapping("/testGetDetailedView")
    public List<ErrorStats> getTestDetailedView() {
        return detailedViewService.getDetailedView("TCM1","bwflegacysvc");

    }

}
