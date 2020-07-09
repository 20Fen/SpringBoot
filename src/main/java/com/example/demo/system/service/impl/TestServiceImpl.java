package com.example.demo.system.service.impl;

import com.example.demo.system.dao.mapper.TestMapper;
import com.example.demo.system.model.bo.TestBo;
import com.example.demo.system.model.po.*;
import com.example.demo.system.service.TestService;
import com.example.demo.system.util.*;
import com.exception.CustomException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Description: service 实现类
 */
@Log4j2
@Service
public class TestServiceImpl implements TestService {

    @Resource
    private TestMapper testMapper;

    @Value("${paths}")
    private String path;

    @Value("${ceshiCount}")
    private Integer ceshiCount;

    /**
     * Description: 根据条件进行查询
     */
    @Override
    public PageResult<List<TestPo>> findAll(Integer pageNum, Integer pageSize, Map<String, Object> map) {
        //前台必须传分页值
        //调用分页插件,执行的语句必须在插件的下面
        int offset = pageNum != null ? pageNum : 1;
        int limit = pageSize != null ? pageSize : 10;
        PageHelper.startPage(offset, limit);
        Page<TestPo> testPos = testMapper.findAll(map);
        //强制分页
        List listResult = PageUtil.startPage(testPos, pageNum, pageSize);
        //页数
        int pages = (int)Math.ceil((double) testPos.size() / pageSize);
        return new PageResult<>(listResult,pageNum,pageSize,testPos.size(),pages);
    }

    @Override
    public PageInfo<TestPo> find(PageReq pageReq) {
        int count = 0;
        page(pageReq);
        List<TestPo> testPos = testMapper.find();

        return new PageInfo<>(testPos);
    }

    /**
     * Description: 新建和修改数据
     */
    @Override
    @Transactional
    public String insert(TestBo testBo) throws Exception {

        Map<String, Object> map = new HashMap<>();
        //UUID
        String planNoId = UUID.randomUUID().toString();

        String planNo = testBo.getPlanNo();
        planNoId = StringUtils.isEmpty(planNo) ? planNoId : planNo;

        Date date=new Date();
        String formatDate = DateUtil.formatDate(date);

        TestPo testPo = new TestPo();
        testPo.setPlanNo(planNoId);
        testPo.setStatTime(testBo.getStatTime());
        testPo.setEndTime(testBo.getEndTime());
        testPo.setCreateTime(formatDate);
        //判断PlanNo是否为空
        if (StringUtils.isEmpty(planNo)) {
            //为空就是新建
            testPo.setId(UUID.randomUUID().toString());
            //调用执行新建语句
            testMapper.insertTest(testPo);
            //超过3条就删除老数据
            del();
        } else {
            //不为空就是修改
            testPo.setPlanNo(planNo);
            map.put("planNo", planNo);
            //调用执行查询语句
            TestPo testPo1 = testMapper.getById(map);
            if (null == testPo1) {
                throw new CustomException("数据不存在");
            }
            testPo.setId(testPo1.getId());
            testPo.setUpdateTime(formatDate);
            //调用执行修改语句
            testMapper.updataTest(testPo);
        }
        return planNoId;
    }

