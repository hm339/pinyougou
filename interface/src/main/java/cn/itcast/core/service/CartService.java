package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.BuyerCart;

import java.util.List;

public interface CartService {


    public List<BuyerCart> addGoodsToCartList(List<BuyerCart> cartList, Long itemId, Integer num);

    public void setCartListToRedis(String userName, List<BuyerCart> cartList);

    public List<BuyerCart> getCartListFromRedis(String userName);

    public List<BuyerCart> mergeCookieCartListToRedisCartList(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList);
}
