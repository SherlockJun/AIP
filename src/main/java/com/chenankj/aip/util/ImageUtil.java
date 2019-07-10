package com.chenankj.aip.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

public class ImageUtil {

    private static final String CONFIG_PATH = "baiduConfig.properties";
    private static final String IMAGE_EXTENTION_JPG = ".jpg";

    public static String convertToJpg(String file){
        String jpgFilePath = null;
        final BufferedImage tif;
        try {
            tif = ImageIO.read(new File(file));
            Properties properties = PropertiesUtil.getPropertiesInstance(CONFIG_PATH);
            String fileName = "Image_" + Calendar.getInstance().getTimeInMillis();
            String directoryName = properties.getProperty("image.temp.path");
            boolean flag = ImageIO.write(tif, "jpeg", new File(directoryName + fileName + IMAGE_EXTENTION_JPG));
            if(flag){
                jpgFilePath = directoryName + fileName + IMAGE_EXTENTION_JPG;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpgFilePath;
    }

}
