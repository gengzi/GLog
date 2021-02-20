package fun.gengzi.core.servicelog;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

/**
 * <h1>service层日志打印</h1>
 *
 * @author gengzi
 * @date 2021年2月20日15:57:02
 */
public class BaseLogService {

    private static final ThreadLocal<Long> SERVICE_BUSINESS_TIME = new ThreadLocal<>();
    private static Object[] parameterNames;

    /**
     * 前置日志打印方法
     *
     * @param businessInfo
     * @param parameterNames
     */
    public static void methodBeforeLog(String businessInfo, Object... parameterNames) {
        SERVICE_BUSINESS_TIME.set(System.currentTimeMillis());



//
//        // 获取参数名称
//
//        ArrayList<Object> arrayList = new ArrayList<>();
//        arrayList.add(businessInfo);
//        // 如果是空数组，代表方法没有参数
//        if (isEmptyArray(parameterNames) || isEmptyArray(args)) {
//            arrayList.add("null");
//        } else if (parameterNames.length == args.length) {
//            HashMap<String, Object> map = new HashMap<>();
//            IntStream.range(0, parameterNames.length).forEach(index -> {
//                map.put(parameterNames[index], args[index]);
//            });
//            String params = JSONUtil.toJsonStr(map);
//            arrayList.add(params);
//        }
//        log.info("[{}]>params:[{}]", arrayList.toArray());


    }

    /**
     * 后置日志打印方法
     *
     * @param businessInfo
     * @param object
     */
    public static void methodAfterLog(String businessInfo, Object... object) {

    }

    /**
     * 判断是否为空数组
     *
     * @param objects
     * @return
     */
    public static boolean isEmptyArray(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return true;
        }
        return false;
    }

}