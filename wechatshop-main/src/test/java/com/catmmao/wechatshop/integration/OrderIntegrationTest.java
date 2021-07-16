package com.catmmao.wechatshop.integration;


import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.catmmao.wechatshop.WechatshopApplication;
import com.catmmao.wechatshop.api.data.GoodsOnlyContainGoodsIdAndNumber;
import com.catmmao.wechatshop.api.data.OrderInfo;
import com.catmmao.wechatshop.generated.Goods;
import com.catmmao.wechatshop.model.GoodsWithNumber;
import com.catmmao.wechatshop.model.response.CommonResponseModel;
import com.catmmao.wechatshop.model.response.OrderResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechatshopApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderIntegrationTest extends AbstractIntegrationTest {
    private String orderUrl;

    @BeforeEach
    void setup() {
        orderUrl = getUrl("/order");
        afterLoginReturnSessionIdAndUserInfo();
    }

    /**
     * 成功创建订单
     */
    @Test
    public void createOrderSucceed() {
        GoodsOnlyContainGoodsIdAndNumber goods1 = new GoodsOnlyContainGoodsIdAndNumber();
        GoodsOnlyContainGoodsIdAndNumber goods2 = new GoodsOnlyContainGoodsIdAndNumber();
        goods1.setGoodsId(1);
        goods1.setNumber(4);
        goods2.setGoodsId(2);
        goods2.setNumber(5);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setGoodsOnlyContainGoodsIdAndNumbers(Arrays.asList(goods1, goods2));

        ResponseEntity<OrderResponse> response = doHttpRequest(
            orderUrl,
            HttpMethod.POST,
            orderInfo,
            new ParameterizedTypeReference<OrderResponse>() {
            });

        OrderResponse body = response.getBody();
        assert body != null;

        Assertions.assertEquals(1L, body.getId());
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(1L, body.getShop().getId());
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                                body.getGoods()
                                    .stream()
                                    .map(Goods::getId)
                                    .collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(4, 5),
                                body.getGoods()
                                    .stream()
                                    .map(GoodsWithNumber::getNumber)
                                    .collect(Collectors.toList()));
    }

    /**
     * 当库存扣减失败时，订单创建失败，返回 410 错误
     */
    @Test
    public void createOrderFailedIfDeductStockFail() {
        GoodsOnlyContainGoodsIdAndNumber goods1 = new GoodsOnlyContainGoodsIdAndNumber();
        GoodsOnlyContainGoodsIdAndNumber goods2 = new GoodsOnlyContainGoodsIdAndNumber();
        goods1.setGoodsId(1);
        goods1.setNumber(2);
        goods2.setGoodsId(2);
        goods2.setNumber(6);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setGoodsOnlyContainGoodsIdAndNumbers(Arrays.asList(goods1, goods2));

        ResponseEntity<CommonResponseModel<?>> response = doHttpRequest(
            orderUrl,
            HttpMethod.POST,
            orderInfo,
            new ParameterizedTypeReference<CommonResponseModel<?>>() {
            });

        Assertions.assertEquals(HttpStatus.GONE, response.getStatusCode());
        Assertions.assertEquals("扣减库存失败", Objects.requireNonNull(response.getBody()).getMessage());

        // 确保上面的库存扣减失败后已回滚
        createOrderSucceed();
    }

    // 成功删除订单
    @Test
    public void deleteOrderSucceed() {
        createOrderSucceed();

        ResponseEntity<OrderResponse> response =
            doHttpRequest(orderUrl + "/1", HttpMethod.DELETE, null, new ParameterizedTypeReference<OrderResponse>() {
            });

        OrderResponse body = response.getBody();
        Assertions.assertEquals(1L, body.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(1L, body.getShop().getId());
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                                body.getGoods()
                                    .stream()
                                    .map(Goods::getId)
                                    .collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(4, 5),
                                body.getGoods()
                                    .stream()
                                    .map(GoodsWithNumber::getNumber)
                                    .collect(Collectors.toList()));
    }
}
