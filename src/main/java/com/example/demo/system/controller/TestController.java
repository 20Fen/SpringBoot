package com.example.demo.system.controller;

import com.example.demo.system.model.bo.TestBo;
import com.example.demo.system.model.po.*;
import com.example.demo.system.service.TestService;
import com.example.demo.system.util.*;
import com.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description: controller restful风格
 */
@Log4j2
@RestController
@Api(tags = "springBoot测试")
public class TestController extends BaseController {

    @Autowired
    private TestService testService;

    @GetMapping(value = "/findAll")
    @ApiOperation("按条件查询，查询全部")
    public AjaxResult findAll(@RequestParam (required = true, value = "page") @ApiParam(value = "当前页") Integer page,
                              @RequestParam(required = true, value = "pageSize") @ApiParam(value = "每页数量") Integer pageSize,
                              @RequestParam(required = false, value = "planNo") String planNo,
                              @RequestParam(required = false, value = "statTime")String statTime,
                              @RequestParam(required = false, value = "endTime")String endTime,
                              @RequestParam(required = false, value = "createTime")String createTime) throws Exception {

        if (statTime != null && endTime != null) {
            if (statTime.compareTo(endTime) >= 0) {
                throw new CustomException("开始时间必须小于结束时间");
            }
        }
        Map<String,Object> map=new HashMap();
        map.put("planNo",planNo);
        map.put("statTime",statTime);
        map.put("endTime",endTime);
        map.put("createTime",createTime);
        Page1<TestPo> all = testService.findAll(page, pageSize, map);
        if (Objects.nonNull(all)) {
            return success(ReturnInfo.QUERY_SUCCESS_MSG, all);
        } else {
            return error(ReturnInfo.QUERY_FAIL_MSG);
        }
    }

    @GetMapping(value = "/find")
    @ApiOperation("查询全部")
    public AjaxResult find(@Valid PageReq page) {


        PageInfo<TestPo> all = testService.find(page);
        return success(ReturnInfo.QUERY_SUCCESS_MSG, all);
    }

    @PostMapping(value = "/test")
    @ApiOperation("添加数据")
    public AjaxResult insert(@Valid @RequestBody TestBo testBo, BindingResult result) throws Exception {
        testBo.validate(result);
        String testId = testService.insert(testBo);
        if (StringUtils.isEmpty(testBo.getPlanNo())) {
            return success(ReturnInfo.SAVE_SUCCESS_MSG, testId);
        } else {
            return success(ReturnInfo.UPDATE_SUCCESS_MSG, testId);
        }
    }

    @GetMapping(value = "/byId/{planNo}")
    @ApiOperation("详情查看")
    public AjaxResult byId(@PathVariable("planNo")String planNo)  {

        TestPo test = testService.getById(planNo);
        if (StringUtils.isEmpty(test)) {
            return error(ReturnInfo.QUERY_FAIL_MSG);
        }
        return success((ReturnInfo.QUERY_SUCCESS_MSG), test);
    }

