package cn.jasonone.sm.exceptions;

import cn.hutool.core.util.StrUtil;

/**
 * 状态机异常
 */
public class StateMachineException extends RuntimeException {
    public StateMachineException() {
        super();
    }

    public StateMachineException(String message, Object... args) {
        super(StrUtil.format(message, args));
    }

    public StateMachineException(Throwable cause, String message, Object... args) {
        super(StrUtil.format(message, args), cause);
    }

    public StateMachineException(Throwable cause) {
        super(cause);
    }

    protected StateMachineException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String message, Object... args) {
        super(StrUtil.format(message, args), cause, enableSuppression, writableStackTrace);
    }
}
