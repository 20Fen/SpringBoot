package com.example.demo;

import jxl.read.biff.BiffException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

//@RunWith(SpringRunner.class)
//@SpringBootTest

public class DemoApplicationTests {

    @Test
    public void excel() throws Exception {
//        Excel导出
        File file = new File("/Users/apple/Documents/1.xlsx"); // 创建文件对象
        Workbook wb = Workbook.getWorkbook(file); // 从文件流中获取Excel工作区对象（WorkBook）
        Sheet sheet = wb.getSheet(0); // 从工作区中取得页（Sheet）
        for (int i = 0; i < sheet.getRows(); i++) { // 循环打印Excel表中的内容
            for (int j = 0; j < sheet.getColumns(); j++) {
                Cell cell = sheet.getCell(j, i);
                System.out.printf(cell.getContents() + " ");
            }
            System.out.println();
            wb.close();
        }
    }


}
