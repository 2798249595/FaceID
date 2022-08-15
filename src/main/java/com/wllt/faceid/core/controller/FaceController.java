package com.wllt.faceid.core.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpDownloader;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.wllt.faceid.core.db.domain.User;
import com.wllt.faceid.core.db.service.UserService;
import com.wllt.faceid.core.service.FaceEngineFactoryService;
import com.wllt.faceid.core.utils.SaResult;
import com.wllt.faceid.core.vo.AcceptUserVO1;
import com.wllt.faceid.core.vo.AcceptUserVO2;
import com.wllt.faceid.core.vo.UserVO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author SCW
 * @date 2022/6/15 11:46
 */
@RestController
@RequestMapping("/face")
@Transactional
@Slf4j
public class FaceController {

    @Autowired
    FaceEngineFactoryService factoryService;

    @Autowired
    UserService userService;

    //人脸临时目录
    @Value("${config.resource-path}")
    String FacePath;

    private static ExecutorService service = Executors.newFixedThreadPool(5000);

    /**
     * 人脸更新或插入接口
     *
     * @return
     */
    @RequestMapping(value = "/updateorinsert", method = RequestMethod.POST)
    public void UpdateORInsert(@RequestBody AcceptUserVO1 acceptUserVo) {
            User user = userService.query().eq("uid", acceptUserVo.getUid()).one();
            byte[] bytes = HttpDownloader.downloadBytes(acceptUserVo.getUrl());
            //获取人脸特征
            ImageInfo imageInfo = ImageFactory.getRGBData(bytes);
            byte[] FaceData = factoryService.extractFaceFeature(imageInfo);
            //新增
            if (user == null) {
                User user1 = new User();
                user1.setId(0);
                user1.setName(acceptUserVo.getName());
                user1.setNumber(acceptUserVo.getNumber());
                user1.setUid(acceptUserVo.getUid());
                user1.setFace_feature(FaceData);
                userService.save(user1);
//                return SaResult.ok();
                //更改
            } else {
                userService.update()
                        .eq("id", user.getId())
                        .set("face_feature", FaceData)
                        .update();
//                return SaResult.ok();
            }
    }

    /**
     *批量下载人脸
     */
    @PostMapping(value = "/updateorinsertlist")
    public SaResult UpdateORInsertList(@RequestBody JSONObject jsonObject) throws InterruptedException {
        Object data = jsonObject.get("data");
        List<AcceptUserVO2> list = JSONUtil.parseObj(data).getBeanList("list", AcceptUserVO2.class);
        System.out.println(list.size());
        for (AcceptUserVO2 acceptUserVo:list){
            Thread.sleep(20);
            System.out.println(acceptUserVo);
            service.submit(() -> {
                User user = userService.query().eq("uid", acceptUserVo.getId()).one();
                byte[] bytes = HttpDownloader.downloadBytes(acceptUserVo.getFaceInfo().getUrl());
                //获取人脸特征
                ImageInfo imageInfo = ImageFactory.getRGBData(bytes);
                byte[] FaceData = factoryService.extractFaceFeature(imageInfo);
                //新增
                if (user == null) {
                    User user1 = new User();
                    user1.setId(0);
                    user1.setName(acceptUserVo.getName());
                    user1.setNumber(acceptUserVo.getNumber());
                    user1.setUid(acceptUserVo.getId());
                    user1.setFace_feature(FaceData);
                    userService.save(user1);
                    //更改
                } else {
                    userService.update()
                            .eq("id", user.getId())
                            .set("face_feature", FaceData)
                            .update();
                }
            });
        }
        return SaResult.ok();
    }






    /**
     * 人脸识别接口
     *
     * @param file
     * @return 相似度最高的
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public SaResult Query(@NonNull @RequestBody String file) throws ExecutionException, InterruptedException {
        log.info("人脸搜索接口被请求");
        long l = System.currentTimeMillis();
        if (file.length() < 30) {
            return SaResult.error("参数格式错误");
        }
        //截取bese64头
//        String base64File = base64Process(file);
        //解码成数组
        byte[] decode = Base64.decode(file);
        //获取图片信息
        ImageInfo imageInfo = ImageFactory.getRGBData(decode);
        if (imageInfo == null) {
            log.info("人脸格式错误");
            return SaResult.error("人脸格式错误无法解析");
        }
        //获取人脸信息
        byte[] bytes = factoryService.extractFaceFeature(imageInfo);

        if (bytes == null) {
            log.info("未识别到人脸");
            return SaResult.error("未识到人脸");
        }
        List<UserVO> userVOs = factoryService.compareFaceFeature(bytes);
        if (userVOs == null) {
            log.info("查无此人");
            return SaResult.error("查无此人");
        }
        long l1 = System.currentTimeMillis();
        log.info("查询到人 :"+ userVOs.get(0).getName()+" 耗时: "+ (l1-l));
        return SaResult.data(userVOs);
    }


    /**
     * 截取base64编码的头
     *
     * @param base64Str
     * @return
     */
    private String base64Process(String base64Str) {
        if (!StringUtils.isEmpty(base64Str)) {
            String photoBase64 = base64Str.substring(0, 30).toLowerCase();
            int indexOf = photoBase64.indexOf("base64,");
            if (indexOf > 0) {
                base64Str = base64Str.substring(indexOf + 7);
            }

            return base64Str;
        } else {
            return "";
        }
    }


}