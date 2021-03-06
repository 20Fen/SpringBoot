package com.example.demo.system.util;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

/**
 * Description: 保留小数
 */
public class NumberUtil {

    /**
     * @Description: 获取小数2位有效数据
     */
    public static String getTwoPlace(String number) {
        String result = "";
        if (StringUtils.isNotBlank(number)) {
            Double doubleNumber=Double.valueOf(number);
            result = String.format("%.2f", doubleNumber);
        }
        return result;
    }

    /**
     * @Description: 获取小数4位有效数据
     */
    public static String getDecimal(String number, int length) {
        String result = "";
        if (StringUtils.isNotBlank(number)) {
            BigDecimal n4 = new BigDecimal(number);
            BigDecimal[] numberList = n4.divideAndRemainder(BigDecimal.valueOf(1));
            BigDecimal b = new BigDecimal(numberList[1].toString());
            BigDecimal divisor = BigDecimal.ONE;
            MathContext mc = new MathContext(length);
            String divide = b.divide(divisor, mc).toString();
            Double doubleResult = (numberList[0].add(new BigDecimal(divide))).doubleValue();
            result = doubleResult.toString();
        }
        return result;
    }

    /**
     * @Description: 整数后保留两位百分之小数
     */
    public static String getTwo(String number) {
        String result = null;
        DecimalFormat df=new DecimalFormat("0.00");
        if (StringUtils.isNotBlank(number)) {
            result = df.format(Integer.valueOf(number) * 100)+"%";
        }else {
            result = 0 + ".00%";;

        }
        return result;
    }
}

