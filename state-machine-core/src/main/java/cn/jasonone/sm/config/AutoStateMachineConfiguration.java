package cn.jasonone.sm.config;

import cn.jasonone.sm.core.DefaultStateMachineEngine;
import cn.jasonone.sm.core.DefaultStateMachineManager;
import cn.jasonone.sm.core.DefaultStateRegistry;
import cn.jasonone.sm.core.DefaultTransactionManagement;
import cn.jasonone.sm.support.*;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(StateMachineProperties.class)
public class AutoStateMachineConfiguration {
    @Resource
    private StateMachineProperties properties;

    @Bean
    public StateMachineProcessor stateMachineProcessor() {
        return new StateMachineProcessor();
    }

    @Bean
    @ConditionalOnBean(StateMachineProperties.class)
    @ConditionalOnMissingBean(StateMachineConfiguration.class)
    public StateMachineConfiguration stateMachineConfiguration() {
        return new DefaultStateMachineConfiguration(properties);
    }

    @Bean
    @ConditionalOnMissingBean(StateMachineManager.class)
    public StateMachineManager stateMachineManager() {
        return new DefaultStateMachineManager();
    }

    @Bean
    @ConditionalOnMissingBean(TransactionManagement.class)
    public TransactionManagement transactionManagement() {
        return new DefaultTransactionManagement();
    }

    @Bean
    @ConditionalOnMissingBean(StateRegistry.class)
    public StateRegistry stateRegistry(StateMachineManager  manager) {
        return new DefaultStateRegistry(manager);
    }

    @Bean
    @ConditionalOnBean({StateMachineManager.class, StateRegistry.class})
    @ConditionalOnMissingBean(StateMachineEngine.class)
    public StateMachineEngine stateMachineEngine(StateRegistry registry, StateMachineManager manager, TransactionManagement transactionManagement) {
        return new DefaultStateMachineEngine(registry, manager, transactionManagement);
    }


}
