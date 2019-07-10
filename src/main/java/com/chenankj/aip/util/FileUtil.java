package com.chenankj.aip.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static boolean isFileExist(File file){
        if(file != null && file.exists()){
            return true;
        }else{
            return false;
        }
    }

    public static List<File> extractAllFile(File file, File outputDir){
        List<File> files = null;
        if(isFileExist(file)){
            files = new ArrayList<>();
            ZipUtil.unpack(file, outputDir);
            if(outputDir.isDirectory()){
                 files = Arrays.asList(outputDir.listFiles());
            }
        }
        return files;
    }

    public static File createFileDirectory(String parentDir, String subDir) throws IOException {
        Path path = Files.createDirectories(Paths.get(parentDir, subDir));
        return path.toFile();
    }

    public static boolean deleteFile(File file) {
        return FileUtils.deleteQuietly(file);
    }

    public static String getFileExtention(String path){
        String extention = null;
        if(StringUtils.isNotBlank(path) && StringUtils.isNotEmpty(path)){
            int index = path.lastIndexOf(".");
            if(index > 0){
                extention = path.substring(index + 1);
            }
        }
        return extention;
    }

}
