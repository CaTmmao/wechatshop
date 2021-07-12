package com.catmmao.wechatshop.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.dao.mapper.generated.ShopMapper;
import com.catmmao.wechatshop.exception.HttpException;
import com.catmmao.wechatshop.model.DbDataStatus;
import com.catmmao.wechatshop.model.generated.Shop;
import com.catmmao.wechatshop.model.generated.User;
import com.catmmao.wechatshop.model.response.PaginationResponseModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {
    @Mock
    private ShopMapper shopMapper;
    @Mock
    private Shop shop;

    @InjectMocks
    private ShopService shopService;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1L);
        UserContext.setCurrentUser(user);
    }

    @AfterEach
    void clear() {
        UserContext.setCurrentUser(null);
    }

    @Test
    public void createShopSucceed() {
        // arrange
        when(shop.getName()).thenReturn("我的店铺");
        when(shopMapper.selectByExample(any())).thenReturn(Collections.emptyList());
        when(shopMapper.insertSelective(shop)).thenReturn(1);

        // act
        Shop result = shopService.createShop(shop);

        // assert
        assertEquals(shop, result);
        verify(shop).setId(1L);
    }

    @Test
    public void createShopThrowExceptionIfShopNameRepeat() {
        // arrange
        List<Shop> mockList = mock(List.class);
        when(shop.getName()).thenReturn("我的店铺");
        when(shopMapper.selectByExample(any())).thenReturn(mockList);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> shopService.createShop(shop));
        assertEquals("店铺名重复", e.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    }

    @Test
    public void updateShopSucceed() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId());
        when(shopMapper.updateByPrimaryKeySelective(shop)).thenReturn(1);

        // act && assert
        assertDoesNotThrow(() -> shopService.updateShop(shop));
    }

    @Test
    public void updateShopThrowExceptionIfShopNotFound() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(null);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> shopService.updateShop(shop));
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals("找不到该店铺", e.getMessage());
    }

    @Test
    public void updateShopThrowExceptionIfUserNotOwner() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId() + 1L);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> shopService.updateShop(shop));
        assertEquals("店铺不属于该用户", e.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, e.getHttpStatus());
    }

    @Test
    public void deleteShopSucceed() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId());
        when(shopMapper.updateByPrimaryKeySelective(shop)).thenReturn(1);

        // act && assert
        assertEquals(shop, shopService.deleteShop(anyLong()));
        verify(shop).setStatus(DbDataStatus.DELETE.getName());
    }

    @Test
    public void deleteShopThrowExceptionIfShopNotFound() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(null);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> shopService.deleteShop(anyLong()));
        assertEquals("找不到该店铺", e.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }

    @Test
    public void deleteShopThrowExceptionIfUserNotOwner() {
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId() + 1L);

        HttpException e = assertThrows(HttpException.class, () -> shopService.deleteShop(anyLong()));
        assertEquals("店铺不属于该用户", e.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, e.getHttpStatus());
    }

    @Test
    public void getShopListSucceed() {
        Integer pageNum = 1;
        Integer pageSize = 10;
        List<Shop> mockList = mock(List.class);
        when(shopMapper.countByExample(any())).thenReturn(32L);
        when(shopMapper.selectByExample(any())).thenReturn(mockList);

        PaginationResponseModel<Shop> result = shopService.getMyShopListByUserId(pageNum, pageSize);

        assertEquals(pageNum, result.getPageNum());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(4, result.getTotalPage());
        assertEquals(mockList, result.getData());
    }

    @Test
    public void getShopByShopIdSucceed() {
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        assertEquals(shop, shopService.getShopByShopId(anyLong()));
    }

    @Test
    public void getShopByShopIdThrowExceptionIfShopNotFound() {
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(null);
        HttpException e = assertThrows(HttpException.class, () -> shopService.getShopByShopId(anyLong()));
        assertEquals("店铺未找到", e.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }
}