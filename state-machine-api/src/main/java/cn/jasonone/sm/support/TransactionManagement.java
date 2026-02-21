package cn.jasonone.sm.support;

/**
 * 事务管理
 */
public interface TransactionManagement {
    /**
     * 开启事务
     */
    void begin();

    /**
     * 提交事务
     */
    void commit();

    /**
     * 回滚事务
     */
    void rollback();
}
