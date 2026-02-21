package cn.jasonone.sm.support;

import cn.jasonone.sm.domain.StateContext;

/**
 * 状态机引擎
 */
public interface StateMachineEngine {

    /**
     * 获取状态机注册中心
     *
     * @return 状态机注册中心实例
     */
    StateRegistry getRegistry();

    /**
     * 触发状态机事件
     *
     * @param machineCode 状态机编码
     * @param businessId  业务ID
     * @param event       事件
     * @param payload     数据
     * @throws Exception 抛出异常
     */
    void trigger(String machineCode, String businessId, Event event, Object payload) throws Exception;
    /**
     * 触发状态机事件
     *
     * @param machineCode 状态机编码
     * @param businessId  业务ID
     * @param event       事件
     * @throws Exception 抛出异常
     */
    default void trigger(String machineCode, String businessId, Event event) throws Exception{
        trigger(machineCode, businessId, event, null);
    }
}
