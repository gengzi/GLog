package fun.gengzi.boot.instrument.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

/**
 * <h1>logger 内容格式化</h1>
 *
 * @author gengzi
 * @date 2021年1月11日20:56:32
 */
@Data
@AllArgsConstructor
public class LoggerFormat {

    // log format
    private String format;
    // log content
    private ArrayList<Object> logContent = new ArrayList<Object>();

}
