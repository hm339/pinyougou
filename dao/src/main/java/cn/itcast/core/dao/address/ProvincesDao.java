package cn.itcast.core.dao.address;

import cn.itcast.core.pojo.address.Provinces;
import cn.itcast.core.pojo.address.ProvincesQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProvincesDao {
    int countByExample(ProvincesQuery example);

    int deleteByExample(ProvincesQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(Provinces record);

    int insertSelective(Provinces record);

    List<Provinces> selectByExample(ProvincesQuery example);

    Provinces selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Provinces record, @Param("example") ProvincesQuery example);

    int updateByExample(@Param("record") Provinces record, @Param("example") ProvincesQuery example);

    int updateByPrimaryKeySelective(Provinces record);

    int updateByPrimaryKey(Provinces record);
}