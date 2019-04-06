package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.SpecEntity;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecServiceImpl implements SpecService {

    @Autowired
    private SpecificationDao specDao;

    @Autowired
    private SpecificationOptionDao optionDao;


    @Override
    public PageResult findPage(Specification spec, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        SpecificationQuery query = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = query.createCriteria();
        if (spec != null) {
            if (spec.getSpecName() != null && !"".equals(spec.getSpecName())) {
                criteria.andSpecNameLike("%"+spec.getSpecName()+"%");
            }
        }
        Page<Specification> specList = (Page<Specification>)specDao.selectByExample(query);
        return new PageResult(specList.getTotal(), specList.getResult());
    }

    @Override
    public void add(SpecEntity spec) {
        //1. 保存规格对象
        specDao.insertSelective(spec.getSpecification());

        //2. 保存规格选项集合对象
        if (spec.getSpecificationOptionList() != null) {
            for (SpecificationOption option : spec.getSpecificationOptionList()) {
                //设置规格选项外键
                option.setSpecId(spec.getSpecification().getId());
                //保存规格选项
                optionDao.insertSelective(option);
            }
        }
    }

    @Override
    public SpecEntity findOne(Long id) {
        //1. 根据规格id查询规格实体
        Specification spec = specDao.selectByPrimaryKey(id);
        //2. 根据规格id查询规格选项集合
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        //根据规格id查询选项集合, 规格id在选项表中是外键
        criteria.andSpecIdEqualTo(id);
        List<SpecificationOption> optionList = optionDao.selectByExample(query);
        //3. 将规格和规格选项集合封装到返回的实体对象中
        SpecEntity entity = new SpecEntity();
        entity.setSpecification(spec);
        entity.setSpecificationOptionList(optionList);
        return entity;
    }

    @Override
    public void update(SpecEntity specEntity) {
        //1. 根据规格主键修改规格对象
        specDao.updateByPrimaryKeySelective(specEntity.getSpecification());

        //2. 根据规格主键删除规格选项集合中的数据
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpecification().getId());
        optionDao.deleteByExample(query);

        //3. 将规格选项集合重新添加到规格选项表
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption option : specEntity.getSpecificationOptionList()) {
                //设置规格选项对象外键
                option.setSpecId(specEntity.getSpecification().getId());
                optionDao.insertSelective(option);
            }
        }
    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long specId : ids) {
                //1. 根据规格主键删除规格数据
                specDao.deleteByPrimaryKey(specId);
                //2. 根据规格主键删除规格选项集合数据
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(specId);
                optionDao.deleteByExample(query);
            }
        }

    }

    @Override
    public List<Map> selectOptionList() {
        return specDao.selectOptionList();
    }
}
