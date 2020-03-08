package com.zyc.file_system.config;

import com.zyc.file_system.util.JsonUtils;
import com.zyc.file_system.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@EnableAsync
@Slf4j
public class MyLogAspect {



    /**
     * 用包名的方式定义切入点
     */
    @Pointcut("execution(* com.zyc..controller.*.*(..))")
    public void pointCut() {
    }

    /**
     * 前置通知
     *
     * @param joinPoint
     */
//    @Before("pointCut()")
//    public void doBeforeAdvice(JoinPoint joinPoint) {
//        //System.out.println("进入方法前执行.....");
//    }

    /**
     * 后置异常通知
     */
//    @AfterThrowing("pointCut()")
//    public void throwss(JoinPoint jp) {
//        //System.out.println("方法异常时执行.....");
//    }

    /**
     * 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
     */
//    @After("pointCut()")
//    public void after(JoinPoint jp) {
//        //System.out.println("方法最后执行.....");
//    }

    /**
     * 处理完请求，返回内容
     *
     * @param ret
     */
    @AfterReturning(returning = "ret", pointcut = "pointCut()")
    public void doAfterReturning(Object ret) {
        //System.out.println("方法的返回值 : " + ret);
    }

    /**
     * 环绕通知，相当于MethodInterceptor
     */
    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //请求返回信息
        Object res = null;
        //请求开始时间
        long startTime = System.currentTimeMillis();
        //请求结束时间
        long endTime = 0L;
        //请求时长(毫秒)
        long duration = 0L;
        try {
            //执行方法
            res = joinPoint.proceed();
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            return res;
        } finally {
            try {
                //方法执行完成后增加日志
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

                //客户端操作系统
                String osName = System.getProperty("os.name");
                //客户端操作系统版本
//                String osVersion = System.getProperty("os.version");
                //客户端浏览器的版本类型
                String userAgent = request.getHeader("user-agent");
                //请求ip
                String requestIp =NetworkUtil.getIpAddress(request);
                //请求url
//                String requestUrl = request.getRequestURL().toString();
                //请求uri
                String requestUri = request.getRequestURI();
                //请求方式 get,post等
                String requestType = request.getMethod();
                //请求参数
                String requestParam = getParams(joinPoint, request);
                //请求执行的类路径
//                String classPath = signature.getDeclaringTypeName();
                //请求执行的方法名
//                String methodName = signature.getName();

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                //输出
                log.info("---------"+(auth == null?"none":auth.getName())+">>>start-----------------");
                log.info("客户端操作系统: " + osName+" -- 客户端浏览器的版本类型: " + userAgent+" -- 请求ip: " + requestIp);
                log.info("请求uri: " + requestUri+" -- 请求方式: " + requestType+" -- 请求参数: " + requestParam);
                log.info("请求开始时间: " + startTime+" -- 请求结束时间: " + endTime+" -- 请求时长(毫秒): " + duration);
                log.info("----------------end------------------");


            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取请求参数 支持get,post(含与不含@RequestBody接收都可以获取)
     *
     * @param joinPoint
     * @param request
     * @return
     * @throws Exception
     */
    public String getParams(JoinPoint joinPoint, HttpServletRequest request) throws Exception {
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String params = "";
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            if ("POST".equals(method)) {
                Object object = args[0];
                //过滤掉 ServletRequest 和 ServletResponse 类型的参数
                Object paramObject = Arrays.stream(args).filter(t -> !(t instanceof ServletRequest) && !(t instanceof ServletResponse) && !(t instanceof BindingResult)).collect(Collectors.toList());
                params = JsonUtils.toJson(paramObject);
            } else if ("GET".equals(method)) {
                params = queryString;
            }
            params = URLDecoder.decode(params, "utf-8");
        }
        return params;
    }

}
