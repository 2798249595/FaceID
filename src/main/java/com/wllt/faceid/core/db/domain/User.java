package com.wllt.faceid.core.db.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @TableName user
 */
@Data
public class User implements Serializable {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 名字
     */
    private String name;

    /**
     * 编号
     */
    private Integer number;

    /**
     * 人脸特征
     */
    private byte[] face_feature;

}
