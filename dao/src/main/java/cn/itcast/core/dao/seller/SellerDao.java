package cn.itcast.core.dao.seller;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SellerDao {
    int countByExample(SellerQuery example);

    int deleteByExample(SellerQuery example);

    int deleteByPrimaryKey(String sellerId);

    int insert(Seller record);

    int insertSelective(Seller record);

    List<Seller> selectByExample(SellerQuery example);

    Seller selectByPrimaryKey(String sellerId);

    int updateByExampleSelective(@Param("record") Seller record, @Param("example") SellerQuery example);

    int updateByExample(@Param("record") Seller record, @Param("example") SellerQuery example);

    int updateByPrimaryKeySelective(Seller record);

    int updateByPrimaryKey(Seller record);
}