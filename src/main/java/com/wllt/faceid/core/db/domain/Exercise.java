package com.wllt.faceid.core.db.domain;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName exercise
 */
@Data
public class Exercise implements Serializable {
    /**
     * 成绩
     */
    private Integer id;

    /**
     * 设备号
     */
    private String deviceNum;

    /**
     * 客户id
     */
    private Integer userID;

    /**
     * 人员id
     */
    private String memberID;

    /**
     * 锻炼开始时间
     */
    private String exerciseStart;

    /**
     * 锻炼结束时间
     */
    private String exerciseEnd;

    /**
     * 锻炼数据
     */
    private String exerciseData;

    /**
     * 1跳绳2跑酷
     */
    private Integer type;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 上传到sso成功后返回的id
     */
    private Integer videoid;

    private static final long serialVersionUID = 1L;
}
