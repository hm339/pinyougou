package cn.itcast.core.listener;

import cn.itcast.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定义监听器: 监听来自于消息服务器发送来的消息,
 * 根据消息也就是商品id到数据库中获取商品详细数据, 将数据放入solr索引库供前端系统搜索使用
 */
public class ItemSearchListener implements MessageListener {

    @Autowired
    private SolrManagerService solrManagerService;

    @Override
    public void onMessage(Message message) {
        //为了方便获取文本消息, 所以将原生的消息对象强转成activeMq的文本消息对象
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;

        try {
            String goodsId = atm.getText();
            solrManagerService.saveItemToSolr(Long.parseLong(goodsId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
