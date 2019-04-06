package cn.itcast.core.service;

import java.io.IOException;
import java.util.Map;

public interface CmsService {

    /**
     * 根据数据和商品id创建静态页面
     * @param rootMap   商品的各种详细数据
     * @param goodsId   商品id
     */
    public void createStaticPage(Map<String, Object> rootMap, Long goodsId) throws Exception;

    public Map<String, Object> findGoodsData(Long goodsId);
}
