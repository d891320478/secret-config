package com.upely.secretconfig.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.upely.secretconfig.common.domain.DbConfigDTO;
import com.upely.secretconfig.common.enums.DbDriverEnum;
import com.upely.secretconfig.util.DecryptConfigUtil;

/**
 * @author dht31261
 * @date 2025年10月12日 20:12:13
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan(basePackages = { "com.upely.secretconfig.mapper" })
public class DbConfiguration {

    @Bean(initMethod = "init")
    DruidDataSource dataSource() throws IOException {
        DbConfigDTO config = DecryptConfigUtil.dbConfig();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(config.jdbcUrl());
        dataSource.setUsername(config.getUser());
        dataSource.setPassword(config.getPass());
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(50);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        dataSource.setDriverClassName(DbDriverEnum.getByDriver(config.getDriver()).getDriverClass());
        return dataSource;
    }

    @Bean
    DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor() throws IOException {
        DbConfigDTO config = DecryptConfigUtil.dbConfig();

        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            return config.getSchema() + "." + tableName;
        });
        return dynamicTableNameInnerInterceptor;
    }

    @Bean
    MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean() throws IOException, SQLException {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource());
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIdType(IdType.AUTO);
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDbConfig(dbConfig);
        bean.setGlobalConfig(globalConfig);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:sqlmap/*.xml"));
        MybatisPlusInterceptor plugin = new MybatisPlusInterceptor();
        plugin.setInterceptors(Arrays.asList(dynamicTableNameInnerInterceptor()));
        bean.setPlugins(plugin);
        return bean;
    }
}