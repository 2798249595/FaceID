package com.wllt.faceid.core.controller;

import com.wllt.faceid.core.db.domain.Equipment;
import com.wllt.faceid.core.db.service.EquipmentService;
import com.wllt.faceid.core.utils.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SCW
 * @date 2022/6/20 18:01
 * 设备状态
 */
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    EquipmentService equipmentService;


    /**
     * 查询
     * @return
     */
    @RequestMapping("/query")
    public SaResult Query() {
        Equipment equipment = equipmentService.query().eq("id",1).one();
        return SaResult.data(equipment);
    }

    /**
     *更新或插入
     * @param equipment
     * @return
     */
    @RequestMapping("/updateorinsert")
    public SaResult UpdateORInsert(@RequestBody Equipment equipment) {
        Equipment equipment1 = equipmentService.getById(1);
        equipment.setId(1);
        if (equipment1 == null) {
            equipmentService.save(equipment);
        }else {
            equipmentService.updateById(equipment);
        }
        return SaResult.ok();
    }

}
