package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller, Integer page, Integer rows) {
        PageResult result = sellerService.findPage(seller, page, rows);
        return result;
    }

    @RequestMapping("/findOne")
    public Seller findOne(String id) {
        Seller one = sellerService.findOne(id);
        return one;
    }

    /**
     * 修改商家状态
     * @param sellerId  卖家id
     * @param status    状态, 0未审核, 1审核通过, 2审核不通过, 3关闭商家
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId, String status) {
        try {
            sellerService.updateStatus(sellerId, status);
            return new Result(true, "审核成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败!");
        }
    }
}
