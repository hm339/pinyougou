package cn.itcast.core.dao.address;

import cn.itcast.core.pojo.address.Cities;
import cn.itcast.core.pojo.address.CitiesQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CitiesDao {
    int countByExample(CitiesQuery example);

    int deleteByExample(CitiesQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(Cities record);

    int insertSelective(Cities record);

    List<Cities> selectByExample(CitiesQuery example);

    Cities selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Cities record, @Param("example") CitiesQuery example);

    int updateByExample(@Param("record") Cities record, @Param("example") CitiesQuery example);

    int updateByPrimaryKeySelective(Cities record);

    int updateByPrimaryKey(Cities record);
}