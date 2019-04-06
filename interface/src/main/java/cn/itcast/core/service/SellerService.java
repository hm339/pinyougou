package cn.itcast.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seller.Seller;

public interface SellerService {

    public void add(Seller seller);

    public Seller findOne(String id);

    public PageResult findPage(Seller seller, Integer page, Integer rows);

    public void updateStatus(String sellerId, String status);

}
