package cn.jasonone.sm.core;

import cn.jasonone.sm.support.TransactionManagement;

/**
 * 默认事务管理空实现
 *
 * @apiNote 本实现类为默认事务实现, 默认情况下不进行事务处理
 */
public class DefaultTransactionManagement implements TransactionManagement {
    @Override
    public void begin() {

    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }
}
