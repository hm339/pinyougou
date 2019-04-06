package cn.itcast.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<Brand> findAll() {
        List<Brand> list = brandService.findAll();
        return list;
    }

    /**
     * 品牌分页查询
     * @param page  当前页
     * @param rows  每页查询多少条数据
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows) {
        PageResult result = brandService.findPage(null, page, rows);
        return result;
    }

    /**
     * 添加品牌数据
     * @param brand
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody  Brand brand) {
        try {
            brandService.add(brand);
            return new Result(true, "添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败!");
        }
    }

    /**
     * 根据id查询品牌对象
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Brand findOne(Long id) {
        Brand one = brandService.findOne(id);
        return one;
    }

    /**
     * 保存修改
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand) {
        try {
            brandService.update(brand);
            return new Result(true, "修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败!");
        }
    }

    /**
     * 删除选中的数据
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败!");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody Brand brand, Integer page, Integer rows) {
        PageResult result = brandService.findPage(brand, page, rows);
        return result;
    }

    /**
     * 获取模板下拉选择框所需要的数据
     * [{id:1,text:xxx},{id:2, text: asdfasdfsa}]
     * @return
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        List<Map> list = brandService.selectOptionList();
        return list;
    }
}
