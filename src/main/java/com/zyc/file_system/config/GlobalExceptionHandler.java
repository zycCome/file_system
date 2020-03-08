package com.zyc.file_system.config;

import com.zyc.file_system.entity.WebResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {




    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    @ResponseBody
    public WebResult maxUploadSizeExceededExceptionHandler(HttpServletRequest req, Exception e){
        log.error(e.getMessage(),e);
        return WebResult.fail("文件太大，上传失败");
    }

    /**
     * AccessDeniedException
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public WebResult accessDeniedExceptionHandler(HttpServletRequest req, Exception e){
        log.error(e.getMessage());
        return WebResult.fail("没有访问权限");
    }



    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public WebResult exceptionHandler(HttpServletRequest req, Exception e){
        log.error(e.getMessage(),e);
        return WebResult.fail(e.getMessage());
    }

}
