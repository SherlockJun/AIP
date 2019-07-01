package com.chenankj.aip.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

    public static Properties getPropertiesInstance(File file){
        Properties pro = null;
        try {
            FileInputStream in = new FileInputStream(file);
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
