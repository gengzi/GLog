package fun.gengzi.boot.instrument.annotations;

import java.lang.annotation.*;

/**
 * <h1>基础日志注解</h1>
 *
 * @author gengzi
 * @date 2021年1月11日21:43:31
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaseLog {
    // 业务信息
    String businessInfo() default "";

}

// 追加流程，返回流水号，返回到响应头，前端再携带回来。如果请求头有流水号，就使用，没有就生成
// 生成的token，token 在redis 中存储，有。就说明之前生成了，最后提交时，直接删除，下次如果还使用该流程号，就不让提交
// 提交接口，幂等校验