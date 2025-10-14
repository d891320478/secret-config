-- 密钥表
CREATE TABLE IF NOT EXISTS work_key (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_name VARCHAR(64) NOT NULL,
    work_key VARCHAR(256) NOT NULL,
    config_type INT NOT NULL COMMENT '1数据库 2redis 3应用',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_appname_configtype (app_name, config_type)
)ENGINE=InnoDB CHARSET=utf8mb4;
-- 加密配置表
CREATE TABLE IF NOT EXISTS secret_config (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_name VARCHAR(64) NOT NULL,
    config_key VARCHAR(256) NOT NULL,
    config_value TEXT NOT NULL,
    config_type INT NOT NULL COMMENT '1数据库 2redis 3应用',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_appname_configkey_configtype (app_name, config_key, config_type)
)ENGINE=InnoDB CHARSET=utf8mb4;