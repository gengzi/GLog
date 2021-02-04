package fun.gengzi.boot.trace;

import cn.hutool.core.util.IdUtil;

/**
 * <H1>默认实现</H1>
 *
 * @author gengzi
 * @date 2021年1月6日11:20:54
 */
public class DefaultCreateTraceId extends CreateTraceId {

    // Lazily initialized and cached.
    volatile String traceIdString;

    /**
     * 创建方法
     *
     * @return
     */
    @Override
    public String create() {
//        String r = traceIdString;
//        if (r == null) {
//            r = IdUtil.simpleUUID();
//            traceIdString = r;
//        }
//        return r;
        return IdUtil.simpleUUID();
    }
}
