package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.util.List;
import java.util.Map;

@Service
public class SolrManagerServiceImpl implements SolrManagerService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemDao itemDao;


    @Override
    public void saveItemToSolr(Long id) {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        //查询指定的商品id的库存数据
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);

        if (items != null) {
            for (Item item : items) {
                //获取规格的json字符串
                String specJsonStr = item.getSpec();
                //将json转换成map
                Map map = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(map);
            }
            //将数据保存到solr索引库
            solrTemplate.saveBeans(items);
            //提交
            solrTemplate.commit();
        }
    }

    @Override
    public void deleteSolrItem(Long id) {
        //创建solr的查询对象
        Query query = new SimpleQuery();
        //创建条件对象
        Criteria criteria = new Criteria("item_goodsid").is(id);
        //将条件对象放入查询对象中
        query.addCriteria(criteria);

        //根据商品id, 条件进行删除
        solrTemplate.delete(query);
        //提交
        solrTemplate.commit();
    }
}
