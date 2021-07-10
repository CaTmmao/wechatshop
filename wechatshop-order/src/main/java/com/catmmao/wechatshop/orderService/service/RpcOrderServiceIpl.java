package com.catmmao.wechatshop.orderService.service;

import com.catmmao.wechatshop.api.rpc.OrderService;
import org.apache.dubbo.config.annotation.DubboService;

// 本模块的版本号，在 application.yml 中定义
@DubboService(version = "${wechatshop.order.version}")
public class RpcOrderServiceIpl implements OrderService {
}
