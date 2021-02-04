package fun.gengzi.boot.environment;

import fun.gengzi.boot.BaseLogProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * <h1>日志信息环境后处理器</h1>
 *
 *
 * 主要用于设置默认的配置
 *
 * autoconfig
 * 配置对象注入，过滤器，拦截器，aop
 *
 *
 * @author gengzi
 * @date 2021年1月4日22:35:42
 */
@Component
public class LogInfoEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "defaultProperties";

    private static final String BASELOG = "baselog";

    // 用于绑定对象
    private Binder binder;
    // 属性
    private BaseLogProperties baseLogProperties = null;


    /**
     * 设置环境变量
     *
     * @param environment
     * @param application
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        HashMap<String, Object> map = new HashMap<>();
        this.binder = Binder.get(environment);
        try {
            baseLogProperties = binder.bind(BASELOG, BaseLogProperties.class).get();
        } catch (Exception e) {
            // 执行默认配置
            baseLogProperties = null;
        }
        // 判断属性是否配置
        if (baseLogProperties != null) {
            boolean enable = Boolean.parseBoolean(baseLogProperties.getEnable());
            if (enable) {
                map.put("logging.pattern.level",
                        "%5p [%X{X-raceId:-}]");
                // 替换Spring中的默认值
                addOrReplace(environment.getPropertySources(), map);
            }
        }
    }

    private void addOrReplace(MutablePropertySources propertySources, Map<String, Object> map) {
        MapPropertySource target = null;
        if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
            PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
            if (source instanceof MapPropertySource) {
                target = (MapPropertySource) source;
                Iterator var5 = map.keySet().iterator();

                while (var5.hasNext()) {
                    String key = (String) var5.next();
                    if (!target.containsProperty(key)) {
                        ((Map) target.getSource()).put(key, map.get(key));
                    }
                }
            }
        }

        if (target == null) {
            target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        }

        if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
            propertySources.addLast(target);
        }

    }
}
