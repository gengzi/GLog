package fun.gengzi.boot.filter;

import fun.gengzi.boot.BaseLogProperties;
import fun.gengzi.boot.LogFiedsEnum;
import fun.gengzi.boot.MDCContentCreate;
import fun.gengzi.boot.instrument.execute.MDCInheritableThreadLocal;
import fun.gengzi.boot.trace.CreateTraceId;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

/**
 * <h1>全局日志拦截器</h1>
 *
 * @author gengzi
 * @date 2021年1月11日21:00:37
 */
@Slf4j
public class TraceFilter implements Filter {

    private BaseLogProperties baseLogProperties;

    @Autowired
    private CreateTraceId createTraceId;


    public TraceFilter(BaseLogProperties baseLogProperties) {
        this.baseLogProperties = baseLogProperties;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("<<< 初始化TraceFilter>>>");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            // 从请求头中获取加入日志的key value
            String requestURI = httpServletRequest.getRequestURI();
            String method = httpServletRequest.getMethod();

            // 生成 流水号，加入 theradloacl 中
            // aop 拦截controller 的请求，响应 ，构成请求为 postman 直接导入
            // 日志内打印，提供一个注解，进入方法的方法信息 和 出方法信息  json 格式化
            // 增加 resttemplate xml 等等请求的日志


            /**
             *2021-01-05 16:34:30.255 -| level [traceid] [thread] %c %l -| %msg%n
             *
             *
             *
             *
             *
             */
            String traceId = createTraceId.create();
            final HashMap<String, String> map = new HashMap<>();
            map.put(LogFiedsEnum.TRACEID.getLogFide(), traceId);
            map.put(LogFiedsEnum.METHOD.getLogFide(), method);
            map.put(LogFiedsEnum.REQUESTURL.getLogFide(), requestURI);
            MDCContentCreate.writeLog(map);
            // 放行
            MDCInheritableThreadLocal.set(MDC.getCopyOfContextMap());
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 请求MDC 的内容
            MDC.clear();
        }

    }

    @Override
    public void destroy() {
        log.info("<<< 销毁TraceFilter>>>");
        MDC.clear();
    }
}
