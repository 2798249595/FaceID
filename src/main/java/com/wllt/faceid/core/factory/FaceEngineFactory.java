package com.wllt.faceid.core.factory;

import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author SCW
 * @date 2022/6/14 16:55
 * FaceEngine对象池
 */
public class FaceEngineFactory extends BasePooledObjectFactory<FaceEngine> {

    private String AppId;
    private String SdkKey;
    private String SdkPath;
    private EngineConfiguration engineConfiguration;


    /** 初始化属性
     * @param appId
     * @param sdkKey
     * @param sdkPath
     * @param engineConfiguration
     */
    public FaceEngineFactory (String appId,String sdkKey ,String sdkPath, EngineConfiguration engineConfiguration){
        this.AppId=appId;
        this.SdkKey=sdkKey;
        this.SdkPath=sdkPath;
        this.engineConfiguration=engineConfiguration;
    }

    /**
     * 用于给池创建对象
     * @return
     */
    @Override
    public FaceEngine create(){
        FaceEngine faceEngine=new FaceEngine(SdkPath);
        int i = faceEngine.activeOnline(AppId, SdkKey);
        //初始化引擎
        int init = faceEngine.init(engineConfiguration);
        return faceEngine;
    }

    /**
     *包装一下对象方便池处理
     * @param faceEngine
     * @return
     */
    @Override
    public PooledObject<FaceEngine> wrap(FaceEngine faceEngine) {
        return new DefaultPooledObject<>(faceEngine);
    }


    @Override
    public void destroyObject(PooledObject<FaceEngine> p, DestroyMode destroyMode){
        FaceEngine faceEngine = p.getObject();
        //卸载引擎
        faceEngine.unInit();
    }
}
