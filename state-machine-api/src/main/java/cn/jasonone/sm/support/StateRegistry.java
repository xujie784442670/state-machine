package cn.jasonone.sm.support;

import cn.jasonone.sm.domain.StateContext;
import cn.jasonone.sm.domain.Transition;
import cn.jasonone.sm.enums.StateMachineActionType;

import java.util.Set;

/**
 * 状态机注册中心
 *
 * @implSpec 用于注册转换规则, 状态守卫, 状态动作
 */
public interface StateRegistry {
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
     * 注册转换规则
     *
     * @param machineCode 状态机编码
     * @param transition  转换规则
     * @implSpec 应使用 {@link StateMachineManager#saveTransition(String, Transition)} 来保存转换规则
     */
    void registerTransition(String machineCode, Transition transition);

    /**
     * 获取转换规则
     *
     * @param machineCode 状态机编码
     * @return 转换规则
     * @implSpec 应使用 {@link StateMachineManager#getTransitions(String)} 来获取转换规则
     */
    Set<Transition> getTransitions(String machineCode);

    /**
     * 获取转换规则
     *
     * @param machineCode 状态机编码
     * @param from        源状态
     * @param event       事件
     * @return 转换规则
     * @apiNote 获取转换规则, 当转换规则不存在时, 返回null
     */
    Transition getTransition(String machineCode, State from, Event event);

    /**
     * 注册状态守卫
     *
     * @param machineCode 状态机编码
     * @param sourceState 源状态
     * @param targetState 目标状态
     * @param guard       状态守卫
     * @apiNote 状态守卫执行顺序为: 状态守卫->状态动作(前置)->状态转换->状态动作(后置)
     */
    void registerGuard(String machineCode, State sourceState, State targetState, Guard guard);

    /**
     * 注册状态动作
     *
     * @param machineCode 状态机编码
     * @param sourceState 源状态
     * @param targetState 目标状态
     * @param action      状态动作
     * @param type        动作类型
     * @apiNote 状态动作执行顺序为: 状态守卫->状态动作(前置)->状态转换->状态动作(后置)
     */
    void registerAction(String machineCode, State sourceState, State targetState, Action action, StateMachineActionType type);

    /**
     * 执行状态守卫
     *
     * @param machineCode 状态机编码
     * @param sourceState 源状态
     * @param targetState 目标状态
     * @param context     上下文
     * @return 是否可以进行状态转换
     * @throws Exception 抛出异常
     * @apiNote 执行状态守卫, 返回true表示可以进行状态转换, 返回false表示不可以进行状态转换
     * @implSpec 当任意一个状态守卫返回false, 则返回false
     */
    boolean executeGuard(StateMachineEngine engine, String machineCode, State sourceState, State targetState, StateContext context) throws Exception;

    /**
     * 执行状态动作-前置
     *
     * @param machineCode 状态机编码
     * @param sourceState 源状态
     * @param targetState 目标状态
     * @param context     上下文
     * @throws Exception 抛出异常
     * @apiNote 执行状态动作-前置执行在状态转换之前执行
     */
    void executeBeforeAction(StateMachineEngine engine, String machineCode, State sourceState, State targetState, StateContext context) throws Exception;

    /**
     * 执行状态动作-后置
     *
     * @param machineCode 状态机编码
     * @param sourceState 源状态
     * @param targetState 目标状态
     * @param context     上下文
     * @throws Exception 抛出异常
     * @apiNote 执行状态动作-后置执行在状态转换之后执行
     */
    void executeAfterAction(StateMachineEngine engine, String machineCode, State sourceState, State targetState, StateContext context) throws Exception;

}
