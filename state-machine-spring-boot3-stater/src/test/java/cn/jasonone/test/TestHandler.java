package cn.jasonone.test;

import cn.jasonone.sm.annotations.StateMachineAction;
import cn.jasonone.sm.annotations.StateMachineAdvice;
import cn.jasonone.sm.annotations.StateMachineGuard;
import cn.jasonone.sm.domain.StateContext;
import cn.jasonone.sm.support.StateMachineEngine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@StateMachineAdvice
public class TestHandler {

    @StateMachineGuard
    public boolean guardTestAll(StateMachineEngine engine, StateContext ctx){
        log.info("[guardTestAll] 状态机[{}] 守卫[{}]", ctx.machineCode(), ctx.businessId());
        return true;
    }
    @StateMachineGuard(machineCode = "test")
    public boolean guardTest(StateMachineEngine engine, StateContext ctx){
        log.info("[guardTest] 状态机[{}] 守卫[{}]", ctx.machineCode(), ctx.businessId());
        return true;
    }
    @StateMachineGuard(machineCode = "test", sourceState = "PENDING", targetState = "APPROVED")
    public boolean guardTestApprove(StateMachineEngine engine, StateContext ctx){
        log.info("[guardTestApprove] 状态机[{}] 守卫[{}]", ctx.machineCode(), ctx.businessId());
        return true;
    }

    @StateMachineGuard(machineCode = "test", sourceState = "PENDING", targetState = "REJECT")
    public boolean guardTestReject(StateMachineEngine engine, StateContext ctx){
        log.info("[guardTestReject] 状态机[{}] 守卫[{}]", ctx.machineCode(), ctx.businessId());
        return true;
    }

    @StateMachineAction
    public void actionTest(StateMachineEngine engine, StateContext ctx){
        log.info("状态机[{}] 动作[{}]", ctx.machineCode(), ctx.businessId());
    }
}
