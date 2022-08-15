package com.wllt.faceid.core.db.domain;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName record
 */
@Data
public class Record implements Serializable {
    /**
     * 记录用户的使用次数
     */
    private Integer id;

    /**
     * 场景
     */
    private String scene;

    /**
     * 角色
     */
    private String role;

    private static final long serialVersionUID = 1L;
}
