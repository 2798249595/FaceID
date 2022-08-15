package com.wllt.faceid.core.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wllt.faceid.core.db.domain.Customer;
import com.wllt.faceid.core.db.service.CustomerService;
import com.wllt.faceid.core.db.mapper.CustomerMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【customer】的数据库操作Service实现
* @createDate 2022-06-29 15:15:58
*/
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer>
    implements CustomerService{

}




