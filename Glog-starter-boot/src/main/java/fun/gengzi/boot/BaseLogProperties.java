package fun.gengzi.boot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

/**
 * <h1>基础日志属性绑定类</h1>
 *
 * @author gengzi
 * @date 2021年1月5日10:38:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("baselog")
public class BaseLogProperties implements Serializable {
    // 默认日志包围路径
    private String packageurl;
    // 是否开启基础日志
    private String enable;
    // 忽略的日志字段
    private List<String> ignoreLogFields;


}
