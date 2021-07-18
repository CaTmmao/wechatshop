package com.catmmao.wechatshop.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.api.converter.CommonConverter;
import com.catmmao.wechatshop.api.data.GoodsIdAndNumber;
import com.catmmao.wechatshop.api.data.OrderInfo;
import com.catmmao.wechatshop.api.data.PaginationResponse;
import com.catmmao.wechatshop.api.data.RpcOrderResponse;
import com.catmmao.wechatshop.api.exception.HttpException;
import com.catmmao.wechatshop.api.generated.Order;
import com.catmmao.wechatshop.api.generated.OrderGoodsMapping;
import com.catmmao.wechatshop.api.rpc.OrderRpcService;
import com.catmmao.wechatshop.dao.mapper.GoodsStockMapper;
import com.catmmao.wechatshop.generated.Goods;
import com.catmmao.wechatshop.generated.Shop;
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
    private final CommonConverter commonConverter;

    @DubboReference(version = "${wechatshop.order.version}")
    private OrderRpcService orderRpcService;

    public OrderServiceIpl(UserMapper userMapper,
                           ShopMapper shopMapper, GoodsService goodsService,
                           GoodsStockMapper goodsStockMapper,
                           CommonConverter commonConverter) {
        this.userMapper = userMapper;
        this.shopMapper = shopMapper;
        this.goodsService = goodsService;
        this.goodsStockMapper = goodsStockMapper;
        this.commonConverter = commonConverter;
    }

    /**
     * 创建订单
     *
     * @param orderInfo 待创建订单中的商品列表信息（包括商品ID和商品数量）
     * @return 创建后完整的订单信息
     */
    @Transactional
    public OrderResponse createOrder(OrderInfo orderInfo) {
        List<GoodsIdAndNumber> listOfGoodsIdAndNumber =
            orderInfo.getGoodsOnlyContainGoodsIdAndNumbers();

        if (!deductStock(listOfGoodsIdAndNumber)) {
            throw HttpException.gone("扣减库存失败");
        }

        List<Goods> goodsListInDb = goodsService.getGoodsListInDb(listOfGoodsIdAndNumber);
        Map<Long, Goods> mapOfGoodsIdToGoodsInDb = goodsService.generateMapOfGoodsIdToGoods(goodsListInDb);

        Order createdOrder = createOrderViaRpc(
            listOfGoodsIdAndNumber,
            mapOfGoodsIdToGoodsInDb);

        List<OrderGoodsMapping> listOfOrderGoodsMapping = commonConverter
            .listOfGoodsIdAndNumber2OrderGoodsMapping(listOfGoodsIdAndNumber);

        return generateOrderResponse(
            listOfOrderGoodsMapping,
            createdOrder);
    }

    /**
     * 删除订单
     *
     * @param orderId 订单ID
     * @return 刚刚删除的订单
     */
    @Transactional
    @Override
    public OrderResponse deleteOrder(long orderId) {
        long userId = UserContext.getCurrentUser().getId();
        RpcOrderResponse rpcOrderResponse = orderRpcService.deleteOrderByOrderId(orderId, userId);

        return generateOrderResponse(
            rpcOrderResponse.getGoodsList(),
            rpcOrderResponse.getOrder());
    }

    /**
     * 获取当前用户名下的所有订单
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @param status   订单状态：pending 待付款 paid 已付款 delivered 物流中 received 已收货
     * @return 订单列表
     */
    @Override
    public PaginationResponse<OrderResponse> getAllOrders(int pageNum, int pageSize, String status) {
        long userId = UserContext.getCurrentUser().getId();
        PaginationResponse<RpcOrderResponse> rpcOrderResponse =
            orderRpcService.getAllOrders(userId, pageNum, pageSize, status);
        List<RpcOrderResponse> listOfRpcOrderResponse = rpcOrderResponse.getData();
        List<OrderResponse> data = generateAllOrderInfoList(listOfRpcOrderResponse);
        PaginationResponse<OrderResponse> result = new PaginationResponse<>(rpcOrderResponse);
        result.setData(data);
        return result;
    }

    /**
     * 更新订单物流信息
     *
     * @param order 订单信息
     * @return 更新后的订单信息
     */
    @Override
    public OrderResponse updateExpressInfo(Order order) {
        long userId = UserContext.getCurrentUser().getId();
        long orderId = order.getId();

        Order orderInDb = orderRpcService.getOrderByOrderId(orderId);
        Shop shop = shopMapper.selectByPrimaryKey(orderInDb.getShopId());

        if (shop == null) {
            throw HttpException.resourceNotFound("店铺不存在");
        }

        if (shop.getOwnerUserId() != userId) {
            throw HttpException.forbidden("无权访问，只能由店铺所有者操作");
        }

        Order newOrder = new Order();
        newOrder.setId(orderId);
        newOrder.setStatus("delivered");
        newOrder.setExpressId(order.getExpressId());
        newOrder.setExpressCompany(order.getExpressCompany());
        RpcOrderResponse rpcOrderResponse = orderRpcService.updateOrder(newOrder);

        return generateOrderResponse(rpcOrderResponse.getGoodsList(), rpcOrderResponse.getOrder());
    }

    /**
     * 更新订单状态
     *
     * @param order 订单信息
     * @return 更新后的订单信息
     */
    @Override
    public OrderResponse updateOrderStatus(Order order) {
        long userId = UserContext.getCurrentUser().getId();
        long orderId = order.getId();
        Order orderInDb = orderRpcService.getOrderByOrderId(orderId);
        if (orderInDb.getUserId() != userId) {
            throw HttpException.forbidden("无权访问，只有订单所有者才能更新订单");
        }

        Order newOrder = new Order();
        order.setId(orderId);
        order.setStatus(order.getStatus());
        RpcOrderResponse rpcOrderResponse = orderRpcService.updateOrder(newOrder);

        return generateOrderResponse(rpcOrderResponse.getGoodsList(), rpcOrderResponse.getOrder());
    }

    /**
     * 生成完整的订单信息
     *
     * @param listOfOrderGoodsMapping list, orderGoodsMapping
     * @param createdOrder            已在数据库创建好的订单信息
     * @return 完整的订单信息
     */
    private OrderResponse generateOrderResponse(
        List<OrderGoodsMapping> listOfOrderGoodsMapping,
        Order createdOrder) {
        List<GoodsIdAndNumber> listOfGoodsIdAndNumber =
            commonConverter.listOfOrderGoodsMapping2GoodsIdAndNumber(listOfOrderGoodsMapping);
        List<GoodsWithNumber> listOfGoodsWithNumber =
            generateListOfGoodsWithNumber(listOfGoodsIdAndNumber);

        OrderResponse result = new OrderResponse(createdOrder);
        result.setGoodsWithNumberList(listOfGoodsWithNumber);
        result.setShop(shopMapper.selectByPrimaryKey(listOfGoodsWithNumber.get(0).getShopId()));

        return result;
    }

    /**
     * 通过 RPC 创建订单
     *
     * @param listOfGoodsIdAndNumber  list, 每个元素包含商品ID和数量
     * @param mapOfGoodsIdToGoodsInDb map, goodsId 到 goods 的映射
     * @return 订单创建成功后的订单信息
     */
    private Order createOrderViaRpc(List<GoodsIdAndNumber> listOfGoodsIdAndNumber,
                                    Map<Long, Goods> mapOfGoodsIdToGoodsInDb) {
        long userId = UserContext.getCurrentUser().getId();

        Order order = new Order();
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setUserId(userId);
        order.setTotalPrice(calculateTotalPrice(listOfGoodsIdAndNumber, mapOfGoodsIdToGoodsInDb));
        return orderRpcService.createOrder(listOfGoodsIdAndNumber, order);
    }

    /**
     * 扣减商品库存
     *
     * @param listOfGoodsIdAndNumber list,每个元素包含商品ID和数量
     * @return true 扣减成功；false 扣减失败
     */
    private boolean deductStock(List<GoodsIdAndNumber> listOfGoodsIdAndNumber) {
        for (GoodsIdAndNumber item : listOfGoodsIdAndNumber) {
            if (goodsStockMapper.deductStock(item) <= 0) {
                logger.error("商品库存扣减失败，商品id: " + item.getId() + "; 数量: " + item.getNumber());
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
    private long calculateTotalPrice(List<GoodsIdAndNumber> goodsList,
                                     Map<Long, Goods> goodsIdToGoodsMap) {
        return goodsList.stream()
            .mapToLong(item -> {
                long goodsId = item.getId();
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

    /**
     * 根据 List GoodsIdAndNumber 转换成 List GoodsWithNumber
     *
     * @param listOfGoodsIdAndNumber list, 每个元素只包含商品ID和数量
     * @return list, 每个元素是包含数量的 goods 对象
     */
    public List<GoodsWithNumber> generateListOfGoodsWithNumber(
        List<GoodsIdAndNumber> listOfGoodsIdAndNumber) {
        List<Goods> goodsListInDb = goodsService.getGoodsListInDb(listOfGoodsIdAndNumber);
        Map<Long, Goods> mapOfGoodsIdToGoodsInDb = goodsService.generateMapOfGoodsIdToGoods(goodsListInDb);
        return combineListOfGoodsWithNumber(listOfGoodsIdAndNumber, mapOfGoodsIdToGoodsInDb);
    }

    /**
     * 生成订单信息列表
     *
     * @param listOfRpcOrderResponse list, RpcOrderResponse
     * @return 订单信息列表
     */
    private List<OrderResponse> generateAllOrderInfoList(
        List<RpcOrderResponse> listOfRpcOrderResponse) {
        return listOfRpcOrderResponse
            .stream()
            .map(item -> generateOrderResponse(item.getGoodsList(), item.getOrder()))
            .collect(Collectors.toList());
    }

    /**
     * 将两个数组合并成包含数量的商品列表
     *
     * @param listOfGoodsIdAndNumber  list, 每个元素包含 goodsId 和 number
     * @param mapOfGoodsIdToGoodsInDb map, goodsId 到 goods 的映射
     * @return 包含数量的商品列表
     */
    private List<GoodsWithNumber> combineListOfGoodsWithNumber(
        List<GoodsIdAndNumber> listOfGoodsIdAndNumber,
        Map<Long, Goods> mapOfGoodsIdToGoodsInDb) {
        return listOfGoodsIdAndNumber
            .stream()
            .map(item -> goodsService.combineGoodsAndNumber(mapOfGoodsIdToGoodsInDb, item))
            .collect(toList());
    }
}
