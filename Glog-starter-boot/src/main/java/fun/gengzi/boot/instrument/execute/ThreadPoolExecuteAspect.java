package fun.gengzi.boot.instrument.execute;


import fun.gengzi.boot.LogFiedsEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

/**
 * <H1>线程池执行切面</H1>
 *
 * @author gengzi
 * @date 2021年1月6日14:44:15
 */
@Aspect
@Configuration

public class ThreadPoolExecuteAspect {

//    @Pointcut("execution(public * java.util.concurrent.ThreadPoolExecutor.execute(..))")
//    public void requestLog(){
//
//    }
    @Pointcut("execution(public * java.lang.Thread.start())")
    public void requestLog(){

    }

//    @Pointcut("@annotation(fun.gengzi.baselog.instrument.execute.ThreadPoolExecuteLog)")
//    public void requestLog() {
//
//    }

    /**
     * 将线程中的traceid 传递进来
     *
     * @param joinPoint
     */
    @Around("requestLog()")
    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = MDC.get(LogFiedsEnum.TRACEID.getLogFide());


        Object proceed = null;
        try {
            proceed = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return proceed;

    }





}
