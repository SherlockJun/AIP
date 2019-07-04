package com.chenankj.aip.ocr;

import com.baidu.aip.ocr.AipOcr;
import com.chenankj.aip.exception.AipException;
import com.chenankj.aip.exception.ErrorCode;
import com.chenankj.aip.util.FileUtil;
import com.chenankj.aip.util.ImageUtil;
import com.chenankj.aip.util.PropertiesUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class OcrClient {

    private static AipOcr aipOcr = null;
    private static final String BAIDU_APP_INFO_FILE = "baiduConfig.properties";
    private static Map<String, Boolean> supportedImagesMap = new HashMap();
    private static Map<String, Boolean> supportedImageForOcrMap = new HashMap<>();

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
     *
     * 如果超出免费额度，切换baidu app；
     *
     *
     * @param path
     * @param options
     * @return
     */
    public static String basicGeneral(String path, HashMap<String, String> options){
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
        }
        if(aipOcr != null){
            return getContent(aipOcr.basicGeneral(path, options));
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
        String ext = ImageUtil.getFileExtention(path);
        try {
            Boolean flag = supportedImagesMap.get(ext.toLowerCase());
            return flag != null ? flag : false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isSupportedImageForOcr(String path){
        String ext = ImageUtil.getFileExtention(path);
        try {
            Boolean flag = supportedImageForOcrMap.get(ext);
            return flag != null ? flag : false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
