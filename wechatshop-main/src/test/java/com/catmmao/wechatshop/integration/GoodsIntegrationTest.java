package com.catmmao.wechatshop.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.catmmao.wechatshop.WechatshopApplication;
import com.catmmao.wechatshop.model.generated.Goods;
import com.catmmao.wechatshop.model.generated.Shop;
import com.catmmao.wechatshop.model.response.CommonResponseModel;
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
public class GoodsIntegrationTest extends AbstractIntegrationTest {
    private String goodsApiUrl;
    private String shopApiUrl;

    private Goods goods = new Goods();
    private Shop shop = new Shop();

    {
        // shop 和 goods 初始化
        shop.setName("ssas");

        goods.setName("肥皂");
        goods.setPrice(500L);
        goods.setStock(10);
    }

    @BeforeEach
    void setup() {
        goodsApiUrl = getUrl("/goods");
        shopApiUrl = getUrl("/shop");
    }

    @Test
    public void createGoods() {
        // 1.登录
        afterLoginReturnSessionIdAndUserInfo();

        // 2.创建店铺
        shop.setOwnerUserId(userInfo.getId());
        ResponseEntity<CommonResponseModel<Shop>> response = doHttpRequest(
            shopApiUrl,
            HttpMethod.POST,
            shop,
            new ParameterizedTypeReference<CommonResponseModel<Shop>>() {
            });

        Shop shopData = response.getBody().getData();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(shop.getName(), shopData.getName());
        assertEquals(shop.getOwnerUserId(), shopData.getOwnerUserId());

        // 3.在创建好的店铺下创建商品
        goods.setShopId(shopData.getId());
        ResponseEntity<CommonResponseModel<Goods>> response2 = doHttpRequest(
            goodsApiUrl,
            HttpMethod.POST,
            goods,
            new ParameterizedTypeReference<CommonResponseModel<Goods>>() {
            });

        Goods goodsData = response2.getBody().getData();
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());
        assertEquals(goods.getName(), goodsData.getName());
        assertEquals(goods.getPrice(), goodsData.getPrice());
        assertEquals(goods.getStock(), goodsData.getStock());
        assertEquals(goods.getShopId(), goodsData.getShopId());
    }

    @Test
    public void deleteGoods() {
        //// arrange
        //HttpHeaders headers = new HttpHeaders();
        //headers.add("Cookie", sessionId);
        //HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);
        //
        //// act
        //ResponseEntity<CommonResponseModel<Goods>> response = restTemplate.exchange(
        //    goodsApiUrl + '/' + goods.getId(),
        //    HttpMethod.DELETE,
        //    requestEntity,
        //    new ParameterizedTypeReference<CommonResponseModel<Goods>>() {
        //    });
        //
        //// assert
        //
        //

    }
}
