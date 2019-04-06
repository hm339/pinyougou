package cn.itcast.core.listener;

import cn.itcast.core.service.CmsService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;

/**
 * 自定义监听器: 监听来自于消息服务器发送来消息, 也就是商品id
 * 根据商品id到数据库中获取商品详细数据, 根据商品数据和模板生成商品详情静态化页面
 */
public class PageListener implements MessageListener {

    @Autowired
    private CmsService cmsService;

    @Override
    public void onMessage(Message message) {
        //强转成文本消息
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;

        try {
            String goodsId = atm.getText();
            //根据商品id获取商品详细数据
            Map<String, Object> goodsData = cmsService.findGoodsData(Long.parseLong(goodsId));

            //生成商品详情页面
            cmsService.createStaticPage(goodsData, Long.parseLong(goodsId));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
