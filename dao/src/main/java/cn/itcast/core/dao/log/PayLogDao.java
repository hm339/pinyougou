package cn.itcast.core.dao.log;

import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.log.PayLogQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PayLogDao {
    int countByExample(PayLogQuery example);

    int deleteByExample(PayLogQuery example);

    int deleteByPrimaryKey(String outTradeNo);

    int insert(PayLog record);

    int insertSelective(PayLog record);

    List<PayLog> selectByExample(PayLogQuery example);

    PayLog selectByPrimaryKey(String outTradeNo);

    int updateByExampleSelective(@Param("record") PayLog record, @Param("example") PayLogQuery example);

    int updateByExample(@Param("record") PayLog record, @Param("example") PayLogQuery example);

    int updateByPrimaryKeySelective(PayLog record);

    int updateByPrimaryKey(PayLog record);
}