package com.upely.secretconfig.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.upely.secretconfig.domain.db.WorkKeyDO;
import com.upely.secretconfig.mapper.WorkKeyMapper;
import com.upely.secretconfig.service.WorkKeyService;

@Service
public class WorkKeyServiceImpl extends ServiceImpl<WorkKeyMapper, WorkKeyDO> implements WorkKeyService {

    @Override
    public WorkKeyDO getByAppNameAndConfigType(String appName, int configType) {
        return getBaseMapper().selectOne(Wrappers.lambdaQuery(WorkKeyDO.class).eq(WorkKeyDO::getAppName, appName)
            .eq(WorkKeyDO::getConfigType, configType));
    }
}