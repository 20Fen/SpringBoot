package com.example.demo.system.service;

import com.example.demo.system.model.bo.TestBo;
import com.example.demo.system.model.po.*;
import com.example.demo.system.util.PageResult;
import com.exception.CustomException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Description: 接口类
 */

public interface TestService {
    //    根据条件查询
    PageResult<List<TestPo>> findAll(Integer page, Integer pageSize, Map<String,Object> map);

    //    新建和修改
    String insert(TestBo testBo) throws Exception;

    //    根据id查询
    TestPo getById(String planNo);

    //    根据id查询上月数据
    TestPo getByIdMonth(String planNo);

    //    上传文件
    String upload(String planNo, MultipartFile file) throws Exception;

    //    批量删除数据并删除文件
    String delete(Test planNo) throws Exception;

    //    删除文件并更新数据库字段
    String deleteUrl(String planNo) throws Exception;

    TestPo getById(Map<String, Object> map);
    //    删除数据并删除多个文件
    String deleteUrlAll(String planNo) throws CustomException, IOException;

    List<City> getCity(String pid);

    List<CityModel> tree();

    PageInfo<TestPo> find(PageReq page);
}
