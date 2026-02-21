package cn.jasonone.sm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 状态机属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "state-machine")
public class StateMachineProperties {
    /**
     * 默认状态机编码
     */
    private String defaultMachineCode;
}
