package cn.itcast.core.dao.good;

import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsDescQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GoodsDescDao {
    int countByExample(GoodsDescQuery example);

    int deleteByExample(GoodsDescQuery example);

    int deleteByPrimaryKey(Long goodsId);

    int insert(GoodsDesc record);

    int insertSelective(GoodsDesc record);

    List<GoodsDesc> selectByExample(GoodsDescQuery example);

    GoodsDesc selectByPrimaryKey(Long goodsId);

    int updateByExampleSelective(@Param("record") GoodsDesc record, @Param("example") GoodsDescQuery example);

    int updateByExample(@Param("record") GoodsDesc record, @Param("example") GoodsDescQuery example);

    int updateByPrimaryKeySelective(GoodsDesc record);

    int updateByPrimaryKey(GoodsDesc record);
}