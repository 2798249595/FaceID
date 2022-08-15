package com.wllt.faceid.core.controller;

import com.wllt.faceid.core.db.domain.Mainboard;
import com.wllt.faceid.core.db.service.MainboardService;
import com.wllt.faceid.core.timed.TimedTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SCW
 * @date 2022/8/1 15:18
 */

@RestController
@RequestMapping("/mainboard")
public class MainBoard {

    @Autowired
    MainboardService mainboardService;

    @Autowired
    TimedTask timedTask;

    @GetMapping("/add")
    public void add(String id){
        Mainboard byId = mainboardService.getById(1);
        Mainboard mainboard=new Mainboard();
        mainboard.setId(1);
        mainboard.setMainboard(id);
        if (byId==null){
            mainboardService.save(mainboard);
        }else{
            mainboardService.updateById(mainboard);
        }
        timedTask.Task2();
    }
}
