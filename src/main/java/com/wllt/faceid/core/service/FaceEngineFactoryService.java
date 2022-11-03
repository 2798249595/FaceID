package com.wllt.faceid.core.service;

import cn.hutool.core.util.RandomUtil;
import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.toolkit.ImageInfo;
import com.google.common.collect.Lists;
import com.wllt.faceid.core.db.domain.User;
import com.wllt.faceid.core.db.service.UserService;
import com.wllt.faceid.core.factory.FaceEngineFactory;
import com.wllt.faceid.core.vo.UserVO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author SCW
 * @date 2022/6/14 17:18
 */
@Slf4j
@Component
@Transactional
public class FaceEngineFactoryService {

    @Value("${config.arcface-sdk.app-id}")
    private String AppId;
    @Value("${config.arcface-sdk.sdk-key}")
    private String SdkKey;
    @Value("${config.path-sdk}")
    private String SdkPath;


    private Integer threadPoolSize = 50;

    //相似度阈值
    private float passRate = 0.8F;

    private ExecutorService executorService;

    private GenericObjectPool<FaceEngine> genericObjectPool;

    @Autowired
    UserService userService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(threadPoolSize);
        config.setMaxTotal(threadPoolSize);
        config.setMinIdle(threadPoolSize);
        config.setLifo(false);

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(1);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);

        FaceEngineFactory factory = new FaceEngineFactory(AppId, SdkKey, SdkPath, engineConfiguration);
        //底层算法对象池
        genericObjectPool = new GenericObjectPool(factory, config);
        log.info("人脸识别初始化完成");
    }

    /**
     * 获取人脸特征
     *
     * @param imageInfo 图片
     * @return 人脸信息 null属于没有识别到人脸
     */
    public byte[] extractFaceFeature(@NonNull ImageInfo imageInfo) {
        FaceEngine faceEngine = null;
        try {
            faceEngine = genericObjectPool.borrowObject();
            //人脸检测
            List<FaceInfo> faceInfoList = new ArrayList<>();
            faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            if (!faceInfoList.isEmpty()) {
                //获取人脸数据
                FaceFeature feature = new FaceFeature();
                faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), feature);
                return feature.getFeatureData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (faceEngine != null) {
                genericObjectPool.returnObject(faceEngine);
            }
        }
        return null;
    }

    /**
     * 获取两人脸相似度
     * @return
     */
    public float FaceSimilarity(byte [] face1,byte [] face2){
        FaceEngine faceEngine=null;
        try {
            faceEngine = genericObjectPool.borrowObject();

            FaceFeature FaceFeature1 = new FaceFeature();
            FaceFeature1.setFeatureData(face1);

            FaceFeature FaceFeature2 = new FaceFeature();
            FaceFeature2.setFeatureData(face2);

            FaceSimilar faceSimilar = new FaceSimilar();
            faceEngine.compareFaceFeature(FaceFeature1,FaceFeature2,faceSimilar);
            return faceSimilar.getScore();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (faceEngine != null) {
                genericObjectPool.returnObject(faceEngine);
            }
        }
        return 0;
    }



    /**
     * 人脸对比获取相似度最高的
     *
     * @param faceFeature
     */
    public List<UserVO> compareFaceFeature(byte[] faceFeature) throws ExecutionException, InterruptedException {
        //识别到的人脸列表
        List<UserVO> resultFaceInfoList = Lists.newLinkedList();
        //要对比的人脸
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(faceFeature);

        List<User> userList = userService.query().list();
        //数据分割
        List<List<User>> UserLists = Lists.partition(userList, 1000);

        CompletionService<List<UserVO>> completionService = new ExecutorCompletionService(executorService);
        //提交任务
        for (List<User> list : UserLists) {
            completionService.submit(new CompareFaceTask(list, targetFaceFeature));
        }
        //获取结果
        for (int i = 0; i < UserLists.size(); i++) {
            List<UserVO> userVOS = completionService.take().get();
            if (userVOS != null) {
                resultFaceInfoList.addAll(userVOS);
            }
        }

        if (resultFaceInfoList.size() == 0) {
            return null;
        }
//        resultFaceInfoList.sort((h1, h2) -> h2.getSimilarity().compareTo(h1.getSimilarity()));
        return resultFaceInfoList;
    }


    //处理任务
    private class CompareFaceTask implements Callable<List<UserVO>> {

        private List<User> userList;

        private FaceFeature feature;

        public CompareFaceTask(List<User> userList, FaceFeature feature) {
            this.userList = userList;
            this.feature = feature;
        }

        @Override
        public List<UserVO> call() {
            FaceEngine faceEngine = null;
            List<UserVO> list = Lists.newArrayList();
            try {
                faceEngine = genericObjectPool.borrowObject();
                for (User user : userList) {
                    //相似度对比
                    FaceFeature sourceFaceFeature = new FaceFeature();
                    sourceFaceFeature.setFeatureData(user.getFace_feature());
                    FaceSimilar faceSimilar = new FaceSimilar();
                    faceEngine.compareFaceFeature(feature, sourceFaceFeature, faceSimilar);
                    //相似度大于阈值加入比较队列
                    if (faceSimilar.getScore() > passRate) {
                        UserVO userVO = new UserVO()
                                .setUid(user.getUid())
                                .setName(user.getName())
                                .setNumber(user.getNumber())
                                .setSimilarity(faceSimilar.getScore());
                        list.add(userVO);
                    }
                }
                //集合不为空就返回
                if (!list.isEmpty()) {
                    log.debug(list.toString());
                    return list;
                }
            } catch (Exception e) {
                log.error("人脸比对任务队列异常");
                e.printStackTrace();
            } finally {
                if (faceEngine != null) {
                    genericObjectPool.returnObject(faceEngine);
                }
            }
            log.debug("返回null");
            return null;
        }

    }


}
