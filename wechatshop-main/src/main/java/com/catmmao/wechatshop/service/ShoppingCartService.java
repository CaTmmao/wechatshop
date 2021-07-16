package com.catmmao.wechatshop.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.generated.GoodsMapper;
import com.catmmao.wechatshop.generated.ShoppingCartMapper;
import com.catmmao.wechatshop.dao.mapper.ShoppingCartQueryMapper;
import com.catmmao.wechatshop.api.exception.HttpException;
import com.catmmao.wechatshop.model.GoodsWithNumber;
import com.catmmao.wechatshop.generated.Goods;
import com.catmmao.wechatshop.generated.ShoppingCart;
import com.catmmao.wechatshop.generated.ShoppingCartExample;
import com.catmmao.wechatshop.model.response.PaginationResponseModel;
import com.catmmao.wechatshop.model.response.ShoppingCartResponseModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {
    private final SqlSessionFactory sqlSessionFactory;
    private final Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);
    private final GoodsService goodsService;
    private final ShoppingCartQueryMapper shoppingCartQueryMapper;
    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartService(SqlSessionFactory sqlSessionFactory, GoodsMapper goodsMapper,
                               GoodsService goodsService,
                               ShoppingCartQueryMapper shoppingCartQueryMapper,
                               ShoppingCartMapper shoppingCartMapper) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.goodsService = goodsService;
        this.shoppingCartQueryMapper = shoppingCartQueryMapper;
        this.shoppingCartMapper = shoppingCartMapper;
    }

    /**
     * 删除当前用户购物车中指定的商品
     *
     * @param goodsId 要删除的商品ID
     * @return 更新后的该店铺物品列表
     */
    public ShoppingCartResponseModel deleteGoodsInShoppingCart(long goodsId) {
        long userId = UserContext.getCurrentUser().getId();

        // 从数据库获取该商品
        ShoppingCartExample example = new ShoppingCartExample();
        example.createCriteria().andUserIdEqualTo(userId).andGoodsIdEqualTo(goodsId);
        List<ShoppingCart> selectedDataFromDb = shoppingCartMapper.selectByExample(example);
        if (selectedDataFromDb.isEmpty()) {
            throw HttpException.resourceNotFound("找不到该商品");
        }

        // 在数据库将该商品的 status 更新为 'delete'
        shoppingCartQueryMapper.deleteShoppingCartByUserIdAndGoodsId(userId, goodsId);

        // 从数据库获取该店铺已加入购物车的商品
        long shopId = selectedDataFromDb.get(0).getShopId();
        return getShoppingCartByUserIdAndShopId(userId, shopId);
    }

    /**
     * 添加商品到购物车
     *
     * @param goodsList 需要添加的商品列表
     * @return 已添加进购物车的该店铺的商品列表
     */
    public ShoppingCartResponseModel addGoodsToShoppingCart(ShoppingCartResponseModel goodsList) {
        List<GoodsWithNumber> goodsListOnlyHaveGoodsIdAndNumberParam = goodsList.getGoods();

        List<Long> goodsIdList = goodsListOnlyHaveGoodsIdAndNumberParam
            .stream()
            .map(Goods::getId)
            .collect(Collectors.toList());

        if (goodsIdList.size() != goodsListOnlyHaveGoodsIdAndNumberParam.size()) {
            throw HttpException.badRequest("所有商品都需要传入商品ID");
        }

        // 根据所有商品的 id 从数据库获取商品信息
        List<Goods> goodsListInDb = goodsService.getGoodsListByGoodsIdList(goodsIdList);

        // 查看前端传入的所有的商品ID是否都能在数据库中查到
        if (goodsListInDb.size() != goodsIdList.size()) {
            String goodsListInDbToString = objectToReadableString(goodsListInDb);
            logger.debug("商品ID不存在在数据库中！商品ID列表：{}；可查到的商品列表：{}", goodsIdList, goodsListInDbToString);
            throw HttpException.badRequest("商品ID不存在在数据库中！可查到的商品有" + goodsListInDbToString);
        }

        // 检查所有商品是否属于同一店铺
        Set<Long> shopIdList = goodsListInDb.stream().map(Goods::getShopId).collect(Collectors.toSet());
        if (shopIdList.size() != 1) {
            String goodsListInDbToString = objectToReadableString(goodsListInDb);
            logger.debug("商品不属于同一店铺！商品ID列表：{}；商品列表：{}", goodsIdList, goodsListInDbToString);
            throw HttpException.badRequest("商品必须属于同一店铺");
        }

        // 将商品信息转化成购物车表的结构(这里可以让前端把商品的其他信息传进来吗，这样就不用自己合并了)
        long shopId = shopIdList.iterator().next();
        long userId = UserContext.getCurrentUser().getId();
        List<ShoppingCart> shoppingCartRows = goodsListOnlyHaveGoodsIdAndNumberParam
            .stream()
            .map(goods -> {
                long goodsId = goods.getId();
                ShoppingCart newGoods = new ShoppingCart();
                newGoods.setGoodsId(goodsId);
                newGoods.setNumber(goods.getNumber());
                newGoods.setShopId(shopId);
                newGoods.setUserId(userId);

                return newGoods;
            })
            .collect(Collectors.toList());

        // 添加到数据库的购物车表单中（使用 jdbc batch 方式）
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            shoppingCartRows.forEach(mapper::insertSelective);
            sqlSession.commit();
        }

        return getShoppingCartByUserIdAndShopId(userId, shopId);
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
            .selectShoppingCartDataListByUserId(userId, offset, pageSize)
            .stream()
            .collect(Collectors.groupingBy(item -> item.getShop().getId()))
            .values()
            .stream()
            .map(this::mergeMultiGoodsListFromSameShopToSingleMap)
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
    public ShoppingCartResponseModel mergeMultiGoodsListFromSameShopToSingleMap(
        List<ShoppingCartResponseModel> sameShopList) {
        List<GoodsWithNumber> goodsList = sameShopList
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

    /**
     * 获取购物车内指定店铺的商品列表，并将返回内容合并为一个对象
     *
     * @param userId 用户ID
     * @param shopId 店铺ID
     * @return 购物车内指定店铺的商品列表
     */
    public ShoppingCartResponseModel getShoppingCartByUserIdAndShopId(long userId, long shopId) {
        List<ShoppingCartResponseModel> dataList =
            shoppingCartQueryMapper.selectShoppingCartDataByUserIdAndShopId(userId, shopId);
        return dataList.isEmpty() ? null : mergeMultiGoodsListFromSameShopToSingleMap(dataList);
    }

    /**
     * 将对象转为可读的字符串
     *
     * @param object 待转换的对象
     * @return 字符串
     */
    public String objectToReadableString(Object object) {
        String result = null;
        try {
            result = new ObjectMapper()
                .writer()
                .withDefaultPrettyPrinter()
                .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
