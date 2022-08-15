package com.wllt.faceid.core.exceptionhandler;

import com.wllt.faceid.core.utils.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author SCW
 * @date 2022/5/16 15:06
 * 全局异常处理
 */
//@ControllerAdvice
public class AllExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public SaResult allException(Exception ex){
        return SaResult.error(ex.toString());
    }


}
