package com.catmmao.wechatshop.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.catmmao.wechatshop.api.data.GoodsOnlyContainGoodsIdAndNumber;
import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.generated.GoodsMapper;
import com.catmmao.wechatshop.generated.ShopMapper;
import com.catmmao.wechatshop.api.exception.HttpException;
import com.catmmao.wechatshop.api.data.DbDataStatus;
import com.catmmao.wechatshop.generated.Goods;
import com.catmmao.wechatshop.generated.GoodsExample;
import com.catmmao.wechatshop.generated.Shop;
import com.catmmao.wechatshop.model.response.PaginationResponse;
import org.springframework.stereotype.Service;
import com.catmmao.wechatshop.model.GoodsWithNumber;

@Service
public class GoodsService {
    private final GoodsMapper goodsMapper;
    private final ShopMapper shopMapper;


    public GoodsService(GoodsMapper goodsMapper,
                        ShopMapper shopMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
    }

    /**
     * 创建商品
     *
     * @param goods 商品信息
     * @return 创建好的商品信息
     */
    public Goods createGoods(Goods goods) {
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());

        Long userId = UserContext.getCurrentUser().getId();
        if (shop == null || !userId.equals(shop.getOwnerUserId())) {
            throw HttpException.forbidden("店铺不属于该用户");
        } else {
            long id = goodsMapper.insertSelective(goods);
            goods.setId(id);
            return goods;
        }
    }

    /**
     * 删除商品
     *
     * @param goodsId 商品 id
     * @return 已删除的商品信息
     */
    public Goods deleteGoodsByGoodsId(Long goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);

        if (goods == null) {
            throw HttpException.resourceNotFound("商品未找到！");
        }

        checkUserIfShopOwner(goods.getShopId());

        goods.setStatus(DbDataStatus.DELETE.getName());
        goodsMapper.updateByPrimaryKey(goods);
        return goods;
    }

    /**
     * 获取所有商品
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @param shopId   店铺ID，若传递，则只显示该店铺中的商品
     * @return 所有商品
     */
    public PaginationResponse<Goods> getGoodsByShopId(
        Integer pageNum, Integer pageSize, Integer shopId) {
        // 商品总数量
        int totalNumber = countGoods(shopId);
        // 商品总页数
        int totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : totalNumber / pageSize + 1;

        // 获取所有商品
        GoodsExample page = new GoodsExample();
        page.setLimit(pageSize);
        page.setOffset((pageNum - 1) * pageSize);
        List<Goods> goodsList = goodsMapper.selectByExample(page);

        return new PaginationResponse<>(pageSize, pageNum, totalPage, goodsList);
    }

    /**
     * 计算商品总数量
     *
     * @param shopId 店铺 id
     * @return 商品总数量
     */
    public int countGoods(Integer shopId) {
        GoodsExample goodsExample = new GoodsExample();

        // 没有商店 id,获取所有商店的商品
        if (shopId == null) {
            goodsExample.createCriteria().andStatusEqualTo(DbDataStatus.OK.getName());
        } else {
            goodsExample.createCriteria()
                .andStatusEqualTo(DbDataStatus.OK.getName())
                .andShopIdEqualTo(shopId.longValue());
        }

        return (int) goodsMapper.countByExample(goodsExample);
    }

    /**
     * 更新商品
     *
     * @param goods 商品信息
     * @return 更新后的商品信息
     */
    public Goods updateGoods(Goods goods) {
        checkUserIfShopOwner(goods.getShopId());

        int affectedRecord = goodsMapper.updateByPrimaryKeySelective(goods);
        if (affectedRecord == 0) {
            throw HttpException.resourceNotFound("找不到该商品");
        }

        return goods;
    }

    /**
     * 获取指定ID的商品
     *
     * @param goodsId 商品ID
     * @return 商品信息
     */
    public Goods getGoodsByGoodsId(long goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);

        if (goods == null) {
            throw HttpException.resourceNotFound("找不到该商品");
        } else {
            return goods;
        }
    }

    /**
     * 检查用户是否是店铺的拥有者
     *
     * @param shopId 店铺ID
     */
    public void checkUserIfShopOwner(Long shopId) {
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        Long userId = UserContext.getCurrentUser().getId();
        if (!userId.equals(shop.getOwnerUserId())) {
            throw HttpException.forbidden("店铺不属于该用户");
        }
    }

    /**
     * 根据商品ID数组获取商品列表
     *
     * @param goodsIdList 商品ID数组
     * @return 商品列表
     */
    public List<Goods> getGoodsListByGoodsIdList(List<Long> goodsIdList) {
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.createCriteria().andIdIn(goodsIdList);
        return goodsMapper.selectByExample(goodsExample);
    }

    /**
     * 将 Goods 和数量合并到一起
     *
     * @param mapOfGoodsIdToGoodsInDb          map, 商品ID到商品信息的映射
     * @param goodsOnlyContainGoodsIdAndNumber 只包括商品数量和商品ID
     * @return 合并后的商品信息
     */
    public GoodsWithNumber combineGoodsAndNumber(Map<Long, Goods> mapOfGoodsIdToGoodsInDb,
                                                 GoodsOnlyContainGoodsIdAndNumber goodsOnlyContainGoodsIdAndNumber) {
        long goodsId = goodsOnlyContainGoodsIdAndNumber.getGoodsId();
        int number = goodsOnlyContainGoodsIdAndNumber.getNumber();
        GoodsWithNumber result = null;

        Goods goodsInDb = mapOfGoodsIdToGoodsInDb.get(goodsId);
        if (goodsInDb != null) {
            result = new GoodsWithNumber(goodsInDb);
            result.setNumber(number);
        }

        return result;
    }

    /**
     * 将商品列表转换为通过商品ID映射到商品信息的 map
     *
     * @param goodsList 商品信息列表
     * @return map，商品ID映射到商品信息
     */
    public Map<Long, Goods> generateMapOfGoodsIdToGoods(List<Goods> goodsList) {
        return goodsList
            .stream()
            .collect(Collectors.toMap(Goods::getId, goods -> goods));
    }
}
