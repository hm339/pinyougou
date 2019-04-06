package cn.itcast.core.service;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public interface ContentCategoryService {

    public PageResult search(ContentCategory category, Integer page, Integer rows);

    public void add(ContentCategory category);

    public ContentCategory findOne(Long id);

    public void update(ContentCategory category);

    public List<ContentCategory> findAll();

    public void delete(Long[] ids);
}
