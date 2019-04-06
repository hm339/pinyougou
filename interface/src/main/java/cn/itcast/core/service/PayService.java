package cn.itcast.core.service;

import java.util.Map;

public interface PayService {

    public Map createNative(String outTradeNo, String totolFee);

    public Map<String, String> queryPayStatus(String out_trade_no);
}
