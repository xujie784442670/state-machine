package cn.jasonone.sm.config;

import cn.jasonone.sm.support.StateMachineConfiguration;
import lombok.Data;

/**
 * 默认状态机配置
 */
@Data
public class DefaultStateMachineConfiguration implements StateMachineConfiguration {

    private final StateMachineProperties properties;

    @Override
    public String getDefaultMachineCode() {
        return properties.getDefaultMachineCode();
    }
}
