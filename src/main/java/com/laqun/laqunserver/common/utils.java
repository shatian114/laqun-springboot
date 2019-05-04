package com.laqun.laqunserver.common;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class utils {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String webPath = "";

    public static void wlToF(String fp, String s) {
        try {
            FileWriter fw = new FileWriter(fp);
            fw.append(s);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String rlFromF(String fp) {
        String s = "";
        String s2;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fp));
            s2 = s;
            while (true) {
                try {
                    String readL = br.readLine();
                    if (readL != null) {
                        s2 = s2 + readL;
                    } else {
                        br.close();
                        s = s2;
                        return s2;
                    }
                } catch (Exception e) {
                    return s2;
                }
            }
        } catch (Exception e2) {
            s2 = s;
            return s2;
        }
    }
}
