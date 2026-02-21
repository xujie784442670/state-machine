package cn.jasonone.sm.support;

import cn.jasonone.sm.domain.StateContext;

/**
 * 状态机守卫
 */
public interface Guard {
    /**
     * 判断是否可以进行状态转换
     *
     * @param engine  状态机引擎
     * @param context 状态上下文
     * @return 是否可以进行状态转换
     */
    boolean canTransition(StateMachineEngine engine, StateContext context) throws  Exception;

    /**
     * 获取优先级(值越小优先级越高,默认0)
     *
     * @return 优先级
     */
    default int getPriority() {
        return 0;
    }
}
