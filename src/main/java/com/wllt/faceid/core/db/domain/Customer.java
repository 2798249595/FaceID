package com.wllt.faceid.core.db.domain;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName customer
 */
@Data
public class Customer implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     * 客户id
     */
    private Integer cid;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户简介
     */
    private String aliasname;

    /**
     * 人脸上传地址
     */
    private String privatefaceurl;

    /**
     * 数据上传地址
     */
    private String privatedataurl;

    /**
     * 数据批量上传地址
     */
    private String privatedatabatchurl;

    /**
     * 私有化密钥
     */
    private String privatekey;

    /**
     *
     */
    private String privatebindurl;

    /**
     * 到期时间
     */
    private Long endtime;

    /**
     * 是否可以匿名使⽤1可以0不⾏
     */
    private String isanonymous;
    private static final long serialVersionUID = 1L;
}
