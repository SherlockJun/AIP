package com.chenankj.aip.util;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static boolean isFileExist(String path){
        if(StringUtils.isNotBlank(path) && StringUtils.isNotEmpty(path)){
            Path filePath = Paths.get(path);
            if(Files.exists(filePath)){
                return true;
            }else {
                return false;
            }
        }else{
            return false;
        }
    }

}
