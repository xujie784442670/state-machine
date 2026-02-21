package cn.jasonone.sm.domain;

import cn.jasonone.sm.support.Event;
import cn.jasonone.sm.support.State;

/**
 * 状态机上下文
 *
 * @param machineCode 状态机编码
 * @param businessId  业务ID
 * @param from        源状态
 * @param to          目标状态
 * @param event       事件
 * @param payload     数据
 */
public record StateContext(String machineCode,String businessId, State from, State to, Event event, Object payload) {
}
