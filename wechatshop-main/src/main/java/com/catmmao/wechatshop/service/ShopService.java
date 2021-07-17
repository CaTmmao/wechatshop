package com.catmmao.wechatshop.service;

import java.util.List;

import com.catmmao.wechatshop.UserContext;
import com.catmmao.wechatshop.generated.ShopMapper;
import com.catmmao.wechatshop.api.exception.HttpException;
import com.catmmao.wechatshop.api.data.DbDataStatus;
import com.catmmao.wechatshop.generated.Shop;
import com.catmmao.wechatshop.generated.ShopExample;
import com.catmmao.wechatshop.api.data.PaginationResponse;
import org.springframework.stereotype.Service;

@Service
public class ShopService {
    private final ShopMapper shopMapper;

    public ShopService(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    /**
     * 创建店铺
     *
     * @param shop 店铺信息
     * @return 创建后的店铺信息
     */
    public Shop createShop(Shop shop) {
        shop.setOwnerUserId(UserContext.getCurrentUser().getId());

        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria().andNameEqualTo(shop.getName());
        List<Shop> shops = shopMapper.selectByExample(shopExample);

        if (!shops.isEmpty()) {
            throw HttpException.badRequest("店铺名重复");
        }

        long id = shopMapper.insertSelective(shop);
        shop.setId(id);

        return shop;
    }

    /**
     * 修改店铺
     *
     * @param shop   店铺信息
     */
    public void updateShop(Shop shop) {
        Long userId = UserContext.getCurrentUser().getId();
        Shop shopInDatabase = shopMapper.selectByPrimaryKey(shop.getId());

        if (shopInDatabase == null) {
            throw HttpException.resourceNotFound("找不到该店铺");
        }

        checkUserIfShopOwner(shopInDatabase.getOwnerUserId(), userId);
        shopMapper.updateByPrimaryKeySelective(shop);
    }

    /**
     * 删除店铺
     *
     * @param shopId 店铺ID
     * @return 已删除的店铺信息
     */
    public Shop deleteShop(Long shopId) {
        Long userId = UserContext.getCurrentUser().getId();
        Shop shopInDatabase = shopMapper.selectByPrimaryKey(shopId);

        if (shopInDatabase == null) {
            throw HttpException.resourceNotFound("找不到该店铺");
        }

        checkUserIfShopOwner(shopInDatabase.getOwnerUserId(), userId);
        shopInDatabase.setStatus(DbDataStatus.DELETE.getName());
        shopMapper.updateByPrimaryKeySelective(shopInDatabase);
        return shopInDatabase;
    }

    /**
     * 获取当前用户拥有的所有店铺
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示的数量
     * @return 获取到的店铺列表
     */
    public PaginationResponse<Shop> getMyShopListByUserId(int pageNum, int pageSize) {
        Long userId = UserContext.getCurrentUser().getId();
        // 店铺总数量
        int totalNumber = countShop(userId);
        // 总页数
        final int totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : totalNumber / pageSize + 1;

        // 获取店铺列表
        ShopExample shopExample = new ShopExample();
        shopExample.setLimit(pageSize);
        shopExample.setOffset((pageNum - 1) * pageSize);
        shopExample.createCriteria().andOwnerUserIdEqualTo(userId);
        List<Shop> shopList = shopMapper.selectByExample(shopExample);

        return new PaginationResponse<>(pageSize, pageNum, totalPage, shopList);
    }

    /**
     * 获取指定ID的店铺
     *
     * @param shopId 店铺ID
     * @return 店铺信息
     */
    public Shop getShopByShopId(Long shopId) {
        Shop shop = shopMapper.selectByPrimaryKey(shopId);

        if (shop == null) {
            throw HttpException.resourceNotFound("店铺未找到");
        }

        return shop;
    }

    /**
     * 获取店铺总数
     *
     * @param userId 当前用户 id
     * @return 当前用户所拥有店铺列表
     */
    public int countShop(Long userId) {
        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria().andOwnerUserIdEqualTo(userId);
        shopExample.createCriteria().andStatusEqualTo(DbDataStatus.OK.getName());
        return (int) shopMapper.countByExample(shopExample);
    }

    /**
     * 检查当前用户是否是该店铺拥有者
     *
     * @param shopOwnerId 店铺拥有者ID
     * @param userId      当前用户ID
     */
    public void checkUserIfShopOwner(long shopOwnerId, Long userId) {
        if (!userId.equals(shopOwnerId)) {
            throw HttpException.forbidden("店铺不属于该用户");
        }
    }
}
