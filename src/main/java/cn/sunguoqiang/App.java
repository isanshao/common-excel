package cn.sunguoqiang;

import cn.sunguoqiang.common.annotation.Excel;
import cn.sunguoqiang.common.utils.poi.ExcelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        List<A> list = new ArrayList<>();

        A a = new A();
        a.setA("aaa");
        a.setBa ("bbb");
        list.add(a);
        list.add(a);
        list.add(a);
        list.add(a);
        list.add(a);


        ExcelUtil<A> aExcelUtil = new ExcelUtil<A>(A.class);
        aExcelUtil.exportExcel("D:/a.xlsx", list, "测试","标题");


    }
}

class A{

    @Excel(name = "列表1")
    String a;

    @Excel(name = "列表2")
    String ba;


    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getBa() {
        return ba;
    }

    public void setBa(String ba) {
        this.ba = ba;
    }
}
