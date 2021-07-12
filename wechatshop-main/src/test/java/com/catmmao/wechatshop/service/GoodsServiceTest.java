package com.catmmao.wechatshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.dao.mapper.generated.GoodsMapper;
import com.catmmao.wechatshop.dao.mapper.generated.ShopMapper;
import com.catmmao.wechatshop.exception.HttpException;
import com.catmmao.wechatshop.model.DbDataStatus;
import com.catmmao.wechatshop.model.generated.Goods;
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
class GoodsServiceTest {
    @Mock
    private ShopMapper shopMapper;

    @Mock
    private GoodsMapper goodsMapper;

    @InjectMocks
    private GoodsService goodsService;

    @Mock
    private Shop shop;

    @Mock
    private Goods goods;

    @BeforeEach
    public void setup() {
        initUserContext();
    }

    public void initUserContext() {
        User user = new User();
        user.setId(1L);
        UserContext.setCurrentUser(user);
    }

    @AfterEach
    public void clearUserContext() {
        UserContext.setCurrentUser(null);
    }

    @Test
    public void createGoodsSucceed() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId());
        when(goodsMapper.insertSelective(goods)).thenReturn(1);

        // act && assert
        assertEquals(goods, goodsService.createGoods(goods));
        verify(goods).setId(1L);
    }

    @Test
    public void createGoodsThrowExceptionIfUserNotOwner() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId() + 1L);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> goodsService.createGoods(goods));
        assertEquals(HttpStatus.FORBIDDEN, e.getHttpStatus());
    }

    @Test
    public void deleteGoodsSucceed() {
        // arrange
        when(goodsMapper.selectByPrimaryKey(anyLong())).thenReturn(goods);
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId());
        when(goodsMapper.updateByPrimaryKey(goods)).thenReturn(anyInt());

        // act  && assert
        assertEquals(goods, goodsService.deleteGoodsByGoodsId(1L));
        verify(goods).setStatus(DbDataStatus.DELETE.getName());
    }

    @Test
    public void deleteGoodsThrowExceptionIfGoodsNotFound() {
        // arrange
        when(goodsMapper.selectByPrimaryKey(anyLong())).thenReturn(null);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> goodsService.deleteGoodsByGoodsId(anyLong()));
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }

    @Test
    public void deleteGoodsThrowExceptionIfUserIsNotOwner() {
        // arrange
        when(goodsMapper.selectByPrimaryKey(anyLong())).thenReturn(goods);
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId() + 1L);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> goodsService.deleteGoodsByGoodsId(anyLong()));
        assertEquals(HttpStatus.FORBIDDEN, e.getHttpStatus());
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @Test
    public void getGoodsSucceedWithNullShopId() {
        // arrange
        Integer pageNum = 1;
        Integer pageSize = 10;
        List<Goods> mockData = mock(List.class);
        when(goodsMapper.countByExample(any())).thenReturn(32L);
        when(goodsMapper.selectByExample(any())).thenReturn(mockData);

        // act
        PaginationResponseModel<Goods> result = goodsService.getGoodsByShopId(pageNum, pageSize, null);

        // assert
        assertEquals(4, result.getTotalPage());
        assertEquals(pageNum, result.getPageNum());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockData, result.getData());
    }

    @Test
    public void getGoodsSucceedWithNonNullShopId() {
        // arrange
        Integer pageNum = 1;
        Integer pageSize = 10;
        List<Goods> mockData = mock(List.class);
        when(goodsMapper.countByExample(any())).thenReturn(32L);
        when(goodsMapper.selectByExample(any())).thenReturn(mockData);

        // act
        PaginationResponseModel<Goods> result = goodsService.getGoodsByShopId(pageNum, pageSize, 123);

        // assert
        assertEquals(4, result.getTotalPage());
        assertEquals(pageNum, result.getPageNum());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockData, result.getData());
    }

    @Test
    public void updateGoodsSucceed() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId());
        when(goodsMapper.updateByPrimaryKeySelective(goods)).thenReturn(1);

        // act && assert
        assertEquals(goods, goodsService.updateGoods(goods));
    }

    @Test
    public void updateGoodsThrowExceptionIfGoodsNotFound() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId());
        when(goodsMapper.updateByPrimaryKeySelective(goods)).thenReturn(0);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> goodsService.updateGoods(goods));
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    }

    @Test
    public void updateGoodsThrowExceptionIfUserIsNotOwner() {
        // arrange
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(UserContext.getCurrentUser().getId() + 1L);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> goodsService.updateGoods(goods));
        assertEquals(HttpStatus.FORBIDDEN, e.getHttpStatus());
    }

    @Test
    public void getGoodsByGoodsIdSucceed() {
        // arrange
        when(goodsMapper.selectByPrimaryKey(anyLong())).thenReturn(goods);

        // act && assert
        assertEquals(goods, goodsService.getGoodsByGoodsId(anyLong()));
    }

    @Test
    public void getGoodsByGoodsIdThrowExceptionIfGoodsNotFound() {
        // arrange
        when(goodsMapper.selectByPrimaryKey(anyLong())).thenReturn(null);

        // act && assert
        HttpException e = assertThrows(HttpException.class, () -> goodsService.getGoodsByGoodsId(anyLong()));
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals("找不到该商品", e.getMessage());
    }
}