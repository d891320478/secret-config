package com.usr.secretconfig.domain.db;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * @author dht31261
 * @date 2025年10月12日 20:19:34
 */
@Data
public class SuperDbDO {

    public static final String DB_FIELD_ID = "id";
    public static final String DB_FIELD_GMT_CREATE = "gmt_create";
    public static final String DB_FIELD_GMT_MODIFIED = "gmt_modified";

    @TableId(type = IdType.AUTO)
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
}