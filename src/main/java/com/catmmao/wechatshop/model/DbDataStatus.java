package com.catmmao.wechatshop.model;

/**
 * 数据库中数据的状态
 */
public enum DbDataStatus {
    // 正常
    OK(),
    // 已删除(逻辑删除)
    DELETE();

    public String getName() {
        return name().toLowerCase();
    }
}
