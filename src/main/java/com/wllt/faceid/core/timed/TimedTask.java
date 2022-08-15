package com.wllt.faceid.core.timed;

import cn.hutool.core.lang.Console;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.Callback;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.google.common.collect.Lists;
import com.wllt.faceid.core.db.domain.Customer;
import com.wllt.faceid.core.db.domain.Devicelog;
import com.wllt.faceid.core.db.domain.Exercise;
import com.wllt.faceid.core.db.domain.Standby;
import com.wllt.faceid.core.db.service.*;
import com.wllt.faceid.core.vo.OssVO;
import com.wllt.faceid.core.vo.StandbyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/**
 * @author SCW
 * @date 2022/6/21 11:14
 */
@Component
@Slf4j
public class TimedTask {

    @Autowired
    CustomerService customerService;

    @Autowired
    StandbyService standbyService;

    @Autowired
    ExerciseService exerciseService;

    @Autowired
    DevicelogService devicelogService;

    @Autowired
    MainboardService mainboardService;
    /**
     * 成绩上传的地址
     */
    @Value("${config.request-url}" + "/home/device/upload-data")
    String UploadUrl;

    /**
     * 获取待机视频和动画
     */
    @Value("${config.request-url}" + "/home/device/standby")
    String StandbyUrl;

    /**
     * 锻炼视频路径
     */
    @Value("${config.path-mp4}")
    String PathMP4;

    @Value("${config.resource-path}")
    String ResourcePath;

    /**
     * 密钥
     */
    @Value("${config.key}")
    String Key;

    /**
     * 获取阿里oss密钥地址
     */
    @Value("${config.request-url}" + "/home/common/get-oss-sign")
    String StsUrl;

    /**
     * 批量上传地址
     */
    @Value("${config.request-url}" + "/home/device/upload-data-list")
    String UrlUploadList;

    /**
     * 埋点统计地址
     */
    @Value("${config.request-url}" + "/home/device/upload-device-log")
    String UrlDeviceLog;


    /**
     * 批量上传锻炼数据
     */
    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void Task1() {
        //判断是否有网
        try {
            HttpRequest.post(UploadUrl)
                    .timeout(1000 * 3)
                    .execute().body();
        } catch (Exception e) {
            return;
        }

        //获取没用锻炼数据的信息
        List<Exercise> exercises = exerciseService
                .query()
                .isNull("filename")
                .list();

        //获取已经上传到oos成功的数据
        List<Exercise> exercises1 = exerciseService
                .query()
                .isNotNull("videoid")
                .list();
        exercises.addAll(exercises1);
        List<Map> mapList = new ArrayList<>();
        //分割成1000一份
        List<List<Exercise>> ExerciseLists = Lists.partition(exercises, 1000);
        for (List<Exercise> exerciseList : ExerciseLists) {
            for (Exercise exercise : exerciseList) {
                Map<String, Object> map = new HashMap<>();
                map.put("deviceNum", exercise.getDeviceNum());
                map.put("userID", exercise.getUserID());
                map.put("memberID", exercise.getMemberID());
                map.put("exerciseStart", exercise.getExerciseStart());
                map.put("exerciseEnd", exercise.getExerciseEnd());
                map.put("exerciseData", exercise.getExerciseData());
                map.put("postType", exercise.getType());
                map.put("videoID", exercise.getFilename() == null ? 0 : exercise.getVideoid());
                mapList.add(map);
            }
            Customer customer = customerService.getById(1);
            Map<String, Object> map = new HashMap<>();
            //设备号
            map.put("deviceNum", getDeviceNum());
            //用户id
            map.put("userID", customer.getCid());
            map.put("exerciseData", JSONUtil.toJsonStr(mapList));

            //判断是否有私有地址
            if (customer.getPrivatekey() != null) {
                //锻炼数据上传
                HttpRequest
                        .post(customer.getPrivatedatabatchurl())
                        .form(Signature(map, customer.getPrivatekey()))
                        .timeout(1000 * 4)
                        .execute()
                        .body();
            }

            //锻炼数据上传
            String body = HttpRequest
                    .post(UrlUploadList)
                    .form(Signature(map, this.Key))
                    .timeout(1000 * 4)
                    .execute()
                    .body();
            Integer errorCode = (Integer) JSONUtil.parseObj(body).get("errorCode");
            if (errorCode == 0) {
                exerciseService.removeBatchByIds(exerciseList);
            }

        }
    }

