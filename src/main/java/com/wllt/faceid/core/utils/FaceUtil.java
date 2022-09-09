package com.wllt.faceid.core.utils;

import com.wllt.faceid.core.vo.FaceVo;
import org.bytedeco.javacpp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * @author SCW
 * @date 2022/9/9 11:56
 * 图片处理
 */
@Component
public class FaceUtil {

    private opencv_objdetect.CascadeClassifier classifier;

    @Value("${config.path-sdk}")
    String path;

    @PostConstruct
    public void init() {
        classifier = new opencv_objdetect.CascadeClassifier(path+"\\haarcascade_frontalface_default.xml");
    }

    /**
     * 获取人脸位置
     *
     * @param Path 图片路径
     * @return
     */
    public FaceVo place(String Path) {
        opencv_core.Mat inMat = opencv_imgcodecs.imread(Path);
        opencv_core.Mat mat1 = new opencv_core.Mat();
        opencv_core.Mat mat2 = new opencv_core.Mat();
        opencv_imgproc.cvtColor(inMat, mat1, opencv_imgproc.COLOR_BGR2GRAY);
        opencv_core.Size size = new opencv_core.Size(7, 7);
        opencv_imgproc.GaussianBlur(mat1, mat2, size, opencv_core.BORDER_DEFAULT);
        opencv_core.RectVector rectVector = new opencv_core.RectVector();
        classifier.detectMultiScale(mat2, rectVector);
        opencv_core.Rect[] Face = rectVector.get();
        if (Face == null) {
            return null;
        }
        opencv_core.Rect rect = Face[0];
        System.out.println("脸的宽" + rect.width() + " 脸的高" + rect.height());
/*        opencv_imgproc.rectangle(inMat,
                new opencv_core.Point(rect.x(), rect.y()),
                new opencv_core.Point(rect.x() + rect.width(), rect.y() + rect.height()),
                new opencv_core.Scalar(0, 255, 0, 0));
        opencv_imgcodecs.imwrite("D:\\FaceDetect\\11.jpg", inMat);*/
        FaceVo faceVo = new FaceVo();
        faceVo.setX(rect.x());
        faceVo.setY(rect.y());
        faceVo.setWidth(rect.width());
        faceVo.setHeight(rect.height());
        return faceVo;
    }

}
