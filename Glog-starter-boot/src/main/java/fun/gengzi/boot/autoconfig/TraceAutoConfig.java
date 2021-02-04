package fun.gengzi.boot.autoconfig;

import fun.gengzi.boot.BaseLogProperties;
import fun.gengzi.boot.LoggerInfo;
import fun.gengzi.boot.MDCContentCreate;
import fun.gengzi.boot.filter.TraceFilter;
import fun.gengzi.boot.instrument.annotations.BaseLogAspect;
import fun.gengzi.boot.instrument.controller.BaseLogUserService;
import fun.gengzi.boot.instrument.controller.ControllerLogAspect;
import fun.gengzi.boot.instrument.controller.DefaultBaseLogUserServiceImpl;
import fun.gengzi.boot.trace.CreateTraceId;
import fun.gengzi.boot.trace.DefaultCreateTraceId;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>跟踪自动配置类</h1>
 * <p>
 * 1, 配置全局过滤器
 *
 * @author gengzi
 * @date 2021年1月5日14:03:17
 */
@Configuration
@ConditionalOnProperty(
        value = {"baselog.enabled"},
        matchIfMissing = true
)
@EnableConfigurationProperties({BaseLogProperties.class})
public class TraceAutoConfig {

    /**
     * 构造全局日志追踪过滤器
     *
     * @param baseLogProperties
     * @return
     */
    @Bean
    public TraceFilter traceFilter(BaseLogProperties baseLogProperties) {
        return new TraceFilter(baseLogProperties);
    }


    @Bean
    @ConditionalOnMissingBean // 如果用户创建了该Bean，此处将不再创建
    public CreateTraceId createTraceId() {
        return new DefaultCreateTraceId();
    }


    @Bean
    public MDCContentCreate mdcContentCreate() {
        return new MDCContentCreate();
    }


//    @Bean
//    @ConditionalOnMissingBean
//    public LoggerInfo loggerInfo() {
//        return new LoggerInfo();
//    }


    @Bean
    @ConditionalOnMissingBean
    public ControllerLogAspect controllerLogAspect(LoggerInfo loggerInfo) {
        return new ControllerLogAspect();
    }


    /**
     * ctl 切面类
     * @param baseLogProperties
     * @param controllerLogAspect
     * @return
     */
    @Bean
    public AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor(BaseLogProperties baseLogProperties, ControllerLogAspect controllerLogAspect) {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(baseLogProperties.getPackageurl());
        advisor.setAdvice(controllerLogAspect);
        return advisor;
    }


    /**
     * 用户服务
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public BaseLogUserService baseLogUserService() {
        return new DefaultBaseLogUserServiceImpl();
    }


    @Bean
    public BaseLogAspect baseLogAspect(){
        return new BaseLogAspect();
    }

}
