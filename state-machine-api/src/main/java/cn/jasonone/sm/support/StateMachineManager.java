package cn.jasonone.sm.support;

import cn.jasonone.sm.domain.Transition;

import java.util.Set;

/**
 * 状态管理器
 *
 * @implSpec 用于获取指定业务的当前状态已经更新状态
 */
public interface StateMachineManager {
    /**
     * 设置初始状态
     *
     * @param machineCode  状态机编码
     * @param initialState 初始状态
     * @apiNote 设置初始状态, 当状态机获取当前状态为null时, 则使用初始状态
     */
    void setInitialState(String machineCode, State initialState);

    /**
     * 获取初始状态
     *
     * @param machineCode 状态机编码
     * @return 初始状态
     * @apiNote 获取初始状态, 当初始状态不存在时, 返回null
     */
    State getInitialState(String machineCode);
    /**
     * 获取当前状态
     *
     * @param machineCode 状态机编码
     * @param businessId  业务ID
     * @return 当前状态
     */
    State getCurrentState(String machineCode, String businessId);

    /**
     * 保存转换规则
     *
     * @param machineCode 状态机编码
     * @param transition  转换规则
     */
    void saveTransition(String machineCode, Transition transition);

    /**
     * 获取转换规则
     *
     * @param machineCode 状态机编码
     * @return 转换规则
     * @implSpec 修改返回的Set时不应该影响原始的转换规则
     * @apiNote 获取转换规则时, 请勿修改返回的Set
     */
    Set<Transition> getTransitions(String machineCode);

    /**
     * 更新当前状态
     *
     * @param machineCode 状态机编码
     * @param businessId  业务ID
     * @param state       当前状态
     * @return 是否更新成功
     */
    boolean updateState(String machineCode, String businessId, State state);
}
