package com.wllt.faceid.core.controller;

import com.wllt.faceid.core.db.domain.Customer;
import com.wllt.faceid.core.db.service.CustomerService;
import com.wllt.faceid.core.utils.SaResult;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SCW
 * @date 2022/6/20 14:14
 * 客户的,增,查,改,接口
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    /**
     * 查询客户信息
     *
     * @return
     */
    @RequestMapping("/query")
    public SaResult Query() {
        Customer customer = customerService.getById(1);
        return SaResult.data(customer);
    }


    /**
     * 客户更新或插入接口
     *
     * @param customer 客户实体类
     * @return
     */
    @RequestMapping("/insertorupdate")
    public SaResult InsertORUpdate(@NonNull @RequestBody Customer customer) {
        System.out.println(customer);
        Customer customer1 = customerService.getById(1);
        customer.setId(1);
        if (customer1 == null) {
            customerService.save(customer);
        } else {
            customerService.updateById(customer);
        }
        return SaResult.ok();
    }


}
