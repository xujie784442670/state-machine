package cn.jasonone.sm.support;

import cn.jasonone.sm.domain.StateContext;

/**
 * 状态变更动作
 */
public interface Action {
    /**
     * 执行动作
     *
     * @param ctx 上下文
     * @throws Exception 抛出异常
     */
    void execute(StateMachineEngine engine, StateContext ctx) throws Exception;
}
