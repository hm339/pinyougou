package cn.itcast.core.pojo.entity;

import cn.itcast.core.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义购物车实体类
 */
public class BuyerCart implements Serializable {

    private String sellerId;//商家ID
    private String sellerName;//商家名称
    private List<OrderItem> orderItemList;//购物车明细

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
