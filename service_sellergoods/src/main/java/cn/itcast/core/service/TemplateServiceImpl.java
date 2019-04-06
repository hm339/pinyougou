package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TypeTemplateDao templateDao;

    @Autowired
    private SpecificationOptionDao optionDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult findPage(TypeTemplate template, Integer page, Integer rows) {
        //1. 查询所有模板数据
        List<TypeTemplate> typeTemplates = templateDao.selectByExample(null);

        for (TypeTemplate typeTemplate : typeTemplates) {
            //2. 模板id作为key, 对应的品牌集合作为value, 缓存入redis中
            String brandIdsJsonStr = typeTemplate.getBrandIds();
            List<Map> brandList = JSON.parseArray(brandIdsJsonStr, Map.class);
            //缓存品牌集合数据
            redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).put(typeTemplate.getId(), brandList);

            //3. 模板id作为key, 对应的规格集合作为value, 缓存入redis中
            List<Map> specList = findBySpecList(typeTemplate.getId());
            redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).put(typeTemplate.getId(), specList);
        }



        //4. 分页查询, 并且将数据返回到页面展示
        PageHelper.startPage(page, rows);
        TypeTemplateQuery query = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = query.createCriteria();
        if (template != null) {
            if (template.getName() != null && !"".equals(template.getName())){
                criteria.andNameLike("%"+template.getName()+"%");
            }
        }

        Page<TypeTemplate> templateList = (Page<TypeTemplate>)templateDao.selectByExample(query);
        return new PageResult(templateList.getTotal(), templateList.getResult());
    }

    @Override
    public void add(TypeTemplate template) {
        templateDao.insertSelective(template);
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return templateDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(TypeTemplate template) {
        templateDao.updateByPrimaryKeySelective(template);
    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                templateDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        //1. 根据模板id查询对应的模板对象
        TypeTemplate typeTemplate = templateDao.selectByPrimaryKey(id);
        //2. 从模板对象中获取规格集合的json数据(字符串类型)
        String specIds = typeTemplate.getSpecIds();
        //3. 将json字符串类型的规格集合转换成Java对象,
        //使用阿里的json转化工具, 将json字符串转换成list集合, 第一个参数是转换的字符串, 第二个参数指定集合泛型的类型
        //这是一个map例如: id:1, text网络      这有是一个map,例如: id:32, text: 机身网络
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        //4. 遍历规格集合数据
        if (maps != null) {
            for (Map map : maps) {
                //5. 在遍历的过程中将根据规格id获取对应的规格选项集合, 并且封装到规格对象中
                Long specId= Long.parseLong(String.valueOf(map.get("id")));

                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(specId);
                //根据规格id查询到规格选项集合
                List<SpecificationOption> optionList = optionDao.selectByExample(query);
                map.put("options", optionList);
            }
        }

        return maps;
    }
}
