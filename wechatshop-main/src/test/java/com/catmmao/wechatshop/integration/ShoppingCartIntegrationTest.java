package com.catmmao.wechatshop.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.catmmao.wechatshop.WechatshopApplication;
import com.catmmao.wechatshop.generated.Goods;
import com.catmmao.wechatshop.model.GoodsWithNumber;
import com.catmmao.wechatshop.model.response.CommonResponseModel;
import com.catmmao.wechatshop.model.response.PaginationResponseModel;
import com.catmmao.wechatshop.model.response.ShoppingCartResponseModel;
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
class ShoppingCartIntegrationTest extends AbstractIntegrationTest {
    private String shoppingCartUrl;

    @BeforeEach
    void setup() {
        shoppingCartUrl = getUrl("/shopping_cart");
        // 登录
        afterLoginReturnSessionIdAndUserInfo();
    }

    @Test
    public void getUserShoppingCartListByUserId() {
        String url = shoppingCartUrl + "?pageNum=2&pageSize=1";
        ResponseEntity<PaginationResponseModel<ShoppingCartResponseModel>> response = doHttpRequest(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<PaginationResponseModel<ShoppingCartResponseModel>>() {
            });

        PaginationResponseModel<ShoppingCartResponseModel> responseBody = response.getBody();
        assert responseBody != null;
        final List<ShoppingCartResponseModel> data = responseBody.getData();

        assertEquals(2, responseBody.getTotalPage());
        assertEquals(1, responseBody.getPageSize());
        assertEquals(2, responseBody.getPageNum());
        assertEquals(1, data.size());
        assertEquals(2, data.get(0).getShop().getId());
        assertEquals(Arrays.asList(4L, 5L),
                     data.get(0)
                         .getGoods()
                         .stream()
                         .map(Goods::getId)
                         .collect(Collectors.toList()));
        assertEquals(Arrays.asList(200, 300),
                     data.get(0)
                         .getGoods()
                         .stream()
                         .map(GoodsWithNumber::getNumber)
                         .collect(Collectors.toList()));
    }

    @Test
    public void addGoodsToShoppingCart() {
        GoodsWithNumber goods = new GoodsWithNumber();
        goods.setId(1L);
        goods.setNumber(100);

        ShoppingCartResponseModel requestBody = new ShoppingCartResponseModel();
        requestBody.setGoods(Collections.singletonList(goods));

        ResponseEntity<CommonResponseModel<ShoppingCartResponseModel>> response = doHttpRequest(
            shoppingCartUrl,
            HttpMethod.POST,
            requestBody,
            new ParameterizedTypeReference<CommonResponseModel<ShoppingCartResponseModel>>() {
            });

        ShoppingCartResponseModel data = response.getBody().getData();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, data.getShop().getId());
        assertEquals(100, data
            .getGoods()
            .stream()
            .filter(item -> item.getId() == 1L)
            .findFirst()
            .get().getNumber()
        );
    }

    @Test
    public void deleteGoodsInShoppingCart() {
        ResponseEntity<CommonResponseModel<ShoppingCartResponseModel>> response = doHttpRequest(
            shoppingCartUrl + "/4", HttpMethod.DELETE, null,
            new ParameterizedTypeReference<CommonResponseModel<ShoppingCartResponseModel>>() {
            });

        ShoppingCartResponseModel data = response.getBody().getData();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, data.getGoods().size());
        assertEquals(2, data.getShop().getId());
        assertEquals(5, data.getGoods().get(0).getId());
    }
}