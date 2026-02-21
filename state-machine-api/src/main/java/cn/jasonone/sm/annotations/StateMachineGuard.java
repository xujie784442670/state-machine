package cn.jasonone.sm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 状态机守卫注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StateMachineGuard {
    /**
     * 状态机编码
     * <pre>
     *     可配置为多个状态机编码,当配置多个状态机编码时,则表示多个状态机编码都生效.但是必须在 {@link StateMachineAdvice#machineCode()} 的范围内,否则不生效
     * </pre>
     *
     * @return
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
}
