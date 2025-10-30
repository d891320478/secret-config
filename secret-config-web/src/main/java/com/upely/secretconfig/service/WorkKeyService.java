package com.upely.secretconfig.service;

import com.upely.secretconfig.domain.db.WorkKeyDO;

public interface WorkKeyService {

    WorkKeyDO getByAppNameAndConfigType(String appName, int configType);
}