package cn.itcast.core.listener;

import cn.itcast.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定义监听器, 监听来自于消息服务器发送来的消息
 * 接收到消息也就是商品id后, 根据商品id到solr索引库中删除对应的数据, 完成商品下架操作
 */
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private SolrManagerService solrManagerService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;

        try {
            String goodsId = atm.getText();
            //根据商品id删除solr索引库中对应的数据
            solrManagerService.deleteSolrItem(Long.parseLong(goodsId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
