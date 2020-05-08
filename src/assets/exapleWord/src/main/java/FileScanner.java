import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileScanner {
    public static List<File> FileScan() {
        List<File> files = new ArrayList<>();
//        File f1 = new File("D:\\legals");
        File f1 = new File(System.getProperty("user.dir"));
        System.out.println(System.getProperty("user.dir"));
        File parentFile = f1.getParentFile();
        System.out.println(parentFile.getAbsolutePath());
        if (!parentFile.exists()) {
            boolean mkdir = parentFile.mkdir();
        }
//        File excelFile = new File(parentFile.getAbsolutePath() + "/excelLegals");
//        if (!excelFile.exists()) {
//            boolean mkdir = excelFile.mkdir();
//        }
        File[] f1s = parentFile.listFiles();
        if (f1s != null) {
            for (File f2 : f1s) {
                if (!f2.isDirectory() && f2.getName().endsWith(".docx")) {
                    System.out.println(f2.getName());
                    files.add(f2);
                    System.out.println(files);
                }
            }
        }
        return files;
    }
}
