package com.wllt.faceid;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//开启定时任务
@EnableScheduling
@MapperScan("com.wllt.faceid.core.db.mapper")
public class FaceIdApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(FaceIdApplication.class, args);
/*        System.setProperty("org.bytedeco.javacpp.maxphysicalbytes", "0");
        System.setProperty("org.bytedeco.javacpp.maxbytes", "0");*/
    }
//wmic csproduct get UUID

    //发布到tomcat用的
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // TODO Auto-generated method stub
        return builder.sources(FaceIdApplication.class);
    }
}
