package com.upely.secretconfig.listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.EnumerablePropertySource;

import com.upely.secretconfig.common.domain.RedisConfigDTO;
import com.upely.secretconfig.util.DecryptConfigUtil;

/**
 * @author dht31261
 * @date 2025年10月12日 20:26:14
 */
public class RedisConfSource extends EnumerablePropertySource<Map<String, Object>> {

    public static final String NAME = "redis.config";

    public RedisConfSource() {
        super(NAME, new HashMap<>());
    }

    public void init() throws IOException {
        RedisConfigDTO config = DecryptConfigUtil.redisConfig();
        Map<String, Object> map = new HashMap<>();
        if (!config.getAddress().contains(",")) {
            String[] address = config.getAddress().split(":");
            map.put("spring.data.redis.host", address[0]);
            map.put("spring.data.redis.port", address[1]);
        } else {
            map.put("spring.data.redis.cluster.nodes", config.getAddress());
            map.put("spring.data.redis.cluster.max-redirects", 3);
            map.put("spring.data.redis.lettuce.cluster.refresh.adaptive", "true");
            map.put("spring.data.redis.lettuce.cluster.refresh.period", 2000);
        }
        map.put("spring.data.redis.lettuce.pool.max-active", 8);
        map.put("spring.data.redis.lettuce.pool.max-wait", "-1ms");
        map.put("spring.data.redis.lettuce.pool.max-idle", 8);
        map.put("spring.data.redis.lettuce.pool.min-idle", 0);
        map.put("spring.data.redis.password", config.getPass());
        source.putAll(map);
    }

    @Override
    public String[] getPropertyNames() {
        return source.keySet().toArray(new String[0]);
    }

    @Override
    public Object getProperty(String name) {
        return source.get(name);
    }
}