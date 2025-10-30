package com.upely.secretconfig.domain.db;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class UserDO extends SuperDbDO {
    
    public static final String DB_FIELD_NAME = "name";
    public static final String DB_FIELD_PASSWORD = "password";

    private String name;
    private String password;
}