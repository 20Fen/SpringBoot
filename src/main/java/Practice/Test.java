package Practice;

import com.example.demo.system.util.MD5;
import com.example.demo.system.util.NumberUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Description:测试类
 */
public class Test {
    public static void main(String[] args) {

        String keyword="i love you";
        String md5= DigestUtils.md5Hex(keyword);
        System.out.println("md5加密后："+"\n"+md5);
        String md5salt= MD5.md5PlusSalt(keyword);
        System.out.println("加盐后："+"\n"+md5salt);
        String word= MD5.md5MinusSalt(md5salt);
        System.out.println("解密后："+"\n"+word);
        String c="10";
        String effective = NumberUtil.getTwo(c);
        String a="10.12145464";
        System.out.println(effective);
        String effective1 = NumberUtil.getDecimal(a, 3);
        System.out.println(effective1);
        String twoPlace = NumberUtil.getTwoPlace(a);
        System.out.println(twoPlace);

//    按顺序获取三个数字加一个字母组合
        System.out.println(getRandom().size());
        List stringRandom = getStringRandom(getRandom());
        System.out.print(stringRandom);
    }

    public static List<String> getRandom() {

        List<String> list = new ArrayList();
        String b;
        for (int i = 0; i < 1000; i++) {
            b = i + "";
            int length = b.length();
            if(length == 1){
                b="00"+i;
            }
            if(length == 2){
                b="0"+i;
            }
            list.add(b);
        }
        return list;
    }

    public static List getStringRandom(List<String> list) {
        List list1=new ArrayList();
        for (int i = 65; i <= 90; i++) {
            for (String s : list) {
                String a3 = s.substring(0, 1);
                String a2 = s.substring(1, 2);
                String a1 = s.substring(2, 3);

                list1.add((char)i+""+a3+a2+a1);
                list1.add(a3+""+(char)i+a2+a1);
                list1.add(a3+""+a2+(char)i+a1);
                list1.add(a3+""+a2+a1+(char)i);
            }
        }
        return list1;
    }
}
