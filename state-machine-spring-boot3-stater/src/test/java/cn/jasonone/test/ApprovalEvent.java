package cn.jasonone.test;

import cn.jasonone.sm.support.Event;
import lombok.Getter;

public enum ApprovalEvent implements Event {
    APPROVE("APPROVE", "审批通过"),
    REJECT("REJECT", "审批拒绝"),
    CANCEL("CANCEL", "取消审批"),
    ;
    @Getter
    private final String code;
    @Getter
    private final String name;

    ApprovalEvent(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
