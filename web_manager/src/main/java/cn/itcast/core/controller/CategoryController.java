package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 广告分类管理
 */
@RestController
@RequestMapping("/contentCategory")
public class CategoryController {

    @Reference
    private ContentCategoryService categoryService;

    @RequestMapping("/findAll")
    public List<ContentCategory> findAll() {
        return categoryService.findAll();
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody ContentCategory category, Integer page, Integer rows) {
        PageResult result = categoryService.search(category, page, rows);
        return result;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody ContentCategory category) {
        try {
            categoryService.add(category);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id) {
        return categoryService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody  ContentCategory category) {
        try {
            categoryService.update(category);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            categoryService.delete(ids);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }
}
