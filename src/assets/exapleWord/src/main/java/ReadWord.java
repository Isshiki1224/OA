import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import util.ChineseNumToArabicNumUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReadWord {

    public static void main(String[] args) {
        List<File> files = FileScanner.FileScan();
        for(File file : files){
            List<String> list = ReadWord.readWord(file);
            String name = file.getName();
            name = name.substring(0,name.indexOf("."));
            ReadWord.writeExcel(list,name);
        }
    }

    private static void writeExcel(List<String> list,String name) {
        //写入excel
        //第一步，创建一个workbook对应一个excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        //第二步，在workbook中创建一个sheet对应excel中的sheet
        HSSFSheet sheet = workbook.createSheet("法律法规");
        //第三步，在sheet表中添加表头第0行，老版本的poi对sheet的行列有限制
        HSSFRow row = sheet.createRow(0);
        //第四步，创建单元格，设置表头
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("章");
        cell = row.createCell(1);
        cell.setCellValue("章内容");
        cell = row.createCell(2);
        cell.setCellValue("节");
        cell = row.createCell(3);
        cell.setCellValue("节内容");
        cell = row.createCell(4);
        cell.setCellValue("条");
        cell = row.createCell(5);
        cell.setCellValue("内容");

        //第五步，写入实体数据
        int i = 0;
        for (String str : list) {
            HSSFRow row1 = sheet.createRow(i + 1);
            HSSFCell cell1 = row1.createCell(0);
            HSSFCell cell2 = row1.createCell(1);
            HSSFCell cell3 = row1.createCell(2);
            HSSFCell cell4 = row1.createCell(3);
            HSSFCell cell5 = row1.createCell(4);
            HSSFCell cell6 = row1.createCell(5);


//            if(i == 0 && !str.startsWith("第")){
//                cell1.setCellValue(str);
//            }

            int startStr = str.indexOf("第");
            int index1 = str.indexOf("章");
            int index2 = str.indexOf("节");
            int index3 = str.indexOf("条");
            String str1 = str.substring(0, index1 + 1);
            String str2 = str.substring(0, index2 + 1);
            String str3 = str.substring(0, index3 + 1);

            if (!str1.equals("") && str1.length() < 5 && str.startsWith("第")) {
                cell2.setCellValue(str);
            }
            if (!str2.equals("") && str2.length() < 4 && str.startsWith("第")) {
                cell4.setCellValue(str);
            }
            if (!str3.equals("") && str3.length() < 8 && str.startsWith("第")) {
                String cnum = str.substring(str.indexOf("第") + 1, str.indexOf("条"));

                int itemNum = ChineseNumToArabicNumUtil.chineseNumToArabicNum(cnum);
                String subStr = str.substring(str.indexOf("条")+1).trim();
                cell6.setCellValue(subStr);
                cell5.setCellValue(itemNum);
            }
            if (!str.startsWith("第") && i != 0) {
                HSSFCell lastCell = sheet.getRow(i).getCell(4);
                String cellValue = "";
                switch (lastCell.getCellType()) {
                    //字符串类型
                    case STRING:
                        cellValue = lastCell.getStringCellValue().trim();
                        break;
                    //数值类型
                    case NUMERIC:
                        cellValue = new DecimalFormat("0.#").format(lastCell.getNumericCellValue());
                        break;
                    default: //其它类型，取空串吧
                        cellValue = "";
                        break;
                }
                int itemNum = 0;
                if(cellValue.equals("")){
                    cell5.setCellValue("");
                }else{
                    itemNum = Integer.parseInt(cellValue);
                }
                cell5.setCellValue(itemNum);
                cell6.setCellValue(str.trim());
            }
            i++;
        }
        //将文件保存到指定的位置
        FileOutputStream fos;
        try {
            File f1 = new File(System.getProperty("user.dir"));
            File parentFile = f1.getParentFile();
            fos = new FileOutputStream(parentFile.getAbsolutePath() +"/" + name + ".xls");
            workbook.write(fos);
            System.out.println("写入成功");
            fos.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static List<String> readWord(File file) {
        List<String> list = new ArrayList<>();
        String buffer = "";
        try {
            //读取word文件
            String path = file.getName();
            if (path.endsWith(".doc")) {
                //ClassPathResource classPathResource = new ClassPathResource(path);
                FileInputStream fileInputStream = new FileInputStream(file);
                WordExtractor wordExtractor = new WordExtractor(fileInputStream);
                buffer = wordExtractor.getText();
            } else if (path.endsWith("docx")) {
                //ClassPathResource classPathResource = new ClassPathResource(path);
                //XWPFDocument xdoc = new XWPFDocument(classPathResource.getInputStream());
                FileInputStream fileInputStream = new FileInputStream(file);
                XWPFDocument xdoc = new XWPFDocument(fileInputStream);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(xdoc);
                buffer = extractor.getText();
            } else {
                System.out.println("此文件不是word文件！");
            }
            String[] ss = buffer.split("\n|\r");
            for (String s : ss) {
                if(!s.equals("")){
                    //System.out.println(s);
                    list.add(s);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
