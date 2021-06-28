package com.catmmao.wechatshop.service;

import java.util.List;
import java.util.stream.Collectors;

import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.dao.mapper.ShoppingCartQueryMapper;
import com.catmmao.wechatshop.model.response.PaginationResponseModel;
import com.catmmao.wechatshop.model.response.ShoppingCartGoodsModel;
import com.catmmao.wechatshop.model.response.ShoppingCartResponseModel;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {
    private final ShoppingCartQueryMapper shoppingCartQueryMapper;

    public ShoppingCartService(ShoppingCartQueryMapper shoppingCartQueryMapper) {
        this.shoppingCartQueryMapper = shoppingCartQueryMapper;
    }

    /**
     * 获取当前用户名下的所有购物车物品
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @return 用户名下的所有购物车物品
     */
    public PaginationResponseModel<ShoppingCartResponseModel> getUserShoppingCart(int pageNum,
                                                                                  int pageSize) {
        long userId = UserContext.getCurrentUser().getId();
        int offset = (pageNum - 1) * pageSize;
        int totalNumber = shoppingCartQueryMapper.countHowManyShopsInUserShoppingCart(userId);
        int totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : totalNumber / pageSize + 1;

        List<ShoppingCartResponseModel> data = shoppingCartQueryMapper
            .selectShoppingCartDataByUserId(userId, offset, pageSize)
            .stream()
            .collect(Collectors.groupingBy(item -> item.getShop().getId()))
            .values()
            .stream()
            .map(this::mergeGoodsList)
            .collect(Collectors.toList());

        return new PaginationResponseModel<>(pageSize, pageNum, totalPage, data);
    }

    /**
     * 属于同一个店铺的数据，将他们的 goods 属性合并起来
     *
     * <pre>
     * [
     *     {
     *       "shop": {"id": 12345,"name": "我的店铺"...},
     *       "goods": [{"id": 12345,"name": "肥皂"...}]
     *     },
     *     {
     *       "shop": {"id": 12345,"name": "我的店铺"...},
     *       "goods": [{"id": 12345,"name": "肥皂"...}]
     *     },
     * ]
     * 将上面的数据合并起来，结果如下
     *  {
     *    "shop": {"id": 12345,"name": "我的店铺"...},
     *    "goods": [
     *          {"id": 12345,"name": "肥皂"...},
     *          {"id": 12345,"name": "肥皂"...}
     *    ]
     *  }
     *
     * </pre>
     *
     * @param sameShopList 店铺ID相同的购物车列表
     * @return 合并后的购物车列表
     */
    public ShoppingCartResponseModel mergeGoodsList(List<ShoppingCartResponseModel> sameShopList) {
        List<ShoppingCartGoodsModel> goodsList = sameShopList
            .stream()
            .map(ShoppingCartResponseModel::getGoods)
            // [{"id": 12345...}] 转为 {"id": 12345...}
            .flatMap(List::stream)
            .collect(Collectors.toList());

        ShoppingCartResponseModel result = new ShoppingCartResponseModel();
        result.setShop(sameShopList.get(0).getShop());
        result.setGoods(goodsList);
        return result;
    }
}
