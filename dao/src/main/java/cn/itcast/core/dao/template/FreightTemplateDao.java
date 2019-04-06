package cn.itcast.core.dao.template;

import cn.itcast.core.pojo.template.FreightTemplate;
import cn.itcast.core.pojo.template.FreightTemplateQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FreightTemplateDao {
    int countByExample(FreightTemplateQuery example);

    int deleteByExample(FreightTemplateQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(FreightTemplate record);

    int insertSelective(FreightTemplate record);

    List<FreightTemplate> selectByExample(FreightTemplateQuery example);

    FreightTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") FreightTemplate record, @Param("example") FreightTemplateQuery example);

    int updateByExample(@Param("record") FreightTemplate record, @Param("example") FreightTemplateQuery example);

    int updateByPrimaryKeySelective(FreightTemplate record);

    int updateByPrimaryKey(FreightTemplate record);
}