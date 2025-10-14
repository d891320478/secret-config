-- 触发器函数，更新 gmt_modified 字段
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.gmt_modified = now();
   RETURN NEW;
END;
$$ language 'plpgsql';
-- 创建schema
CREATE SCHEMA usr_secret_config;
-- 密钥表
CREATE TABLE IF NOT EXISTS usr_secret_config.work_key (
    id BIGSERIAL PRIMARY KEY,
    app_name VARCHAR(64) NOT NULL,
    work_key VARCHAR(256) NOT NULL,
    config_type INT NOT NULL,
    gmt_create TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gmt_modified TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON COLUMN usr_secret_config.work_key.config_type IS '1数据库 2redis 3应用';
CREATE UNIQUE INDEX work_key_uk_appname_configtype ON usr_secret_config.work_key USING BTREE (app_name, config_type);
CREATE TRIGGER update_usr_secret_config_work_key_gmt_modified BEFORE UPDATE ON usr_secret_config.work_key FOR EACH ROW EXECUTE FUNCTION update_modified_column();
-- 加密配置表
CREATE TABLE IF NOT EXISTS usr_secret_config.secret_config (
	id BIGSERIAL PRIMARY KEY,
    app_name VARCHAR(64) NOT NULL,
    config_key VARCHAR(256) NOT NULL,
    config_value TEXT NOT NULL,
    config_type INT NOT NULL,
    gmt_create TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gmt_modified TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON COLUMN usr_secret_config.secret_config.config_type IS '1数据库 2redis 3应用';
CREATE UNIQUE INDEX secret_config_uk_appname_configkey_configtype ON usr_secret_config.secret_config USING BTREE (app_name, config_key, config_type);
CREATE TRIGGER update_usr_secret_config_secret_config_gmt_modified BEFORE UPDATE ON usr_secret_config.secret_config FOR EACH ROW EXECUTE FUNCTION update_modified_column();