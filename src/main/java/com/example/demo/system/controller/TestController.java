package com.example.demo.system.controller;

import com.example.demo.system.model.bo.FindAllTest;
import com.example.demo.system.model.po.TestPo;
import com.example.demo.system.service.TestService;
import com.example.demo.system.util.AjaxResult;
import com.example.demo.system.util.CustomException;
import com.example.demo.system.util.ReturnInfo;
import com.example.demo.system.util.TestUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: controller restful风格
 */

@RestController
@Api(tags = "springBoot测试")
public class TestController extends BaseController {

    @Resource
    private TestService testService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ApiOperation("按条件查询，查询全部")
    public PageInfo<TestPo> findAll(@RequestParam(required = true, value = "page") Integer page,
                                    @RequestParam(required = true, value = "pageSize") Integer pageSize,
                                    @RequestParam(required = false, value = "planNo") String planNo,
                                    @RequestParam(required = false, value = "statTime")String statTime,
                                    @RequestParam(required = false, value = "endTime")String endTime,
                                    @RequestParam(required = false, value = "createTime")String createTime) throws Exception {

        Map<String,Object> map=new HashMap();
        map.put("planNo",planNo);
        map.put("statTime",statTime);
        map.put("endTime",endTime);
        map.put("createTime",createTime);
        PageInfo<TestPo> all = testService.findAll(page, pageSize, map);
        return all;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ApiOperation("添加数据")
    public AjaxResult insert(@Valid @RequestBody TestPo testPo) throws Exception {

        String testId = testService.insert(testPo);
        if (StringUtils.isEmpty(testPo.getPlanNo())) {
            return success(ReturnInfo.SAVE_SUCCESS_MSG, testId);
        } else {
            return success(ReturnInfo.UPDATE_SUCCESS_MSG, testId);
        }
    }

    @RequestMapping(value = "/byId/{planNo}", method = RequestMethod.GET)
    @ApiOperation("详情查看")
    public AjaxResult byId(@PathVariable("planNo")String planNo) throws Exception {

        TestPo test = testService.getById(planNo);
        if (StringUtils.isEmpty(test)) {
            return success(ReturnInfo.QUERY_FAIL_MSG);
        }
        return success((ReturnInfo.QUERY_SUCCESS_MSG), test);
    }

    @RequestMapping(value = "/byIdMonth/{planNo}", method = RequestMethod.GET)
    @ApiOperation("根据id查询上个月数据")
    public AjaxResult byIdMonth(@PathVariable("planNo")String planNo) throws Exception {

        TestPo test = testService.getByIdMonth(planNo);
        if (StringUtils.isEmpty(test)) {
            return success(ReturnInfo.QUERY_FAIL_MSG);
        }
        return success((ReturnInfo.QUERY_SUCCESS_MSG), test);
    }


    @RequestMapping(value = "/data/{planNo}", method = RequestMethod.DELETE)
    @ApiOperation("删除数据")
    public AjaxResult deleteById(@PathVariable("planNo")String planNo) throws Exception {

        String byId = testService.deleteById(planNo);
        if (StringUtils.isEmpty(byId)) {
            return success(ReturnInfo.DEL_FAIL_MSG);
        }
        return success((ReturnInfo.DEL_SUCCESS_MSG), byId);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation("批量删除数据")
    public AjaxResult deleteById(String[] planNo) throws Exception {

        String byId = testService.delete(planNo);
        if (StringUtils.isEmpty(byId)) {
            return success(ReturnInfo.DEL_FAIL_MSG);
        }
        return success((ReturnInfo.DEL_SUCCESS_MSG), byId);
    }

    @RequestMapping(value = "/file/{planNo}", method = RequestMethod.DELETE)
    @ApiOperation("删除文件")
    public AjaxResult deleteUrl(@PathVariable("planNo") String planNo) throws Exception {

        String byId = testService.deleteUrl(planNo);
        if (StringUtils.isEmpty(byId)) {
            return success(ReturnInfo.DEL_FAIL_MSG);
        }
        return success((ReturnInfo.DEL_SUCCESS_MSG), byId);
    }

    @RequestMapping(value = "/upload/{planNo}", method = RequestMethod.POST)
    @ApiOperation("上传文件")
    public AjaxResult upload(@PathVariable("planNo") String planNo, @RequestParam("file")MultipartFile file) throws Exception {

        String name = testService.upload(planNo, file);
        if (name.equals("2")) {
            return success(ReturnInfo.UPLOAD_FAIL_MSG);
        }
        return success((ReturnInfo.UPLOAD_SUCCESS_MSG), name);
    }

    @RequestMapping(value = "/downLoad/{planNo}", method = RequestMethod.GET)
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
            return success(ReturnInfo.DOWNLOAD_ERROR_MSG);
        }
        String fileName = test.getDoc();
        String fileUrl = test.getUrl();
        File file = new File(fileUrl);
        //如果文件不存在
        if (!file.exists()) {
            return success(ReturnInfo.DOWNLOAD_ERROR_MSG);
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
                return success(ReturnInfo.UPLOAD_FAIL_MSG);
            }
            return success((ReturnInfo.UPLOAD_SUCCESS_MSG), name);
        }
        return success((ReturnInfo.UPLOAD_SUCCESS_MSG));
    }

}
