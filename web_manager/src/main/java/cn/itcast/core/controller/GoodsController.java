package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.GoodsEntity;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.CmsService;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
//
//    @Reference
//    private CmsService cmsService;


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

    /**
     * 修改商品状态
     * @param ids       商品id
     * @param status    商品状态: 0未审核, 1审核通过, 2驳回
     * @return
     */
    @RequestMapping("/updateStatus")
    public  Result updateStatus(Long[] ids, String status) {
        try {
            if (ids != null) {
                for (Long id : ids) {
                    //1. 更改数据库中商品的审核状态
                    goodsService.updateStatus(id, status);

                    //2. 判断商品审核状态是否为1, 审核通过
//                    if ("1".equals(status)) {
//                        //3. 根据商品id, 获取商品详细数据, 放入solr索引库中供前台系统搜索使用
//                        solrManagerService.saveItemToSolr(id);
//                        //4. 根据商品id, 获取商品详细数据, 通过数据和模板生成商品详情页面
//                        Map<String, Object> goodsMap = cmsService.findGoodsData(id);
//                        cmsService.createStaticPage(goodsMap, id);
//                    }
                }
            }
            return new Result(true, "状态修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "状态修改失败!");
        }
    }

    /**
     * 测试接口, 测试生成静态页面
     * @param id    商品id
     * @return
     */
//    @RequestMapping("/testPage")
//    public Boolean testCreatePage(Long id) throws Exception {
//        try {
//            Map<String, Object> goodsMap = cmsService.findGoodsData(id);
//            cmsService.createStaticPage(goodsMap, id);
//            return  true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return  false;
//        }
//    }
}
