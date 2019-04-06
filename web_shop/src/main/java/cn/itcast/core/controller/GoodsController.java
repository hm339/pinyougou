package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品管理
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

//    @Reference
//    private SolrManagerService solrManagerService;

    @RequestMapping("/add")
    public Result add(@RequestBody GoodsEntity goodsEntity){
        try {
            //获取当前登录用户的用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //设置商品的卖家id
            goodsEntity.getGoods().setSellerId(userName);

            goodsService.add(goodsEntity);
            return new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败!");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods, Integer page, Integer rows) {
        //获取当前登录用户用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //将当前用户名放入卖家id字段
        goods.setSellerId(userName);

        PageResult result = goodsService.findPage(goods, page, rows);
        return result;
    }

    /**
     * 根据商品id查询所有商品涉及的数据
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id) {
        GoodsEntity one = goodsService.findOne(id);
        return  one;
    }

    @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity) {
        try {
            //判断当前用户是否有权限修改这个对象
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //卖家id
            String sellerId = goodsEntity.getGoods().getSellerId();
            if (!userName.equals(sellerId)) {
                return   new Result(false, "您没有权限修改这个商品!");
            }
            goodsService.update(goodsEntity);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            if (ids != null) {
                for (Long id : ids) {
                    //1. 到数据库中根据商品id逻辑删除商品数据
                    goodsService.delete(id);

                    //2. 根据商品id, 到solr索引库中删除对应的数据
                    //solrManagerService.deleteSolrItem(id);
                }
            }

            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }
}
