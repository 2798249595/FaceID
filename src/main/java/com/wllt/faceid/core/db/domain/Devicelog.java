package com.wllt.faceid.core.db.domain;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName devicelog
 */
@Data
public class Devicelog implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     * 设备编号
     */
    private String deviceNum;

    /**
     * 1跳绳2跑酷
     */
    private Integer item;

    /**
     * 姓名
     */
    private String name;

    /**
     * 开始时间戳
     */
    private Long startTime;

    /**
     * 使用时长   秒
     */
    private Integer duration;

    /**
     * 角色
     */
    private String player;

    /**
     * 场景
     */
    private String playerScenes;

    private static final long serialVersionUID = 1L;
}
