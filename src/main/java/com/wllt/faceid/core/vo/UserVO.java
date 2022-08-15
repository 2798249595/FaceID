package com.wllt.faceid.core.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author SCW
 * @date 2022/6/16 16:13
 */
@Data
@Accessors(chain = true)
public class UserVO {

    private Integer uid;
    private String name;
    private Integer number;
    //相似度
    private Float similarity;





}
