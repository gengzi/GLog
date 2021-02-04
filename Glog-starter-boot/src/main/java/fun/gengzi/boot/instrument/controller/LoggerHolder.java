package fun.gengzi.boot.instrument.controller;


import fun.gengzi.boot.LoggerInfo;

/**
 * <h1>当前日志线程</h1>
 * <p>
 * 持有当前线程的 loggerInfo 对象内容
 *
 * @author gengzi
 * @date 2021年1月11日20:58:45
 */
public class LoggerHolder {

    private static final ThreadLocal<LoggerInfo> LOGGER_HOLDER = new ThreadLocal<>();

    private LoggerHolder() {
    }

    static void set(LoggerInfo loggerInfo) {
        LOGGER_HOLDER.set(loggerInfo);
    }

    static LoggerInfo get() {
        return LOGGER_HOLDER.get();
    }

    static void remove() {
        LOGGER_HOLDER.remove();
    }

    /**
     * 返回userid
     *
     * @return
     */
    static String getUserId() {
        return get().getUserId();
    }

}
