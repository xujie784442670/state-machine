package cn.jasonone.sm.domain;

import cn.jasonone.sm.support.Event;
import cn.jasonone.sm.support.State;
import lombok.Builder;

/**
 * 状态机转换
 *
 * @param from  源状态
 * @param event 事件
 * @param to    目标状态
 */
@Builder
public record Transition(State from, Event event, State to) {
    /**
     * 获取转换规则的key
     *
     * @return
     */
    public String key() {
        return from.getState() + ":" + event.getCode();
    }
}
