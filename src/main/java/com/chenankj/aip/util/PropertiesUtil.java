package com.chenankj.aip.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    public static Properties getPropertiesInstance(String path){
        Properties pro = null;
        try {
//            FileInputStream in = new FileInputStream(ResourceUtil.getFileFromResources(path));
            InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
            pro = new Properties();
            pro.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pro;
    }

}
