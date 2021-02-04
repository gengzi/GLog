package fun.gengzi.boot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggerInfo {
    // 请求路径
    private String uri;
    // 跟踪id
    private String traceid;
    // 请求ip
    private String deviceIp;
    // 请求方法名称
    private String requestName;
    // 请求路径
    private String reqUrl;
    // 请求方式
    private String httpMethod;
    // 请求数据，默认json格式  请求body
    private Object requestData;
    // 用户id
    private String userId;
    // 请求url的参数
    private Map<String, String[]> requestMap;
    // 响应数据
    private Object result;
    // 业务时间耗时
    private String timeCostMils;

}
