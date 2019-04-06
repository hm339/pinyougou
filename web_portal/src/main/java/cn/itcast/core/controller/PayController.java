package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.service.OrderService;
import cn.itcast.core.service.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付业务
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference
    private PayService payService;

    /**
     * 统一下单接口
     * 根据当前登录用户的用户名, 获取支付日志对象, 根据支付日志对象中的总金额和支付单号
     * 调用微信支付api的统一下单接口, 生成支付链接返回
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //1. 获取当前登录用户的用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //2. 获取支付日志对象
        PayLog payLog = orderService.getPayLogByUserName(userName);
        if (payLog != null) {
            //3. 调用统一下单接口, 生成支付链接
            Map map = payService.createNative(payLog.getOutTradeNo(), "1");//payLog.getTotalFee()
            return map;
        }
        return  new HashMap();
    }

    /**
     * 根据支付单号, 查询是否支付成功
     * @param out_trade_no  支付单号
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        int flag = 1;
        while(true) {
            //1. 根据支付单号调用查询订单接口, 查询是否支付成功
            Map<String, String> map = payService.queryPayStatus(out_trade_no);
            //2. 判断查询支付是否成功
            if (map == null) {
                result = new Result(false, "二维码超时");
                break;
            }
            //3. 判断支付是否成功
            if ("SUCCESS".equals(map.get("trade_state"))) {
                result = new Result(true, "支付成功!");
                //4. 如果支付成功, 删除支付日志记录, 将支付状态改为已支付
                orderService.updatePayStatus(userName);
                break;
            }

            //每3秒查一次
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //如果等待扫码时间超过5分钟, 则返回支付失败, 二维码超时, 页面重新调用统一下单接口重新生成一个二维码.
            if (flag > 100) {
                result = new Result(false, "二维码超时");
                break;
            }
            flag++;

        }
        return result;
    }
}
