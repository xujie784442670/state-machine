package cn.jasonone.sm.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记当前类中可配置状态的守卫方法和动作方法,支持被Spring管理
 * @see Component
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface StateMachineAdvice {
    @AliasFor(annotation = Component.class)
    String value() default "";

    /**
     * 状态机编码, 默认为*
     *
     * @return
     */
    String[] machineCode() default "*";
}
