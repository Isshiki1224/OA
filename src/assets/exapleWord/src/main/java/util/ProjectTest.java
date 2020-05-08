package util;

import lombok.Data;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectTest {
    private Object data;
    public static void main(String[] args) {
        String s1 = "第一章 总则";
        String s2 = "第十条.doc";
        String str1=s1.substring(0, s1.indexOf("章")+1);

        List<String> list = new ArrayList<String>();
        list.add("JavaWeb编程词典");  //向列表中添加数据
        list.add("Java编程词典");  //向列表中添加数据
        list.add("C#编程词典");  //向列表中添加数据
        list.add("ASP.NET编程词典");  //向列表中添加数据
        list.add("VC编程词典");  //向列表中添加数据
        list.add("SQL编程词典");  //向列表中添加数据

        List<String> subList = list.subList(0, 6);
        for(String s : subList){
            System.out.println(s);
        }

        String str = "江苏省航道养护管理办法";
        System.out.println(str.contains("航道管理"));


    }
}