    /**
     * Description: 根据ID查询数据
     */
    @Override
    @Transactional
    public TestPo getById(String planNo) {

        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(planNo)) {
            throw new CustomException("planNo编号不能为空");
        }
        map.put("planNo", planNo);
        //调用执行查询语句
        TestPo test = testMapper.getById(map);
        if (null == test) {
            throw new CustomException("查询数据不存在");
        }
        return test;
    }

    /**
     * Description: 根据ID查询上个月数据
     */
    @Override
    @Transactional
    public TestPo getByIdMonth(String planNo) {

        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(planNo)) {
            throw new CustomException("planNo编号不能为空");
        }
        String lastMonth = DateUtil.getPreviousMonthDayBegin();
        String endMonth = DateUtil.getPreviousMonthDayEnd();
        map.put("planNo", planNo);
        map.put("statTime", lastMonth);
        map.put("endTime", endMonth);
        //调用执行查询语句
        TestPo test = testMapper.getByIdMonth(map);
        if (StringUtils.isEmpty(test)) {
            return new TestPo();
        }
        return test;
    }


    /**
     * Description: 批量删除数据并且删除文件
     */
    @Override
    @Transactional
    public String delete(Test testPoList) throws Exception {

        Map<String, Object> map = new HashMap<>();
            if (StringUtils.isEmpty(testPoList)) {
                throw new CustomException("planNo编号不能为空");
            }
        for (TestPo testPo : testPoList.getList()) {
            map.put("planNo", testPo.getPlanNo());

            //调用执行查询语句
            TestPo test = testMapper.getById(map);
            if (null == test) {
                throw new CustomException("数据不存在");
            }
            if (null != test.getUrl() && !test.getUrl().equals("")) {
                //得到数据库中的文件路径
                Path path1 = Paths.get(test.getUrl());
                //删除文件
                boolean exists = Files.deleteIfExists(path1);
                if (!exists) {
                    throw new CustomException("删除文件失败");
                }
                //按照 / 进行截取
                String path = test.getUrl().substring(0, test.getUrl().lastIndexOf(File.separator));
                //删除文件夹
                TestUtil.delFolder(path);
            }}
            //调用执行删除语句
            map.put("list",testPoList.getList());
            Integer integer = testMapper.deleteByIdAll(map);
            if (0 != integer) {
                return "1";
            }
        return null;
    }

    /**
     * Description: 删除文件并更新数据库字段
     */
    @Override
    public String deleteUrl(String planNo) throws Exception {

        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(planNo)) {
            throw new Exception("planNo为空");
        }
        map.put("planNo", planNo);
        //调用执行查询语句
        TestPo test = testMapper.getById(map);
        if (null == test) {
            throw new CustomException("数据不存在");
        }
        //得到数据库中的文件路径
        Path path = Paths.get(test.getUrl());
        //删除文件
        boolean exists = Files.deleteIfExists(path);
        if (!exists) {
            throw new CustomException("删除文件失败");
        }
        map.put("filename", "");
        map.put("url", "");
        //        调用修改数据库字段语句
        testMapper.updataTestDoc(map);
        return test.getPlanNo();
    }

    /**
     * Description: 上传文件
     */
    @Override
    public String upload(String planNo, MultipartFile file) throws Exception {

        Map<String, Object> map = new HashMap<>();
        if (file.getSize() == 0) {
            throw new CustomException("文件内容不不能为空");
        }
        if (StringUtils.isEmpty(planNo) || null == file) {
            throw new CustomException("请输入必传值");
        }
        map.put("planNo", planNo);
        //拼接上传路径
        String savePath = path + File.separator + planNo;
        //检查路径是否存在
        TestUtil.checkFilePath(savePath);
        //得到上传的文件名
        String filename = file.getOriginalFilename();
        //调用执行查询语句
        map.put("doc",filename);
        Integer count = testMapper.getFilename(map);
        //判断路径是否为空
        if (count > 0) {
            throw new CustomException("文件已存在");
        }
        try {
            //进行上传
            Integer integer = saveFile(savePath, file, filename);
            if (integer != 1) {
                throw new CustomException("上传文件失败");
            }
        } catch (IOException e) {
            log.error(e);
        }
        map.put("planNo", planNo);
        map.put("filename", filename);
        map.put("url", savePath + File.separator + filename);
        //更新到数据库中
        Integer doc = testMapper.updataTestDoc(map);
        if (doc == 1) {
            return filename;
        }
        return "2";
    }


    /**
     * @description 文件上传
     */
    private Integer saveFile(String savePath, MultipartFile file, String filename) throws Exception {
        Integer num = checkFileSize(file.getSize(), savePath);
        if (num == 1) {
            //得到上传的文件的字节
            byte[] bytes = file.getBytes();
            //上传的文件的全路径
            Path path = Paths.get(savePath + File.separator + filename);
            //调用方法上传
            Files.write(path, bytes);
        }
        return 1;
    }

    /**
     * @description 检测文件大小
     */
    private Integer checkFileSize(long size, String savePath)  {
        //获取存储当前文件后剩余的磁盘空间
        Long space = TestUtil.getFreeDiskSpace(savePath);
        File file = new File(savePath);
        //获取当前磁盘空间
        long usableSpace = file.getUsableSpace();
        log.info("-------------可用磁盘空间大小----" + usableSpace / 1024 / 1024 + "M--------");
        log.info("-------------上传文件大小----" + size / 1024 / 1024 + "M--------");
        //判断剩余的磁盘空间是否小于当前上传的文件大小
        if (space < size) {
            throw new CustomException("磁盘空间不足");
        }
        return 1;
    }

    @Override
    public TestPo getById(Map<String, Object> map) {
        return testMapper.getById(map);
    }


    /**
     * Description: 删除数据并且删除多个文件文件
     */
    @Override
    @Transactional
    public String deleteUrlAll(String planNo) throws IOException {

        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(planNo)) {
            throw new CustomException("planNo编号不能为空");
        }
        map.put("planNo", planNo);
        //调用执行查询语句
        TestPo1 test = testMapper.getByIdAll(map);
        if (StringUtils.isEmpty(test)) {
            throw new CustomException("数据不存在");
        }
        String url = "";
        if (!CollectionUtils.isEmpty(test.getList())) {
            for (Image image : test.getList()) {
                if (null != image.getUrl() && !image.getUrl().equals("")) {
                    url = image.getUrl();
                    //得到数据库中的文件路径
                    Path path1 = Paths.get(url);
                    //删除文件
                    boolean exists = Files.deleteIfExists(path1);
                    if (!exists) {
                        throw new CustomException("删除文件失败");
                    }
                }
            }
            if (StringUtils.isEmpty(url)) {
                throw new CustomException("路径不存在");
            }
            //按照 / 进行截取
            String path = url.substring(0, url.lastIndexOf(File.separator));
            //删除文件夹
            TestUtil.delFolder(path);
            Integer image1 = testMapper.deleteByIdImage(map);
            if (1 == image1) {
                return "1";
            }
        }
        //调用执行删除语句
        Integer ceshi = testMapper.deleteById(map);
        if (1 == ceshi) {
            return "1";
        }
        return null;
    }

    /**
     * 删除老数据
     */
    public void del() {

       String tableName = TableAll.table.replaceAll("-","");
        testMapper.del(tableName,ceshiCount);
    }

    /**
     * Description: 根据ID查询区域
     */
    @Override
    public List<City> getCity(String pId) {

        Map<String,Object> map=new HashMap<>();
        List<City> list= new ArrayList<>();
        //调用执行查询语句
        map.put("pid",pId);
        List<City> tests = testMapper.getCity(map);
        return tests;
    }
    /**
     * Description: 查询区域组装树
     */
    @Override
    public List<CityModel> tree() {

        List<CityModel> list=new ArrayList<>();
        List<CityModel> cityModels;
        Map<String,Object> map=new HashMap<>();
        //调用执行查询语句
        List<City> tests = testMapper.getCity(map);

        for (City test : tests) {

            CityModel cityModel =new CityModel();
            cityModel.setId(test.getId());
            cityModel.setPid(test.getPid());
            cityModel.setName(test.getName());
            if(test.getPid().equals("0")){
                cityModels = buildResourceTree(tests, test.getId());
            } else {
                cityModels = buildResourceTree(tests, test.getPid());
            }

            cityModel.setCitys(cityModels);
            list.add(cityModel);
        }

        return list;
            }

    private List<CityModel> buildResourceTree(List<City> tests, String id) {

        List<CityModel> list=new ArrayList<>();
        for (City test : tests) {
            String pid = test.getPid();
            String id1 = test.getId();
            if(!pid.equals("0") && pid.equals(id)){
                CityModel cityModel =new CityModel();
                cityModel.setId(test.getId());
                cityModel.setPid(test.getPid());
                cityModel.setName(test.getName());

                List<CityModel> list1 = buildResourceTree(tests, id1);
                cityModel.setCitys(list1);
                list.add(cityModel);
            }
        }
        return list;
    }


    /**
     *  分页
     */
    private void page(PageReq pageReq){
        if(pageReq.isNeedPage()){

            int pageNum = pageReq.getPage() != null && Integer.parseInt(pageReq.getPage()) > 0 ? Integer.parseInt(pageReq.getPage()) : 1;
            int limit = pageReq.getPageSize() != null ? Integer.parseInt(pageReq.getPageSize()) : 10;

            PageHelper.startPage(pageNum,limit );
        }
    }

}