    @GetMapping(value = "/byIdMonth/{planNo}")
    @ApiOperation("根据id查询上个月数据")
    public AjaxResult byIdMonth(@PathVariable("planNo")String planNo) throws Exception {

        TestPo test = testService.getByIdMonth(planNo);
        if (StringUtils.isEmpty(test)) {
            return error(ReturnInfo.QUERY_FAIL_MSG);
        }
        return success((ReturnInfo.QUERY_SUCCESS_MSG), test);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation("批量删除数据")
    public AjaxResult deleteById(@Valid @RequestBody Test test) throws Exception {
        String byId = testService.delete(test);
        if (StringUtils.isEmpty(byId)) {
            return error(ReturnInfo.DEL_FAIL_MSG);
        }
        return success((ReturnInfo.DEL_SUCCESS_MSG), byId);
    }

    @DeleteMapping(value = "/file/{planNo}")
    @ApiOperation("删除文件")
    public AjaxResult deleteUrl(@PathVariable("planNo") String planNo) throws Exception {

        String byId = testService.deleteUrl(planNo);
        if (StringUtils.isEmpty(byId)) {
            return error(ReturnInfo.DEL_FAIL_MSG);
        }
        return success((ReturnInfo.DEL_SUCCESS_MSG), byId);
    }

    @PostMapping(value = "/upload/{planNo}")
    @ApiOperation("上传文件")
    public AjaxResult upload(@PathVariable("planNo") String planNo, @RequestParam("file")MultipartFile file) throws Exception {

        String name = testService.upload(planNo, file);
        if (name.equals("2")) {
            return error(ReturnInfo.UPLOAD_FAIL_MSG);
        }
        return success((ReturnInfo.UPLOAD_SUCCESS_MSG), name);
    }

    @GetMapping(value = "/downLoad/{planNo}")
    @ApiOperation("下载文件")
    public AjaxResult downLoad(@PathVariable("planNo") String planNo, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(planNo)) {
            throw new CustomException("planNo为空");
        }
        map.put("planNo", planNo);
        TestPo test = testService.getById(map);
        if (null == test) {
            throw new CustomException("数据不存在");
        }
        if (null == test.getDoc() || test.getDoc().equals("") || null == test.getUrl() || test.getUrl().equals("")) {
            return error(ReturnInfo.DOWNLOAD_ERROR_MSG);
        }
        String fileName = test.getDoc();
        String fileUrl = test.getUrl();
        File file = new File(fileUrl);
        //如果文件不存在
        if (!file.exists()) {
            return error(ReturnInfo.DOWNLOAD_ERROR_MSG);
        }
//        匹配文件类型，保障上传的类型和下载的类型一致
        response.setHeader("Content-Type", "application/octet-stream");
//        设置在浏览器上下载的文件名
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        TestUtil.down(fileUrl, response);
        return success(ReturnInfo.DOWNLOAD_SUCCESS_MSG);
    }

    @RequestMapping(value = "/uploadAll/{planNo}", method = RequestMethod.POST)
    @ApiOperation("上传多个文件")
    public AjaxResult uploadAll(@PathVariable("planNo") String planNo, @RequestParam("file")MultipartFile[] file) throws Exception {

        for (MultipartFile multipartFile : file) {
            String name = testService.upload(planNo, multipartFile);
            if (name.equals("2")) {
                return error(ReturnInfo.UPLOAD_FAIL_MSG);
            }
            return success((ReturnInfo.UPLOAD_SUCCESS_MSG), name);
        }
        return success((ReturnInfo.UPLOAD_SUCCESS_MSG));
    }

    @RequestMapping(value = "/urlall/{planNo}", method = RequestMethod.DELETE)
    @ApiOperation("删除数据并删除对个文件")
    public AjaxResult deleteUrlAll(@PathVariable("planNo")String planNo) throws Exception {

        String byId = testService.deleteUrlAll(planNo);
        if (StringUtils.isEmpty(byId)) {
            return error(ReturnInfo.DEL_FAIL_MSG);
        }
        return success((ReturnInfo.DEL_SUCCESS_MSG), planNo);
    }

    @org.junit.Test
    public void upEquipmentDerat() throws Exception {
        String s = MD5.md5("123");
        System.out.println(s);
    }


    @PostMapping(value = "/city")
    @ApiOperation("省级联动")
    public AjaxResult getCity(String pid)  {

        List<City> test = testService.getCity(pid);
        if (StringUtils.isEmpty(test)) {
            return error(ReturnInfo.QUERY_FAIL_MSG);
        }
        return success((ReturnInfo.QUERY_SUCCESS_MSG), test);
    }


    @PostMapping(value = "/tree")
    @ApiOperation("城市查看")
    public AjaxResult tree()  {

        List<CityModel> test = testService.tree();
        if (StringUtils.isEmpty(test)) {
            return error(ReturnInfo.QUERY_FAIL_MSG);
        }
        return success((ReturnInfo.QUERY_SUCCESS_MSG), test);
    }
}
