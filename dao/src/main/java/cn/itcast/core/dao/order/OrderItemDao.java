package cn.itcast.core.dao.order;

import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderItemDao {
    int countByExample(OrderItemQuery example);

    int deleteByExample(OrderItemQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    List<OrderItem> selectByExample(OrderItemQuery example);

    OrderItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrderItem record, @Param("example") OrderItemQuery example);

    int updateByExample(@Param("record") OrderItem record, @Param("example") OrderItemQuery example);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
}