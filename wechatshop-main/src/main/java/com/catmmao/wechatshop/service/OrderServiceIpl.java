package com.catmmao.wechatshop.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.api.data.GoodsOnlyContainGoodsIdAndNumber;
import com.catmmao.wechatshop.api.data.OrderInfo;
import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.api.rpc.OrderRpcService;
import com.catmmao.wechatshop.dao.mapper.GoodsStockMapper;
import com.catmmao.wechatshop.exception.HttpException;
import com.catmmao.wechatshop.generated.Goods;
import com.catmmao.wechatshop.generated.ShopMapper;
import com.catmmao.wechatshop.generated.UserMapper;
import com.catmmao.wechatshop.model.GoodsWithNumber;
import com.catmmao.wechatshop.model.response.OrderResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceIpl implements OrderService {
    private final UserMapper userMapper;
    private final ShopMapper shopMapper;
    private final GoodsService goodsService;
    private final GoodsStockMapper goodsStockMapper;
    private final Logger logger = LoggerFactory.getLogger(OrderServiceIpl.class);

    @DubboReference(version = "${wechatshop.order.version}")
    private OrderRpcService orderRpcService;

    public OrderServiceIpl(UserMapper userMapper,
                           ShopMapper shopMapper, GoodsService goodsService,
                           GoodsStockMapper goodsStockMapper) {
        this.userMapper = userMapper;
        this.shopMapper = shopMapper;
        this.goodsService = goodsService;
        this.goodsStockMapper = goodsStockMapper;
    }

    /**
     * 创建订单
     *
     * @param orderInfo 待创建订单中的商品列表信息（包括商品ID和商品数量）
     * @return 创建后完整的订单信息
     */
    @Transactional
    public OrderResponse createOrder(OrderInfo orderInfo) {
        List<GoodsOnlyContainGoodsIdAndNumber> listOfGoodsOnlyContainGoodsIdAndNumber =
            orderInfo.getGoodsOnlyContainGoodsIdAndNumbers();

        if (!deductStock(listOfGoodsOnlyContainGoodsIdAndNumber)) {
            throw HttpException.gone("扣减库存失败");
        }

        List<Goods> goodsListInDb = getGoodsListInDb(listOfGoodsOnlyContainGoodsIdAndNumber);
        Map<Long, Goods> mapOfGoodsIdToGoodsInDb = goodsService.generateMapOfGoodsIdToGoods(goodsListInDb);

        Order createdOrder = createOrderViaRpc(
            listOfGoodsOnlyContainGoodsIdAndNumber,
            mapOfGoodsIdToGoodsInDb);

        List<GoodsWithNumber> listOfGoodsWithNumber =
            combineListOfGoodsWithNumber(listOfGoodsOnlyContainGoodsIdAndNumber, mapOfGoodsIdToGoodsInDb);

        return generateCompleteOrderInfo(
            listOfGoodsWithNumber,
            createdOrder,
            goodsListInDb);
    }

    /**
     * 将两个数组合并成包含数量的商品列表
     *
     * @param listOfGoodsOnlyContainGoodsIdAndNumber list, 每个元素包含 goodsId 和 number
     * @param mapOfGoodsIdToGoodsInDb                map, goodsId 到 goods 的映射
     * @return 包含数量的商品列表
     */
    private List<GoodsWithNumber> combineListOfGoodsWithNumber(
        List<GoodsOnlyContainGoodsIdAndNumber> listOfGoodsOnlyContainGoodsIdAndNumber,
        Map<Long, Goods> mapOfGoodsIdToGoodsInDb) {
        return listOfGoodsOnlyContainGoodsIdAndNumber
            .stream()
            .map(item -> goodsService.combineGoodsAndNumber(mapOfGoodsIdToGoodsInDb, item))
            .collect(toList());
    }

    /**
     * 获取商品列表
     *
     * @param listOfGoodsOnlyContainGoodsIdAndNumber list, 每个元素包括商品ID和数量
     * @return 商品列表
     */
    private List<Goods> getGoodsListInDb(
        List<GoodsOnlyContainGoodsIdAndNumber> listOfGoodsOnlyContainGoodsIdAndNumber) {
        List<Long> goodsIdList = listOfGoodsOnlyContainGoodsIdAndNumber
            .stream()
            .map(GoodsOnlyContainGoodsIdAndNumber::getGoodsId)
            .collect(toList());

        return goodsService.getGoodsListByGoodsIdList(goodsIdList);
    }

    /**
     * 生成完整的订单信息
     *
     * @param listOfGoodsWithNumber list, 包括数量的商品信息
     * @param createdOrder          已在数据库创建好的订单信息
     * @param goodsListInDb         数据库中存储的商品列表
     * @return 完整的订单信息
     */
    private OrderResponse generateCompleteOrderInfo(
        List<GoodsWithNumber> listOfGoodsWithNumber,
        Order createdOrder,
        List<Goods> goodsListInDb) {

        OrderResponse result = new OrderResponse(createdOrder);
        result.setShop(shopMapper.selectByPrimaryKey(goodsListInDb.get(0).getShopId()));
        result.setGoods(listOfGoodsWithNumber);

        return result;
    }

    /**
     * 通过 RPC 创建订单
     *
     * @param listOfGoodsOnlyContainGoodsIdAndNumber list, 每个元素包含商品ID和数量
     * @param mapOfGoodsIdToGoodsInDb                map, goodsId 到 goods 的映射
     * @return 订单创建成功后的订单信息
     */
    private Order createOrderViaRpc(List<GoodsOnlyContainGoodsIdAndNumber> listOfGoodsOnlyContainGoodsIdAndNumber,
                                    Map<Long, Goods> mapOfGoodsIdToGoodsInDb) {
        long userId = UserContext.getCurrentUser().getId();

        Order order = new Order();
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setUserId(userId);
        order.setTotalPrice(calculateTotalPrice(listOfGoodsOnlyContainGoodsIdAndNumber, mapOfGoodsIdToGoodsInDb));
        return orderRpcService.createOrder(listOfGoodsOnlyContainGoodsIdAndNumber, order);
    }

    /**
     * 扣减商品库存
     *
     * @param goodsOnlyContainGoodsIdAndNumberList list,每个元素包含商品ID和数量
     * @return true 扣减成功；false 扣减失败
     */
    private boolean deductStock(List<GoodsOnlyContainGoodsIdAndNumber> goodsOnlyContainGoodsIdAndNumberList) {
        for (GoodsOnlyContainGoodsIdAndNumber item : goodsOnlyContainGoodsIdAndNumberList) {
            if (goodsStockMapper.deductStock(item) <= 0) {
                logger.error("商品库存扣减失败，商品id: " + item.getGoodsId() + "; 数量: " + item.getNumber());
                return false;
            }
        }

        return true;
    }

    /**
     * 计算订单中商品的总金额
     *
     * @param goodsList         商品列表
     * @param goodsIdToGoodsMap map，商品ID到商品信息的映射
     * @return 总金额
     */
    private long calculateTotalPrice(List<GoodsOnlyContainGoodsIdAndNumber> goodsList,
                                     Map<Long, Goods> goodsIdToGoodsMap) {
        return goodsList.stream()
            .mapToLong(item -> {
                long goodsId = item.getGoodsId();
                long number = item.getNumber();

                if (number <= 0) {
                    throw HttpException.badRequest("商品数量应该在 0 以上，goodsId 为 " + goodsId);
                }

                Goods goods = goodsIdToGoodsMap.get(goodsId);

                if (goods == null) {
                    throw HttpException.resourceNotFound("找不到该商品， goodsId 为 " + goodsId);
                }

                return goods.getPrice() * number;
            })
            .sum();
    }
}
