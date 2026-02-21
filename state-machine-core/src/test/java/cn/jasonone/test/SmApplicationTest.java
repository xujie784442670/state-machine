package cn.jasonone.test;

import cn.jasonone.sm.config.AutoStateMachineConfiguration;
import cn.jasonone.sm.domain.Transition;
import cn.jasonone.sm.support.StateMachineEngine;
import cn.jasonone.sm.support.StateRegistry;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

@SpringBootTest(classes = {AutoStateMachineConfiguration.class,TestHandler.class})
public class SmApplicationTest {

    @Resource
    private StateRegistry registry;
    @Resource
    private StateMachineEngine engine;

    @Test
    public void test() throws Exception {
        registry.setInitialState("test", ApprovalStatus.PENDING);
        registry.registerTransition("test", new Transition(ApprovalStatus.PENDING, ApprovalEvent.APPROVE, ApprovalStatus.APPROVED));
        registry.registerTransition("test", new Transition(ApprovalStatus.PENDING, ApprovalEvent.REJECT, ApprovalStatus.REJECTED));
        registry.registerTransition("test", new Transition(ApprovalStatus.APPROVED, ApprovalEvent.CANCEL, ApprovalStatus.CANCELED));


        engine.trigger("test", "1", ApprovalEvent.APPROVE);
    }
}
