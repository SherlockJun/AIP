package com.chenankj.aip.util;

import it.sauronsoftware.jave.AudioUtils;

import java.io.File;

public class AudioUtil {

    public static void convertAmrToWav(File sourceFile, File targetFile){
        if (FileUtil.isFileExist(sourceFile) && targetFile != null){
            AudioUtils.amrToWav(sourceFile, targetFile);
        }
    }

}
