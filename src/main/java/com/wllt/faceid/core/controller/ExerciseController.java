package com.wllt.faceid.core.controller;

import com.wllt.faceid.core.db.domain.Exercise;
import com.wllt.faceid.core.db.service.ExerciseService;
import com.wllt.faceid.core.timed.TimedTask;
import com.wllt.faceid.core.utils.SaResult;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * @author SCW
 * @date 2022/6/21 9:50
 * 成绩录入
 */
@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    @Autowired
    ExerciseService exerciseService;

    @Autowired
    TimedTask timedTask;

    /**
     * 插入
     *
     * @param exercise
     * @return
     */
    @RequestMapping("/insert")
    public SaResult Insert(@RequestBody @NonNull Exercise exercise) {
        exercise.setId(0);
        if (exercise.getFilename().length() == 0) {
            exercise.setFilename(null);
        }
        exerciseService.save(exercise);
        return SaResult.ok();
    }

    /**
     * 发送到阿里云oss
     * 返回videoid
     */
    @GetMapping("/add")
    public SaResult add(String path) throws Exception {
        Integer videoid = timedTask.OssUpload(new File(path));
        return SaResult.data(videoid);
    }

}
