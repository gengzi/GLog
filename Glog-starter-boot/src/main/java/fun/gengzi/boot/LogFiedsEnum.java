package fun.gengzi.boot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <H1>全量日志字段常量类</H1>
 */
@AllArgsConstructor
@Getter
public enum LogFiedsEnum {
    //
    TRACEID("bl-traceid","跟踪id"),
    REQUESTURL("requesturl", "请求的url"),
    METHOD("method", "请求的方法");

    // 日志字段
    private String logFide;
    // 注释
    private String desc;
    // 全局日志字段map
    public static final Map<String, String> LOGFIDE_TO_DESC = Arrays.stream(LogFiedsEnum.values()).collect(Collectors.toMap(LogFiedsEnum::getLogFide, LogFiedsEnum::getDesc));

}
