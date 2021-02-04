package fun.gengzi.boot.instrument.execute;


/**
 * <h1>线程池增加Traceid 的注解</h1>
 */
public @interface ThreadPoolExecuteLog {

    String traceId() default "bl-traceid";


}