    /**
     * 获取设备待机视频和封面
     */
    @Scheduled(fixedRate = 1000 * 60*60)
    public void Task2() {
        //判断网络是否通畅
        try {
            HttpRequest.get(StandbyUrl)
                    .timeout(1000 * 10)
                    .execute();
        } catch (Exception e) {
            return;
        }
        //获取设备号
        Customer customer = customerService.getById(1);
        Map<String, Object> map = new HashMap<>();
        //设备号 mac地址
        map.put("deviceNum", getDeviceNum());
        //客户id
        map.put("userID", customer.getCid());
        HttpResponse response = HttpRequest
                .post(StandbyUrl)
                .form(Signature(map, this.Key))
                .timeout(1000 * 5)
                .execute();
        JSONObject jsonObject = new JSONObject(response.body());
        JSONObject jsonObject1 = new JSONObject(jsonObject.get("data"));
        List<StandbyVO> list = jsonObject1.getBeanList("list", StandbyVO.class);
        if (list == null) {
            return;
        }
        List<Standby> list1 = standbyService.list();
        for (StandbyVO standbyVO : list) {
            //图片
            if (standbyVO.getType() == 1) {
                String ImageUrl = standbyVO.getImageInfo().getUrl();
                String ImageName = IdUtil.simpleUUID() + ImageUrl.substring(ImageUrl.lastIndexOf("."));
                //下载图片
                File file = new File(ResourcePath, ImageName);
                HttpUtil.downloadFile(ImageUrl, file);
                //保存到数据库
                Standby standby = new Standby();
                standby.setId(0);
                standby.setTitle(standbyVO.getTitle());
                standby.setType(standbyVO.getType());
                standby.setImage(file.getPath());
                standby.setImagewide(standbyVO.getImageInfo().getWidth());
                standby.setImagehign(standbyVO.getImageInfo().getHeight());
                standby.setVideo(null);
                standbyService.save(standby);
            }

            //视频
            if (standbyVO.getType() == 2) {
                String VideoUrl = standbyVO.getVideoInfo().getUrl();
                String VideoName = IdUtil.simpleUUID() + VideoUrl.substring(VideoUrl.lastIndexOf("."));
                //下载视频
                File FileVideo = new File(ResourcePath, VideoName);
                HttpUtil.downloadFile(VideoUrl, new File(ResourcePath, VideoName));

                String ImageUrl = standbyVO.getImageInfo().getUrl();
                String ImageName = IdUtil.simpleUUID() + ImageUrl.substring(ImageUrl.lastIndexOf("."));
                //下载图片
                File FileImage = new File(ResourcePath, ImageName);
                HttpUtil.downloadFile(ImageUrl, FileImage);
                //保存到数据库
                Standby standby = new Standby();
                standby.setId(0);
                standby.setTitle(standbyVO.getTitle());
                standby.setType(standbyVO.getType());
                standby.setImage(FileImage.getPath());
                standby.setImagewide(standbyVO.getImageInfo().getWidth());
                standby.setImagehign(standbyVO.getImageInfo().getHeight());
                standby.setVideo(FileVideo.getPath());
                standbyService.save(standby);
            }
        }

        //删除旧的的待机动画
        for (Standby standby : list1) {
            //删除库中的数据
            standbyService.removeById(standby.getId());
            if (standby.getType() == 1) {
                //删除图片
                File FileImage = new File(standby.getImage());
                FileImage.delete();
            }
            if (standby.getType() == 2) {
                //删除图片
                File FileImage = new File(standby.getImage());
                FileImage.delete();
                //删除视频
                File FileMP4 = new File(standby.getVideo());
                FileMP4.delete();
            }
        }
    }

    /**
     * 向oos发送数据
     */
    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void Task3() throws Exception {
        //判断是否有网
        try {
            HttpRequest.post(UploadUrl)
                    .timeout(1000 * 3)
                    .execute().body();
        } catch (Exception e) {
            return;
        }
        //获取所有有锻炼数据没上传到oos的数据
        List<Exercise> list = exerciseService
                .query()
                .isNull("videoid")
                .isNotNull("filename")
                .list();
        //上传到oos
        for (Exercise exercise : list) {
            File file = new File(PathMP4, exercise.getFilename());
            //判断文件是否存在
            if (file.exists()) {
                //上传到阿里云成功后获取的资源id
                Integer VideoId = OssUpload(file);
                exerciseService
                        .update()
                        .eq("id", exercise.getId())
                        .set("videoid", VideoId)
                        .update();
                //上传成功后删除本地文件
                file.delete();
            }
        }
    }

