package com.example.demo.system.dao.mapper;

import com.example.demo.system.model.po.City;
import com.example.demo.system.model.po.TestPo;
import com.example.demo.system.model.po.TestPo1;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Description: mapper接口类
 */
@Mapper
public interface TestMapper {
    //    根据条件查询
    Page<TestPo> findAll(Map<String, Object> map);

    //    新建
    Integer insertTest(@Param("list") TestPo test);

    //    修改
    Integer updataTest(@Param("list") TestPo test);

    //    根据id查询
    TestPo getById(Map<String, Object> map);

    //    根据id查询上月数据
    TestPo getByIdMonth(Map<String, Object> map);

    //    删除文件更新数据库字段
    Integer updataTestDoc(Map<String, Object> map);

    //    删除数据
    Integer deleteByIdAll(Map<String, Object> map);

    //    批量插入
    Integer insertTestAll(@Param("list") List<TestPo> testPo);

    //    根据id查询多个数据
    TestPo1 getByIdAll(Map<String, Object> map);

    //    删除数据
    Integer deleteByIdImage(Map<String, Object> map);

    //    删除数据
    Integer deleteById(Map<String, Object> map);

    //  删除最老数据  多个入参必须要用@Param()注解表明，或者传参传map
    Integer del(@Param("tableName") String tableName , @Param("ceshiCount") Integer ceshiCount);

    //    判断文件名否存在
    Integer getFilename(Map<String, Object> map);
    //    省级区域
    List<City> getCity(Map<String, Object> map);

    List<TestPo> find();
}
