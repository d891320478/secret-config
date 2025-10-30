package com.upely.secretconfig.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.upely.secretconfig.domain.db.UserDO;
import com.upely.secretconfig.mapper.UserMapper;
import com.upely.secretconfig.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Override
    public Boolean hasUser() {
        return getBaseMapper().selectCount(null) > 0;
    }
}