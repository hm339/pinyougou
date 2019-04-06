package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ServletContextAware是spring中的接口, spring控制这个接口的初始化
 * 这个接口中有servletContext对象, 已经被spring初始化了, 我们实现这个接口, 实现它的方法
 * 有spring初始化ServletContextAware接口中的servletContext对象, 我们用这个对象给我们
 * 本类中的servletContext属性赋值, 也就是servletContext对象被初始化了
 */
@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao descDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao catDao;

    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    //由ServletContextAware接口中的servletContext赋值进行初始化
    private ServletContext servletContext;

    @Override
    public void createStaticPage(Map<String, Object> rootMap, Long goodsId) throws Exception {
        //1. 获取模板初始化对象
        Configuration configuration = freemarkerConfig.getConfiguration();
        //2. 加载模板对象
        Template template = configuration.getTemplate("item.ftl");

        //3. 定义输出流, 流中指定文件生成的位置, 和文件名
        String path = goodsId + ".html";
        //由相对路径转换成绝对路径
        String realPath = getRealPath(path);

        //定义输出流, 并且要设置字符集编码
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)),"utf-8");
        //4. 生成静态页面
        template.process(rootMap, out);
        //5. 关闭流
        out.close();
    }


    /**
     * 获取绝对路径
     * @param path  相对路径
     * @return
     */
    private String getRealPath(String path) {
        String realPath = servletContext.getRealPath(path);
        System.out.println("===realPath=====" + realPath);
        return realPath;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public Map<String, Object> findGoodsData(Long goodsId) {
        Map<String, Object> resultMap = new HashMap<>();
        //1. 获取商品数据
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        //2. 获取商品详情数据
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(goodsId);

        //3. 获取库存数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> itemList = itemDao.selectByExample(query);

        //4. 获取分类数据
        if (goods != null) {
            ItemCat itemCat1 = catDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = catDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = catDao.selectByPrimaryKey(goods.getCategory3Id());
            resultMap.put("itemCat1", itemCat1.getName());
            resultMap.put("itemCat2", itemCat2.getName());
            resultMap.put("itemCat3", itemCat3.getName());
        }

        //5. 将所有数据封装到map中返回
        resultMap.put("goods", goods);
        resultMap.put("goodsDesc", goodsDesc);
        resultMap.put("itemList", itemList);
        return resultMap;
    }
}
