package com.upely.secretconfig.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.upely.secretconfig.domain.db.SecretConfigDO;
import com.upely.secretconfig.mapper.SecretConfigMapper;
import com.upely.secretconfig.service.SecretConfigService;

@Service
public class SecretConfigServiceImpl extends ServiceImpl<SecretConfigMapper, SecretConfigDO>
    implements SecretConfigService {

    @Override
    public List<SecretConfigDO> selectListByAppNameAndConfigType(String appName, int configType) {
        return getBaseMapper().selectList(Wrappers.lambdaQuery(SecretConfigDO.class)
            .eq(SecretConfigDO::getAppName, appName).eq(SecretConfigDO::getConfigType, configType));
    }
}