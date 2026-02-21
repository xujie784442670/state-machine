package cn.jasonone.sm.core;

import cn.hutool.core.lang.Assert;
import cn.jasonone.sm.domain.StateContext;
import cn.jasonone.sm.domain.Transition;
import cn.jasonone.sm.support.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 状态机引擎默认实现
 */
@Data
@Slf4j
public class DefaultStateMachineEngine implements StateMachineEngine {

    private final StateRegistry registry;

    private final StateMachineManager manager;

    private final TransactionManagement transactionManagement;

    /**
     * 获取当前状态
     *
     * @param machineCode
     * @param businessId
     * @return
     */
    private State getCurrentState(String machineCode, String businessId) {
        State currentState = manager.getCurrentState(machineCode, businessId);
        if (currentState == null) {
            currentState = registry.getInitialState(machineCode);
            Assert.notNull(currentState, "状态机[{}]未配置初始状态", machineCode);
        }
        return currentState;
    }

    /**
     * 创建状态上下文
     *
     * @param machineCode 状态机编码
     * @param businessId  业务ID
     * @param transition  转换规则
     * @param payload     数据
     * @return 状态上下文
     */
    private StateContext createStateContext(String machineCode, String businessId, Transition transition, Object payload) {
        return new StateContext(machineCode, businessId, transition.from(), transition.to(), transition.event(), payload);
    }


    @Override
    public void trigger(String machineCode, String businessId, Event event, Object payload) throws Exception {
        log.info("状态机[{}] 业务ID[{}] 触发事件[{}]", machineCode, businessId, event.getCode());
        State currentState = getCurrentState(machineCode, businessId);
        Transition transition = registry.getTransition(machineCode, currentState, event);
        Assert.notNull(transition, "状态机[{}] 不支持的事件[{}]", machineCode, event.getCode());
        StateContext context = createStateContext(machineCode, businessId, transition, payload);
        log.trace("状态机[{}] 业务ID[{}]构建状态上下文: {}", machineCode, businessId, context);
        transactionManagement.begin();
        try {
            log.debug("状态机[{}] 业务ID[{}]执行状态守卫: {}", machineCode, businessId, transition.key());
            Assert.isTrue(registry.executeGuard(this, machineCode, currentState, transition.to(), context), "状态机[{}] 守卫[{}]未通过", machineCode, transition.key());
            log.debug("状态机[{}] 业务ID[{}]执行状态动作(前置): {}", machineCode, businessId, transition.key());
            registry.executeBeforeAction(this, machineCode, currentState, transition.to(), context);
            log.debug("状态机[{}] 业务ID[{}]更新状态: {} -> {}", machineCode, businessId, currentState.getState(), transition.to().getState());
            manager.updateState(machineCode, businessId, transition.to());
            log.debug("状态机[{}] 业务ID[{}]执行状态动作(后置): {}", machineCode, businessId, transition.key());
            registry.executeAfterAction(this, machineCode, currentState, transition.to(), context);
            // 提交事务
            transactionManagement.commit();
        } catch (Exception e) {
            // 回滚事务
            transactionManagement.rollback();
            throw new RuntimeException(e);
        }
    }
}
