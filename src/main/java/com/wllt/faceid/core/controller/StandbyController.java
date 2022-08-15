package com.wllt.faceid.core.controller;

import com.wllt.faceid.core.db.domain.Standby;
import com.wllt.faceid.core.db.service.StandbyService;
import com.wllt.faceid.core.utils.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author SCW
 * @date 2022/7/23 17:14
 * 待机动画
 */
@RestController
@RequestMapping("/standby")
public class StandbyController {

    @Autowired
    StandbyService standbyService;

    /**
     * 获取所有待机动画
     * @return
     */
    @GetMapping("/list")
    public SaResult list(){
        List<Standby> list = standbyService.list();
        return SaResult.data(list);
    }



}
