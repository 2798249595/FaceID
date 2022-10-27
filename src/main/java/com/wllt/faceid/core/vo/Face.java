package com.wllt.faceid.core.vo;

import lombok.Data;

/**
 * @author SCW
 * @date 2022/10/25 15:37
 */
@Data
public class Face {
    //用于区分每一次请求的唯一的字符串
    String request_id;
    //obj文件，人脸3D模型文件
    String obj_file;
    //展开的纹理图，jpg格式。base64 编码的二进制图片数据。
    String texture_img;
    //mtl文件，材质库文件
    String mtl_file;
    //整个请求所花费的时间，单位为毫秒。
    Integer time_used;
    //当请求失败时才会返回此字符串，具体返回内容见后续错误信息章节。否则此字段不存在。
    String error_message;
}
