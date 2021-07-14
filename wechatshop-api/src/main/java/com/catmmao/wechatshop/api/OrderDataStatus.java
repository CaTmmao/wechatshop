package com.catmmao.wechatshop.api;

/**
 * 订单状态
 */
public enum OrderDataStatus {
    // 待付款
    pending,
    // 已付款
    paid,
    // 物流中
    delivered,
    // 已收获
    received
}
