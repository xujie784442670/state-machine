package cn.jasonone.sm.core;

import cn.hutool.core.util.StrUtil;
import cn.jasonone.sm.domain.StateContext;
import cn.jasonone.sm.domain.Transition;
import cn.jasonone.sm.enums.StateMachineActionType;
import cn.jasonone.sm.support.*;
import lombok.Data;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 状态机注册中心默认实现
 */
@Data
public class DefaultStateRegistry implements StateRegistry {

    private static final String ALL_MATCH = "*";
    private static final String KEY_SEPARATOR = ":";

    private final StateMachineManager manager;

    private final Map<String, Transition> TRANSITION_CACHE = new ConcurrentHashMap<>();
    private final Map<String, Set<Guard>> GUARD_CACHE = new ConcurrentHashMap<>();
    private final Map<String, Set<Action>> ACTION_BEFORE_CACHE = new ConcurrentHashMap<>();
    private final Map<String, Set<Action>> ACTION_AFTER_CACHE = new ConcurrentHashMap<>();


    @Override
    public void setInitialState(String machineCode, State initialState) {
        manager.setInitialState(machineCode, initialState);
    }

    @Override
    public State getInitialState(String machineCode) {
        return manager.getInitialState(machineCode);
    }

    @Override
    public void registerTransition(String machineCode, Transition transition) {
        manager.saveTransition(machineCode, transition);
    }

    @Override
    public Set<Transition> getTransitions(String machineCode) {
        Set<Transition> transitions = manager.getTransitions(machineCode);
        Set<Transition> objects = ConcurrentHashMap.newKeySet(transitions.size());
        objects.addAll(transitions);
        return objects;
    }

    @Override
    public Transition getTransition(String machineCode, State from, Event event) {
        String key = machineCode + ":" + from.getState() + ":" + event.getCode();
        // 缓存中获取
        Transition transition = TRANSITION_CACHE.get(key);
        if (transition != null) {
            return transition;
        }
        // 获取所有转换规则
        Set<Transition> transitions = manager.getTransitions(machineCode);
        key = from.getState() + ":" + event.getCode();
        // 遍历转换规则
        for (Transition t : transitions) {
            if (StrUtil.equals(t.key(), key)) {
                // 缓存转换规则
                TRANSITION_CACHE.put(machineCode + ":" + key, t);
                return t;
            }
        }
        return null;
    }

    @Override
    public void registerGuard(String machineCode, State sourceState, State targetState, Guard guard) {
        String key = buildKey(machineCode, sourceState, targetState);
        Set<Guard> guards = GUARD_CACHE.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet());
        guards.add(guard);
    }

    @Override
    public void registerAction(String machineCode, State sourceState, State targetState, Action action, StateMachineActionType type) {
        String key = buildKey(machineCode, sourceState, targetState);
        Set<Action> actions = null;
        if (type == StateMachineActionType.BEFORE) {
            actions = ACTION_BEFORE_CACHE.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet());
        } else if (type == StateMachineActionType.AFTER) {
            actions = ACTION_AFTER_CACHE.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet());
        }
        if (actions != null) {
            actions.add(action);
        }
    }

    @Override
    public boolean executeGuard(StateMachineEngine engine, String machineCode, State sourceState, State targetState, StateContext context) throws Exception {
        Set<Guard> guards = getList(machineCode, sourceState, targetState,GUARD_CACHE);
        if (guards != null) {
            for (Guard guard : guards) {
                if (!guard.canTransition(engine, context)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void executeBeforeAction(StateMachineEngine engine, String machineCode, State sourceState, State targetState, StateContext context) throws Exception {
        String key = buildKey(machineCode, sourceState, targetState);
        Set<Action> actions = getList(machineCode, sourceState, targetState,ACTION_BEFORE_CACHE);
        if (actions != null) {
            for (Action action : actions) {
                action.execute(engine, context);
            }
        }
    }

    @Override
    public void executeAfterAction(StateMachineEngine engine, String machineCode, State sourceState, State targetState, StateContext context) throws Exception {
        String key = buildKey(machineCode, sourceState, targetState);
        Set<Action> actions = getList(machineCode, sourceState, targetState,ACTION_AFTER_CACHE);
        if (actions != null) {
            for (Action action : actions) {
                action.execute(engine, context);
            }
        }
    }



    /**
     * 构建缓存Key：machineCode:sourceState:targetState
     * @param machineCode 机器码
     * @param sourceState 源状态
     * @param targetState 目标状态
     * @return 拼接后的Key
     */
    private String buildKey(String machineCode, State sourceState, State targetState) {
        // 处理State为null的情况，默认用*匹配所有
        String source = sourceState == null ? ALL_MATCH : sourceState.getState();
        String target = targetState == null ? ALL_MATCH : targetState.getState();
        // 处理machineCode为null的情况
        String machine = StrUtil.isBlank(machineCode) ? ALL_MATCH : machineCode;
        return machine + KEY_SEPARATOR + source + KEY_SEPARATOR + target;
    }

    /**
     * 根据machineCode、sourceState、targetState从缓存中获取匹配的集合
     * 支持*通配符匹配，优先级：精确匹配 > 部分通配 > 全通配
     * @param machineCode 机器码（支持*）
     * @param sourceState 源状态（支持*）
     * @param targetState 目标状态（支持*）
     * @param cache 目标缓存Map
     * @return 匹配到的集合（线程安全，无重复元素）
     */
    private static <T> Set<T> getList(String machineCode, State sourceState, State targetState, Map<String, Set<T>> cache) {
        // 空缓存直接返回空集合
        if (cache == null || cache.isEmpty()) {
            return Collections.emptySet();
        }

        // 处理入参，null值替换为*
        String mc = StrUtil.isBlank(machineCode) ? ALL_MATCH : machineCode;
        String ss = sourceState == null ? ALL_MATCH : sourceState.getState();
        String ts = targetState == null ? ALL_MATCH : targetState.getState();

        // 线程安全的结果集合，避免并发问题
        Set<T> result = ConcurrentHashMap.newKeySet();

        // 遍历缓存所有Key，匹配符合规则的项
        for (Map.Entry<String, Set<T>> entry : cache.entrySet()) {
            String cacheKey = entry.getKey();
            Set<T> cacheValue = entry.getValue();

            if (cacheValue == null || cacheValue.isEmpty()) {
                continue;
            }

            // 拆分缓存Key为三部分：machineCode:sourceState:targetState
            String[] keyParts = cacheKey.split(KEY_SEPARATOR);
            if (keyParts.length != 3) {
                // 非法Key格式，跳过
                continue;
            }

            String cacheMc = keyParts[0];
            String cacheSs = keyParts[1];
            String cacheTs = keyParts[2];

            // 匹配规则：缓存Key的每一部分为* 或 与入参相等，则该部分匹配
            boolean machineMatch = ALL_MATCH.equals(cacheMc) || mc.equals(cacheMc);
            boolean sourceMatch = ALL_MATCH.equals(cacheSs) || ss.equals(cacheSs);
            boolean targetMatch = ALL_MATCH.equals(cacheTs) || ts.equals(cacheTs);

            // 三个部分都匹配，才将对应集合加入结果
            if (machineMatch && sourceMatch && targetMatch) {
                result.addAll(cacheValue);
            }
        }

        return result;
    }
}
