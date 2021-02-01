package java.glog.base;


import org.slf4j.MDC;

import java.util.Map;

public final class MDCInheritableThreadLocal {

    static {
        System.out.println("MDCInheritableThreadLocal class loader is " + MDCInheritableThreadLocal.class.getClassLoader());
    }

    private static final InheritableThreadLocal LOGGER_HOLDER = new InheritableThreadLocal<>();

    public static void set(Object obj) {
        LOGGER_HOLDER.set(obj);
    }

    public static Object get() {
        return LOGGER_HOLDER.get();
    }

    public static void remove() {
        LOGGER_HOLDER.remove();
    }

    public static void setMdc() {
        MDC.setContextMap((Map<String, String>) LOGGER_HOLDER.get());
    }

}
