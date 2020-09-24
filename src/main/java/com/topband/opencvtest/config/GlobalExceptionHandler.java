package com.topband.opencvtest.config;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/7/24 16:44
 * @remark
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
class GlobalExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error";

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public String defaultErrorHandler(Exception e) throws Exception {
        //String url=req.getRequestURL().toString();
        log.error("全局异常捕获",e);
        e.printStackTrace();
//        ResultModel0<String> res=new ResultModel0();
//        res.setCode(HttpResultCodeEnum.SYSTEM_ERROR.getCode());
//        res.setMsg("system error");
//        res.setData("");
        return  "系统出错";

    }


}

