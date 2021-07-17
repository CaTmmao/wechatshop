package com.catmmao.wechatshop.api.data;

/**
 * 数据库中数据的状态
 */
public enum DbDataStatus {
    // 正常
    OK(),
    // 已删除(逻辑删除)
    DELETE(),

    /**
     * 专用于 order
     */
    // 待付款
    PENDING(),
    // 已付款
    PAID(),
    // 物流中
    DELIVERED(),
    // 已收货
    RECEIVED();

    /**
     * 查看枚举是否存在
     *
     * @param name 需要检查的枚举名
     * @return 存在 true, 不存在 false
     */
    public static boolean ifEnumExist(String name) {
        try {
            DbDataStatus.valueOf(DbDataStatus.class, name.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getName() {
        return name().toLowerCase();
    }
}
