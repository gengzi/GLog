package fun.gengzi.boot.instrument.controller;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * <H1>默认userid 的实现</H1>
 */
public class DefaultBaseLogUserServiceImpl implements BaseLogUserService {

    /**
     * 获取用户id
     *
     * @return
     */
    @Override
    public String getUserId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest().getSession().getId();
    }
}
