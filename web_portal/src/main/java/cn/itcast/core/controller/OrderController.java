package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 提交订单
     * @param order
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody  Order order) {

        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(userName);
            orderService.add(order);

            return new Result(true, "订单提交成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "订单提交失败!");
        }
    }
}
