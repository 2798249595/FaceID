package com.wllt.faceid.core.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.wllt.faceid.core.vo.Face;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SCW
 * @date 2022/10/27 9:48
 * 获取人脸3d模型
 */
@Component
public class FaceThree {

    @Value("${config.face-three.key}")
    private String key;
    @Value("${config.face-three.secret}")
    private String secret;
    @Value("${config.face-three.url}")
    private String url;


    /**
     * 获取人脸3d模型数据
     *
     * @param img base64编码的脸
     */
    public Face getThree(String img) {
        Map<String, Object> map = new HashMap<>(5);
        map.put("api_key", key);
        map.put("api_secret", secret);
        map.put("image_base64_1", img);
//        map.put("image_base64_2","");
        map.put("texture", 1);
        map.put("mtl", 1);
        String body = HttpRequest.post(url)
                .contentType("multipart/form-data")
                .form(map)
                .execute()
                .body();
        return JSONUtil.toBean(JSONUtil.parseObj(body), Face.class);
    }

    /**
     * 多张图片获取人脸3d模型数据
     *
     * @param list base64编码的脸
     */
    public Face getThree(List<String> list) {
        Map<String, Object> map = new HashMap<>(5);
        map.put("api_key", key);
        map.put("api_secret", secret);
/*        map.put("image_base64_1", list.get(0));
        map.put("image_base64_2", list.get(1));
        map.put("image_base64_3", list.get(2));*/
        map.put("texture", 1);
        map.put("mtl", 1);
        int i = 1;
        for (String s : list) {
            map.put("image_base64_" + i, s);
            i += 1;
        }
        String body = HttpRequest.post(url)
                .contentType("multipart/form-data")
                .form(map)
                .execute()
                .body();
        return JSONUtil.toBean(JSONUtil.parseObj(body), Face.class);
    }
}
