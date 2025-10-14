package com.upely.secretconfig.listener;

import java.io.IOException;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nonnull;

/**
 * @author dht31261
 * @date 2025年10月12日 20:26:19
 */
@Component
public class RedisConfListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(@Nonnull ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ConfigurableEnvironment env = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
            MutablePropertySources pss = env.getPropertySources();
            if (!pss.contains(RedisConfSource.NAME)) {
                RedisConfSource rps = new RedisConfSource();
                try {
                    rps.init();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pss.addFirst(rps);
            }
        }
    }
}