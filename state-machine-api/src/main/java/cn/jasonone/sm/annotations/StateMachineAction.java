package cn.jasonone.sm.annotations;

import java.lang.annotation.*;

/**
 * 状态机动作注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(StateMachineActions.class)
public @interface StateMachineAction {
    /**
     * 状态机编码
     * <pre>
     *     可配置为多个状态机编码,当配置多个状态机编码时,则表示多个状态机编码都生效.但是必须在 {@link StateMachineAdvice#machineCode()} 的范围内,否则不生效
     * </pre>
     *
     * @return 状态机编码
     */
    String[] machineCode() default "*";

    /**
     * 源状态
     *
     * @return
     */
    String sourceState() default "*";

    /**
     * 目标状态
     *
     * @return
     */
    String targetState() default "*";

    /**
     * 是否在转换之前执行
     *
     * @return
     */
    boolean before() default false;
}
