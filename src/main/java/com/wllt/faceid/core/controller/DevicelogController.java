package com.wllt.faceid.core.controller;

import com.wllt.faceid.core.db.domain.Devicelog;
import com.wllt.faceid.core.db.service.DevicelogService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SCW
 * @date 2022/7/27 14:08
 * 埋点统计
 */
@RestController
@RequestMapping("/devicelog")
public class DevicelogController {

    @Autowired
    DevicelogService devicelogService;

    @PostMapping("/add")
    public void add(@RequestBody @NonNull Devicelog devicelog) {
        devicelog.setId(0);
        devicelogService.save(devicelog);
    }


}
