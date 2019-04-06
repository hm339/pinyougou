package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map paramMap) {
        //1. 根据条件, 分页,高亮, 过滤, 排序查询
        Map<String, Object> resultMap = searchMap(paramMap);

        //2. 根据条件分组查询, 根据分类分组, 主要是为了找到查询结果中的分类名称, 分组的目的是为了给分类去重
        List<String> categotryGroup = findCategotryGroup(paramMap);
        resultMap.put("categoryList", categotryGroup);

        //获取从页面传入的条件中是否有分类
        String categoryName = String.valueOf(paramMap.get("category"));
        //3. 根据传入的条件判断, 是否有分类
        if (categoryName != null && !"".equals(categoryName)) {
            //4. 如果有分类根据分类找对应的品牌集合和规格集合
            Map brandAndSpecMap = findBrandListAndSpecListByCategoryName(categoryName);
            resultMap.putAll(brandAndSpecMap);
        } else {
            //5. 如果传入的条件中没有分类, 则根据第一个分类, 查找对应的品牌集合和规格集合
            Map brandAndSpecMap = findBrandListAndSpecListByCategoryName(categotryGroup.get(0));
            resultMap.putAll(brandAndSpecMap);
        }

        return resultMap;
    }

    /**
     * 根据关键字, 分页高亮过滤查询
     * @param paramMap 页面传入过来的参数
     * @return
     */
    private Map<String, Object> searchMap(Map paramMap) {
        Map<String, Object> resultMap = new HashMap<>();

        /**
         * 1. 接收查询条件
         */
        //获取查询的关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        if (keywords != null) {
            keywords = keywords.replaceAll(" ", "");
        }
        //获取当前页
        Integer pageNo= Integer.parseInt(String.valueOf(paramMap.get("pageNo")));
        //获取每页展示多少条数据
        Integer pageSize= Integer.parseInt(String.valueOf(paramMap.get("pageSize")));
        //获取用户选中的分类过滤条件
        String category = String.valueOf(paramMap.get("category"));
        //获取用户选中的品牌过滤条件
        String brand = String.valueOf(paramMap.get("brand"));
        //获取用户选中的规格过滤条件
        String spec = String.valueOf(paramMap.get("spec"));
        //获取用户选中的价格区间过滤条件
        String price = String.valueOf(paramMap.get("price"));
        //接收页面条件排序字段
        String sortField = String.valueOf(paramMap.get("sortField"));
        //接收页面条件排序方式
        String sortType = String.valueOf(paramMap.get("sort"));


        /**
         * 2. 封装查询对象
         */
        //创建查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询对象中
        query.addCriteria(criteria);

        if (pageNo == null || pageNo <= 0 ) {
            pageNo = 1;
        }
        //计算从第几条开始查询
        Integer start = (pageNo - 1) * pageSize;
        //设置当前从第几条开始查询
        query.setOffset(start);
        //设置每页查询多少条数据
        query.setRows(pageSize);

        //设置分页
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置在标题域中高亮显示
        highlightOptions.addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        //设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //将高亮选项放入查询对象中
        query.setHighlightOptions(highlightOptions);

        /**
         * 查询对象中加入过滤条件
         */

        //按照分类过滤
        if (category != null && !"".equals(category)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建过滤条件对象
            Criteria filterCriTeria = new Criteria("item_category").is(category);
            //将过滤条件加入到过滤查询对象中
            filterQuery.addCriteria(filterCriTeria);
            //将过滤查询对象加入到查询对象中
            query.addFilterQuery(filterQuery);
        }

        //按照品牌过滤
        if (brand != null && !"".equals(brand)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建过滤条件对象
            Criteria filterCriTeria = new Criteria("item_brand").is(brand);
            //将过滤条件加入到过滤查询对象中
            filterQuery.addCriteria(filterCriTeria);
            //将过滤查询对象加入到查询对象中
            query.addFilterQuery(filterQuery);
        }

        //按照规格过滤
        if (spec != null && !"".equals(spec)) {
            //将页面传入的过滤条件json字符串转换成map
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            if (map != null && map.size() > 0) {
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建过滤条件对象
                    Criteria filterCriTeria = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                    //将过滤条件加入到过滤查询对象中
                    filterQuery.addCriteria(filterCriTeria);
                    //将过滤查询对象加入到查询对象中
                    query.addFilterQuery(filterQuery);
                }
            }
        }

        //按照价格区间过滤
        if (price != null && !"".equals(price)) {
            //切割价格区间字符串, 这个数组一共两个数据, 最小值和最大值
            String[] split = price.split("-");
            if (split != null && split.length == 2) {
                //最小值不等于0的, 大于等于
                if (!"0".equals(split[0])) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建过滤条件对象
                    Criteria filterCriTeria = new Criteria("item_price").greaterThanEqual(split[0]);
                    //将过滤条件加入到过滤查询对象中
                    filterQuery.addCriteria(filterCriTeria);
                    //将过滤查询对象加入到查询对象中
                    query.addFilterQuery(filterQuery);
                }
                //最大值不等于*的, 小于等于
                if (!"*".equals(split[1])) {
                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery();
                    //创建过滤条件对象
                    Criteria filterCriTeria = new Criteria("item_price").lessThanEqual(split[1]);
                    //将过滤条件加入到过滤查询对象中
                    filterQuery.addCriteria(filterCriTeria);
                    //将过滤查询对象加入到查询对象中
                    query.addFilterQuery(filterQuery);
                }
            }
        }

        /**
         * 设置排序
         */
        if (sortField != null && sortType != null && !"".equals(sortField) && !"".equals(sortType)) {
            //升序
            if ("ASC".equals(sortType)) {
                //创建排序对象, 第一个参数排序方式, 第二个参数:排序的域名
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                //将排序对象放入查询对象中
                query.addSort(sort);
            }
            //降序
            if ("DESC".equals(sortType)) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }

        /**
         * 3.  查询并返回结果
         */
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);

        //获取高亮结果集
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();

        List<Item> itemList = new ArrayList<>();
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //获取不带高亮的item对象
            Item item = itemHighlightEntry.getEntity();
            if (itemHighlightEntry.getHighlights() != null && itemHighlightEntry.getHighlights().size() > 0) {
                List<String> snipplets = itemHighlightEntry.getHighlights().get(0).getSnipplets();
                if (snipplets != null && snipplets.size() > 0) {
                    //终于获取到高亮的标题了
                    String hightTitle = snipplets.get(0);
                    //如果获取到高亮标题覆盖实体对象中原来不带高亮的标题
                    item.setTitle(hightTitle);
                }
            }
            itemList.add(item);
        }



        //封装返回的查询到的结果集
        resultMap.put("rows", itemList);
        //总条数
        resultMap.put("total", items.getTotalElements());
        //总页数
        resultMap.put("totalPages", items.getTotalPages());
        return resultMap;
    }

    /**
     * 
     * 根据条件分组查询, 根据分类分组, 主要是为了找到查询结果中的分类名称, 分组的目的是为了给分类去重
     * @param paramMap 页面传入的查询条件
     * @return
     */
    private List<String> findCategotryGroup(Map paramMap) {
        List<String> resultList = new ArrayList<>();
        //获取查询的关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        if (keywords != null) {
            keywords = keywords.replaceAll(" ", "");
        }
        //创建查询对象
        Query query = new SimpleQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询对象中
        query.addCriteria(criteria);
        
        //创建分组对象
        GroupOptions groupOption = new GroupOptions();
        //设置根据哪个域进行分组
        groupOption.addGroupByField("item_category");
        //将分组对象放入查询对象中
        query.setGroupOptions(groupOption);

        //分组查询
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //获取分组后的结果集
        GroupResult<Item> item_category = items.getGroupResult("item_category");
        //获取分组后的集合
        Page<GroupEntry<Item>> groupEntries =  item_category.getGroupEntries();
        //遍历分组集合数据
        if (groupEntries != null) {
            for (GroupEntry<Item> groupEntry : groupEntries) {
                //获取分组后的分类的名称
                String categoryGroupValue = groupEntry.getGroupValue();
                resultList.add(categoryGroupValue);
            }
        }
        
        return resultList;
    }

    /**
     * 根据消费者选中的分类名称查询对应的品牌集合和规格集合过滤条件展示到页面
     * @param categoryName 分类名称
     * @return
     */
    private Map findBrandListAndSpecListByCategoryName(String categoryName) {
        //1. 根据分类名称到redis中查询对应的模板id
        Long templateId = (Long)redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).get(categoryName);
        //2. 根据模板id, 到redis查询对应的品牌集合
        List<Map> brandList= (List<Map>)redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).get(templateId);
        //3. 根据模板id, 到redis中查询对应的规格集合
        List<Map> specList = (List<Map>)redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).get(templateId);
        //4. 将品牌集合和规格集合封装到map中返回
        Map resultMap = new HashMap();
        resultMap.put("brandList", brandList);
        resultMap.put("specList", specList);
        return resultMap;
    }
}
