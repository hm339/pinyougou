package cn.itcast.core.util;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

//@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext*.xml"})
public class ImportDataToSolr {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void importItemDataToSolr () {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        //查询审核通过的
        criteria.andStatusEqualTo("1");
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

//    public static void main(String[] args) {
//        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
//        ImportDataToSolr bean = (ImportDataToSolr)context.getBean("importDataToSolr");
//        bean.importItemDataToSolr();
//
//    }

}
