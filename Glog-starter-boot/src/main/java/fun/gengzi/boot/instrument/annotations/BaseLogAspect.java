package fun.gengzi.boot.instrument.annotations;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import fun.gengzi.boot.instrument.controller.LoggerFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

@Slf4j
@Aspect
public class BaseLogAspect {

    private static final ThreadLocal<Long> SERVICE_BUSINESS_TIME = new ThreadLocal<>();


    @Pointcut("@annotation(fun.gengzi.boot.instrument.annotations.BaseLog)")
    public void serviceLog() {
    }


    /**
     * 获取注解中的 businessInfo 内容
     *
     * @param joinPoint
     * @return
     */
    private String getBussinessInfo(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        BaseLog baseLogAnnotation = method.getAnnotation(BaseLog.class);
        String businessInfo = baseLogAnnotation.businessInfo();
        if (StringUtils.isBlank(businessInfo)) {
            return method.getName();
        }
        return baseLogAnnotation.businessInfo();
    }

    /**
     * 前置方法
     *
     * @param businessInfo
     * @param joinPoint
     * @return
     */
    public LoggerFormat methodBefore(String businessInfo, ProceedingJoinPoint joinPoint) {
        SERVICE_BUSINESS_TIME.set(System.currentTimeMillis());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取参数名称
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(businessInfo);
        // 如果是空数组，代表方法没有参数
        if (this.isEmptyArray(parameterNames) || this.isEmptyArray(args)) {
            arrayList.add("null");
        } else if (parameterNames.length == args.length) {
            HashMap<String, Object> map = new HashMap<>();
            IntStream.range(0, parameterNames.length).forEach(index -> {
                map.put(parameterNames[index], args[index]);
            });
            String params = JSONUtil.toJsonStr(map);
            arrayList.add(params);
        }
        return new LoggerFormat("[{}]>params:[{}]", arrayList);
    }

    private LoggerFormat methodAfter(String businessInfo, Object obj) {
        ArrayList<Object> arrayList = new ArrayList<>();
        Long time = System.currentTimeMillis() - SERVICE_BUSINESS_TIME.get();
        arrayList.add(businessInfo);
        if (obj == null) {
            arrayList.add(null);
        } else {
            String resultJson = "";
            if (JSONUtil.isJsonObj(JSONUtil.toJsonStr(obj))) {
                JSONObject jsonObject = JSONUtil.parseObj(JSONUtil.toJsonStr(obj));
                resultJson = jsonObject.toStringPretty();
            } else if (JSONUtil.isJsonArray(JSONUtil.toJsonStr(obj))) {
                JSONArray jsonArray = JSONUtil.parseArray(JSONUtil.toJsonStr(obj));
                resultJson = jsonArray.toStringPretty();
            } else {
                resultJson = JSONUtil.toJsonStr(obj);
            }
            arrayList.add(resultJson);
        }
        arrayList.add(time + "ms");
        return new LoggerFormat("[{}]<result:[{}],time:{}", arrayList);
    }


    @Around("serviceLog()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object obj = null;
        try {
            String businessInfo = getBussinessInfo(joinPoint);
            LoggerFormat methodBeforeLogger = methodBefore(businessInfo, joinPoint);
            log.info(methodBeforeLogger.getFormat(), methodBeforeLogger.getLogContent().toArray());
            obj = joinPoint.proceed();
            LoggerFormat methodAfterLogger = methodAfter(businessInfo, obj);
            log.info(methodAfterLogger.getFormat(), methodAfterLogger.getLogContent().toArray());
        } catch (Throwable e) {
            log.error("BaseLogAspect error:{}", e.getMessage());
        } finally {
            SERVICE_BUSINESS_TIME.remove();
        }
        return obj;
    }

    /**
     * 判断是否为空数组
     *
     * @param objects
     * @return
     */
    public boolean isEmptyArray(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return true;
        }
        return false;
    }


}