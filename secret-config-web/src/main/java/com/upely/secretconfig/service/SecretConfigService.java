package com.upely.secretconfig.service;

import java.util.List;

import com.upely.secretconfig.domain.db.SecretConfigDO;

public interface SecretConfigService {

    List<SecretConfigDO> selectListByAppNameAndConfigType(String appName, int configType);
}