    /**
     * 上传设备操作记录
     */
    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void Task4() {
        //判断是否有网
        try {
            HttpRequest.post(UploadUrl)
                    .timeout(1000 * 3)
                    .execute().body();
        } catch (Exception e) {
            return;
        }

        List<Devicelog> list = devicelogService.list();

        for (Devicelog devicelog : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("deviceNum", devicelog.getDeviceNum());
            map.put("item", devicelog.getItem());
            map.put("name", devicelog.getName());
            map.put("startTime", devicelog.getStartTime());
            map.put("duration", devicelog.getDuration());
            map.put("player", devicelog.getPlayer());
            map.put("playerScenes", devicelog.getPlayerScenes());
            String body = HttpRequest
                    .post(UrlDeviceLog)
                    .timeout(1000 * 2)
                    .form(Signature(map, this.Key))
                    .execute()
                    .body();
            //上传成功后删除
            Integer errorCode = (Integer) JSONUtil.parseObj(body).get("errorCode");
            if (errorCode == 0) {
                devicelogService.removeById(devicelog.getId());
            }
        }


    }


    public Integer OssUpload(File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("文件不存在");
        }
        OssVO ossVO = Sts(StsUrl);
        String objectName = "public/uploads/video/" + file.getName();
        // 创建OSSClient实例。
        OSS ossClient = null;
        FileInputStream inputStream = null;
        try {
            ossClient = new OSSClientBuilder().build(ossVO.getEndpoint(), ossVO.getAccessKeyId(), ossVO.getAccessKeySecret(), ossVO.getSecurityToken());
            inputStream = new FileInputStream(file);
            // 填写字符串。
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossVO.getBucket(), objectName, inputStream);
            //设置回调信息
            Callback callback = new Callback();
            callback.setCallbackUrl(ossVO.getCallbackUrl());
            callback.setCallbackBody("ucket=${bucket}&object=${object}&size=${size}&mimeType=${mimeType}\n" +
                    "&imageHeight=${imageInfo.height}&imageWidth=${imageInfo.width}&imag\n" +
                    "eType=${imageInfo.format}");
            callback.setCalbackBodyType(Callback.CalbackBodyType.URL);
            putObjectRequest.setCallback(callback);
            //开始上传
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            //获取返回的文件id
            byte[] bytes = result.getResponse().getContent().readAllBytes();
            Integer id = (Integer) JSONUtil.parseObj(new String(bytes)).get("id");
            //关流
            result.getResponse().getContent().close();
            return id;
        } catch (OSSException oe) {
            oe.printStackTrace();
        } catch (ClientException ce) {
            ce.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return null;
    }


    /**
     * 获取阿里云oss密钥
     *
     * @param url
     * @return
     */
    public OssVO Sts(String url) {
        String body = HttpRequest.post(url)
                .form(Signature())
                .execute()
                .body();
        Console.log(body);
        JSONObject json = JSONUtil.parseObj(body);
        Object data = json.get("data");
        JSONObject info = JSONUtil.parseObj(data);
        return info.getBean("info", OssVO.class);
    }


    /**
     * 无请求参数签名
     */
    public Map<String, Object> Signature() {
        Integer salt = RandomUtil.randomInt(1000, 9999);
        Map<String, Object> map = new HashMap<>();
        //盐
        map.put("salt", salt);
        //秒级时间戳
        map.put("timestamp", (System.currentTimeMillis() / 1000));
        String sign = SecureUtil.signParamsMd5(map, Key);
        map.put("sign", sign);
        return map;
    }


    /**
     * 有请求参数签名
     *
     * @param map
     * @param key 密钥
     * @return
     */
    public Map<String, Object> Signature(Map<String, Object> map, String key) {
        Integer salt = RandomUtil.randomInt(1000, 9999);
        //盐
        map.put("salt", salt);
        //秒级时间戳
        map.put("timestamp", (System.currentTimeMillis() / 1000));
        String sign = SecureUtil.signParamsMd5(map, key);
        map.put("sign", sign);
        return map;
    }



    /**
     * 获取设备标识
     *
     * @return
     * @throws IOException
     */
    public String getDeviceNum() {
        return mainboardService.getById(1).getMainboard();
    }

}
