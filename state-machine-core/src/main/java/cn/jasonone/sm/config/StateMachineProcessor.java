package cn.jasonone.sm.config;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.*;
import cn.jasonone.sm.annotations.StateMachineAction;
import cn.jasonone.sm.annotations.StateMachineActions;
import cn.jasonone.sm.annotations.StateMachineAdvice;
import cn.jasonone.sm.annotations.StateMachineGuard;
import cn.jasonone.sm.domain.StateContext;
import cn.jasonone.sm.domain.Transition;
import cn.jasonone.sm.enums.StateMachineActionType;
import cn.jasonone.sm.support.State;
import cn.jasonone.sm.support.StateMachineEngine;
import cn.jasonone.sm.support.StateRegistry;
import jakarta.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 状态机处理器,用于收集状态动作和状态守卫
 */
public class StateMachineProcessor implements BeanPostProcessor {
    @Resource
    private StateRegistry registry;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> type = bean.getClass();
        if (AnnotationUtil.hasAnnotation(type, StateMachineAdvice.class)) {
            StateMachineAdvice advice = AnnotationUtil.getAnnotation(type, StateMachineAdvice.class);
            String[] machineCodes = advice.machineCode();
            for (Method method : ClassUtil.getDeclaredMethods(type)) {
                // 获取方法上的状态守卫
                if (AnnotationUtil.hasAnnotation(method, StateMachineGuard.class)) {
                    registerGurard(bean, method, machineCodes);
                }
                // 获取方法上的状态动作
                if (AnnotationUtil.hasAnnotation(method, StateMachineAction.class)) {
                    registerAction(bean, method, machineCodes);
                }
            }
        }
        return bean;
    }

    private void registerAction(Object bean, Method method, String[] machineCodes) {
        // 检查参数列表
        Assert.isTrue(method.getParameterCount() == 2, "状态动作方法参数列表错误, 参数列表为: StateMachineEngine, StateContext");
        Assert.isTrue(method.getReturnType() == void.class, "状态动作方法返回值类型错误, 返回值类型为: void");
        Assert.isTrue(method.getParameterTypes()[0] == StateMachineEngine.class, "状态动作方法参数类型错误, 参数类型为: StateMachineEngine, StateContext");
        Assert.isTrue(method.getParameterTypes()[1] == StateContext.class, "状态动作方法参数类型错误, 参数类型为: StateMachineEngine, StateContext");
        StateMachineActions smas = AnnotationUtil.getAnnotation(method, StateMachineActions.class);
        List<StateMachineAction> actions = new ArrayList<>();
        if (smas != null) {
            actions.addAll(Arrays.asList(smas.value()));
        } else {
            StateMachineAction action = AnnotationUtil.getAnnotation(method, StateMachineAction.class);
            actions.add(action);
        }
        for (StateMachineAction action : actions) {
            State sourceState = action::sourceState;
            State targetState = action::targetState;
            StateMachineActionType type = action.before() ? StateMachineActionType.BEFORE : StateMachineActionType.AFTER;
            List<String> mcList = getMachineCodes(machineCodes, action.machineCode());
            for (String mc : mcList) {
                registry.registerAction(mc, sourceState, targetState, (engine, ctx) -> method.invoke(bean, engine, ctx), type);
            }
        }
    }

    private void registerGurard(Object bean, Method method, String[] machineCodes) {
        // 检查参数列表
        Assert.isTrue(method.getParameterCount() == 2, "状态守卫方法参数列表错误, 参数列表为: StateMachineEngine, StateContext");
        Assert.isTrue(method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class, "状态守卫方法返回值类型错误, 返回值类型为: Boolean");
        Assert.isTrue(method.getParameterTypes()[0] == StateMachineEngine.class, "状态守卫方法参数类型错误, 参数类型为: StateMachineEngine, StateContext");
        Assert.isTrue(method.getParameterTypes()[1] == StateContext.class, "状态守卫方法参数类型错误, 参数类型为: StateMachineEngine, StateContext");
        StateMachineGuard guard = AnnotationUtil.getAnnotation(method, StateMachineGuard.class);
        String[] mcs = guard.machineCode();
        State sourceState = guard::sourceState;
        State targetState = guard::targetState;
        List<String> mcList = getMachineCodes(machineCodes, mcs);
        for (String mc : mcList) {
            registry.registerGuard(mc, sourceState, targetState, (engine, ctx) -> (Boolean) method.invoke(bean, engine, ctx));
        }
    }

    /**
     * 获取状态机编码列表
     * <pre>
     *     状态机编码获取规则:
     *     1. 如果父类状态机编码为*或为空, 则使用子类的状态机编码
     *     2. 如果子类没有配置状态机编码, 则使用父类的状态机编码
     *     3. 如果子类配置了状态机编码,且在父类的状态机编码范围内, 则使用子类的状态机编码
     *     4. 如果为*,则表示所有状态机编码
     * </pre>
     *
     * @param parentMcs
     * @param childMcs
     * @return
     */
    private List<String> getMachineCodes(String[] parentMcs, String[] childMcs) {
        List<String> mcs = new ArrayList<>();
        // 父类状态机编码为*
        if (ObjUtil.isEmpty(parentMcs) || ArrayUtil.contains(parentMcs, "*")) {
            return Arrays.asList(childMcs);
        }
        // 子类状态机编码为*
        if (ObjUtil.isEmpty(childMcs) || ArrayUtil.contains(childMcs, "*")) {
            return Arrays.asList(parentMcs);
        }
        for (String childMc : childMcs) {
            for (String parentMc : parentMcs) {
                if (StrUtil.containsAny(childMc, parentMc)) {
                    mcs.add(childMc);
                }
            }
        }
        return mcs;
    }
}
