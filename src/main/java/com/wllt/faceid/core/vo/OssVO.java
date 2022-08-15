package com.wllt.faceid.core.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author SCW
 * @date 2022/6/23 11:01
 */
@Data
public class OssVO {

    private String SecurityToken;

    private String AccessKeyId;

    private String AccessKeySecret;

    private String callbackUrl;

    private String bucket;

    private String endpoint;
}
