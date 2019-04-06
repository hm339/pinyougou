package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * redis分布式缓存使用原则:
 *   数据库中是最可靠的数据, 维护数据库中数据的时候, 不管是增加, 修改, 还是删除, 同步的维护redis缓存中的数据
 *   不管任何时候, 都要保证数据库中的数据和redis中的数据一致.
 */
@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Content content, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        if (content != null) {
            if (content.getTitle() != null  && !"".equals(content.getTitle())) {
                criteria.andTitleLike("%"+content.getTitle()+"%");
            }
        }
        Page<Content> contentList = (Page<Content>)contentDao.selectByExample(query);
        return new PageResult(contentList.getTotal(), contentList.getResult());
    }

    @Override
    public void add(Content content) {
        //1. 将数据添加到数据库中
        contentDao.insertSelective(content);
        //2. 将redis中对应的数据删除, 删除后就查询不到了, 在展示广告数据的方法中, 就会去数据库中查询,
        //查询到后保存到redis中一份, 这样数据就一致了
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
    }

    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Content content) {
        //1. 根据广告id到数据库中查询数据库中原来的广告对象
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());

        //2. 根据数据库中原来的广告对象的分类id, 删除redis中对应的广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(oldContent.getCategoryId());
        //3. 根据新的广告对象中的分类id删除redis中的对应的广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
        //4. 将新的广告对象中的数据更新到数据库中
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                //1. 根据广告id, 到数据库中查询广告对象
                Content content = contentDao.selectByPrimaryKey(id);
                //2. 根据广告对象中的分类id, 删除redis中对应的广告集合数据
                redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
                //3. 删除数据库中对应的数据
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) {

        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<Content> contents = contentDao.selectByExample(query);
        return contents;
    }

    /**
     * 使用hash类型存储
     *    key   value(value是hash类型)
     *          filed      value
     *          filed      value
     *          field      .....
     *          广告分类id   对应的广告集合List<Content>
     *
     */
    @Override
    public List<Content> findByCategoryIdFromRedis(Long categoryId) {
        //1. 从redis中获取数据
        List<Content> contentList = (List<Content>)redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).get(categoryId);
        //2. 判断redis中是否存在数据
        if (contentList == null) {
            //3. 如果redis中不存在对应的数据可以从数据库中获取
            contentList = findByCategoryId(categoryId);
            //4. 从数据库中获取数据后, 放入redis中缓存上
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).put(categoryId, contentList);
        }

        return contentList;
    }


}
