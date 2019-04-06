package cn.itcast.core.controller;

import cn.itcast.core.service.SearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {

    @Reference
    private SearchService searchService;

    /**
     * 搜索
     * 页面需要展示, 当前页, 每页展示条数, 一共查询到了多少条数据, 还有返回的结果集
     */
    @RequestMapping("/search")
    public Map<String, Object> search(@RequestBody Map paramMap) {
        Map<String, Object> resultMap = searchService.search(paramMap);
        return resultMap;
    }
}
