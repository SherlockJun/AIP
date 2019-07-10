package com.chenankj.aip.ocr;

import com.baidu.aip.ocr.AipOcr;
import com.chenankj.aip.exception.AipException;
import com.chenankj.aip.exception.ErrorCode;
import com.chenankj.aip.util.FileUtil;
import com.chenankj.aip.util.ImageUtil;
import com.chenankj.aip.util.PropertiesUtil;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class OcrClient {

    private static AipOcr aipOcr = null;
    private static final String BAIDU_APP_INFO_FILE = "baiduConfig.properties";
    private static Map<String, Boolean> supportedImagesMap = new HashMap();
    private static Map<String, Boolean> supportedImageForOcrMap = new HashMap<>();
    private static Map<String, Boolean> supportedCompressFileForOcrMap = new HashMap<>();

    static {
        Properties pro = PropertiesUtil.getPropertiesInstance(BAIDU_APP_INFO_FILE);
        String supportedImages = pro.getProperty("image.support.ext");
        String[] supportedImageArray = supportedImages.split(",");
        for (String supportedImage : supportedImageArray){
            supportedImagesMap.put(supportedImage, true);
        }
        String supportedImagesForOcr = pro.getProperty("baidu.ocr.support.ext");
        String[] supportedImagesForOcrArray = supportedImagesForOcr.split(",");
        for (String supportedImageForOcr : supportedImagesForOcrArray){
            supportedImageForOcrMap.put(supportedImageForOcr, true);
        }
        String supportedCompressFileForOcr = pro.getProperty("file.compress.support.ext");
        String[] supportedCompressFileArray = supportedCompressFileForOcr.split(",");
        for(String supportedCompressFile : supportedCompressFileArray){
            supportedCompressFileForOcrMap.put(supportedCompressFile, true);
        }
        aipOcr = new AipOcr(pro.getProperty("baidu.app1.appid"),
                pro.getProperty("baidu.app1.appkey"),
                pro.getProperty("baidu.app1.secretkey"));
    }

    public OcrClient(){
        initAipOcrInstance();
    }

    private void initAipOcrInstance(){
        if(aipOcr == null){
            synchronized (OcrClient.class){
                if(aipOcr == null){
                    Properties pro = PropertiesUtil.getPropertiesInstance(BAIDU_APP_INFO_FILE);
                    aipOcr = new AipOcr(pro.getProperty("baidu.app1.appid"),
                            pro.getProperty("baidu.app1.appkey"),
                            pro.getProperty("baidu.app1.secretkey"));
                }
            }
        }
    }

    /**
     * @param file 图片文件或者压缩文件
     * @param options ocr识别参数
     * @return 识别结果
     * @throws IOException IOException
     */
    public static String basicGeneral(File file, HashMap<String, String> options) {
        if(file == null || !file.exists()){
            throw new AipException(ErrorCode.Err013);
        }
        if(isSupportedImage(file.getAbsolutePath())){
            return basicGeneralForImage(file.getAbsolutePath(), new HashMap<>());
        }
        else if(isSupportedCompressFile(file)){
            return basicGeneralForZip(file);
        }else{
            throw new AipException(ErrorCode.Err014);
        }
    }

    /**
     *
     * 如果超出免费额度，切换baidu app；
     *
     *
     * @param path
     * @param options
     * @return
     */
    public static String basicGeneralForImage(String path, HashMap<String, String> options){
        boolean tempFileFlag = false;
        //校验文件路径所表示的文件是否存在
        if(!FileUtil.isFileExist(path)){
            throw new AipException(ErrorCode.Err008);
        }
        //校验是否支持的格式，不支持的格式抛出异常
        if(!isSupportedImage(path)){
            throw new AipException(ErrorCode.Err009);
        }
        //如果是tif格式或者...格式，先转换为jpeg格式
        if(!isSupportedImageForOcr(path)){
            path = ImageUtil.convertToJpg(path);
            tempFileFlag = true;
        }
        //TODO XieJun 2019-07-05 限制识别文件的大小

        if(aipOcr != null){
           String content = getContent(aipOcr.basicGeneral(path, options));
           if(tempFileFlag){
                FileUtil.deleteFile(new File(path));
           }
           return content;
        }else{
            return null;
        }
    }

    private static String getContent(JSONObject jsonObject){
        StringBuilder strBuilder = new StringBuilder();
        if(jsonObject != null && !jsonObject.isNull("words_result")){
            JSONArray jsonArray = jsonObject.getJSONArray("words_result");
            int len = jsonArray.length();
            JSONObject wordsJsonObj = null;
            for (int i = 0; i < len; i++) {
                wordsJsonObj = (JSONObject) jsonArray.get(i);
                strBuilder.append(wordsJsonObj.getString("words")).append("||");
            }
            return strBuilder.toString();
        }else {
            return null;
        }
    }

    private static boolean isSupportedImage(String path){
        String ext = FileUtil.getFileExtention(path);
        try {
            Boolean flag = supportedImagesMap.get(ext.toLowerCase());
            return flag != null ? flag : false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isSupportedImageForOcr(String path){
        String ext = FileUtil.getFileExtention(path);
        try {
            Boolean flag = supportedImageForOcrMap.get(ext);
            return flag != null ? flag : false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isSupportedCompressFile(File file){
        String ext = FileUtil.getFileExtention(file.getAbsolutePath());
        try {
            Boolean flag = supportedCompressFileForOcrMap.get(ext);
            return flag != null ? flag : false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String basicGeneralForZip(File zipFile) {
        //校验文件路径所表示的文件是否存在
        if(!FileUtil.isFileExist(zipFile)){
            throw new AipException(ErrorCode.Err008);
        }
        Properties properties = PropertiesUtil.getPropertiesInstance(BAIDU_APP_INFO_FILE);
        String parentDir = properties.getProperty("image.temp.path");
        String dir = String.valueOf(Calendar.getInstance().getTimeInMillis());
        File outputDir = null;
        try {
            outputDir = FileUtil.createFileDirectory(parentDir, dir);
        }catch (IOException e){
            throw new AipException(ErrorCode.Err016);
        }
        List<File> files = FileUtil.extractAllFile(zipFile, outputDir);
        if(files == null || files.isEmpty()){
            throw new AipException(ErrorCode.Err012);
        }
        StringBuilder stringBuilder = new StringBuilder();
        String content = null;
        for(File file : files){
            content = basicGeneralForImage(file.getAbsolutePath(), new HashMap<>());
            stringBuilder.append(content).append("&&&&&");
        }
        FileUtil.deleteFile(outputDir);
        return stringBuilder.toString();
    }

}