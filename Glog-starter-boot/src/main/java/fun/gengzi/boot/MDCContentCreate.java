package fun.gengzi.boot;


import org.slf4j.MDC;

import java.util.Map;

/**
 * <h1>日志内容写入</h1>
 *
 * @author gengzi
 * @date 2021年1月6日09:57:23
 */
public class MDCContentCreate {


    /**
     * 写入日志
     *
     * @param traceMap
     */
    public static void writeLog(Map<String, String> traceMap) {
        LogFiedsEnum.LOGFIDE_TO_DESC.forEach(
                (key, value) -> {
                    // 设置日志
                    if (traceMap.getOrDefault(key, null) != null) {
                        MDC.put(key, traceMap.get(key));
                    }
                }
        );
    }


}
