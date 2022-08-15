package com.wllt.faceid.core.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wllt.faceid.core.db.domain.User;
import com.wllt.faceid.core.db.service.UserService;
import com.wllt.faceid.core.db.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user】的数据库操作Service实现
* @createDate 2022-06-29 15:15:58
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




