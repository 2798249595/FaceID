package com.wllt.faceid.core.vo;

import lombok.Data;

/**
 * @author SCW
 * @date 2022/7/23 15:03
 */
@Data
public class StandbyVO {

    /**
     *
     */
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 1图片2视频
     */
    private Integer type;

    /**
     * 视频名
     */
    private VideoInfoVO videoInfo;

    /**
     * 图片名
     */
    private ImageInfoVO imageInfo;
}
