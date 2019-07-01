package com.chenankj.aip.util;

import java.io.File;
import java.net.URL;

public class ResourceUtil {

    public static File getFileFromResources(String path){
        ClassLoader classLoader = ResourceUtil.class.getClassLoader();
        URL resources = classLoader.getResource(path);
        if (resources == null){
            throw new IllegalArgumentException("file is not found!");
        }else{
            return new File(resources.getFile());
        }
    }

}
