package cn.jasonone.test;

import cn.jasonone.sm.support.State;
import lombok.Getter;

public enum ApprovalStatus implements State {
    PENDING("PENDING", "待处理"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "被拒绝"),
    CANCELED("CANCELED", "已取消"),
    ;
    @Getter
    private final String state;
    @Getter
    private final String desc;

    ApprovalStatus(String state, String desc) {
        this.state = state;
        this.desc = desc;
    }
}
