package com.wllt.faceid.core.controller;

import com.wllt.faceid.core.db.domain.Record;
import com.wllt.faceid.core.db.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SCW
 * @date 2022/7/24 15:37
 * 记录场景和角色的使用次数
 */
@RestController
@RequestMapping("/record")
public class RecordController {


    @Autowired
    RecordService recordService;

    /**
     *
     * @param Scene 场景名
     * @param Role 角色名
     */
    @GetMapping("install")
    public void Install (String Scene,String Role){
        Record record=new Record();
        record.setId(0);
        record.setScene(Scene);
        record.setRole(Role);
        recordService.save(record);
    }

}
