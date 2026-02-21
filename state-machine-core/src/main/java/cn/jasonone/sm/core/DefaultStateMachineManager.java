package cn.jasonone.sm.core;

import cn.hutool.core.lang.Assert;
import cn.jasonone.sm.domain.Transition;
import cn.jasonone.sm.support.State;
import cn.jasonone.sm.support.StateMachineManager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 状态管理器默认实现
 *
 */
public class DefaultStateMachineManager implements StateMachineManager {

    private static final Map<String, Set<Transition>> TRANSITION_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, State> STATE_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, State> INITIAL_STATE_CACHE = new ConcurrentHashMap<>();


    @Override
    public void setInitialState(String machineCode, State initialState) {
        Assert.isFalse(INITIAL_STATE_CACHE.containsKey(machineCode), "状态机[{}]已存在初始状态", machineCode);
        INITIAL_STATE_CACHE.put(machineCode, initialState);
    }

    @Override
    public State getInitialState(String machineCode) {
        return INITIAL_STATE_CACHE.get(machineCode);
    }

    @Override
    public State getCurrentState(String machineCode, String businessId) {
        Assert.notBlank(machineCode, "状态机编码不能为空");
        Assert.notBlank(businessId, "业务ID不能为空");
        String key = getKey(machineCode, businessId);
        return STATE_CACHE.get(key);
    }

    private static String getKey(String machineCode, String businessId) {
        return machineCode + ":" + businessId;
    }

    @Override
    public void saveTransition(String machineCode, Transition transition) {
        Assert.notBlank(machineCode, "状态机编码不能为空");
        Assert.notNull(transition, "转换规则不能为空");
        Set<Transition> transitions = TRANSITION_CACHE.computeIfAbsent(machineCode, k -> ConcurrentHashMap.newKeySet());
        Assert.isTrue(transitions.add(transition), "状态转换规则[{}->{}->{}]已存在", transition.from(), transition.event(), transition.to());
    }

    @Override
    public Set<Transition> getTransitions(String machineCode) {
        ConcurrentHashMap.KeySetView<Transition, Boolean> transitions = ConcurrentHashMap.newKeySet();
        transitions.addAll(TRANSITION_CACHE.get(machineCode));
        return transitions;
    }

    @Override
    public boolean updateState(String machineCode, String businessId, State state) {
        Assert.notBlank(machineCode, "状态机编码不能为空");
        Assert.notBlank(businessId, "业务ID不能为空");
        Assert.notNull(state, "状态不能为空");
        String key = getKey(machineCode, businessId);
        STATE_CACHE.put(key, state);
        return true;
    }
}
