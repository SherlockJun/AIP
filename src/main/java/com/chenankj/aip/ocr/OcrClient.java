package com.chenankj.aip.ocr;

import com.baidu.aip.ocr.AipOcr;
import com.chenankj.aip.util.PropertiesUtil;
import com.chenankj.aip.util.ResourceUtil;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Properties;

public class OcrClient {

    private static AipOcr aipOcr = null;
    private static final String BAIDU_APP_INFO_FILE = "config/baiduConfig.properties";

    public OcrClient(){
        initAipOcrInstance();
    }

    private void initAipOcrInstance(){
        if(aipOcr == null){
            synchronized (OcrClient.class){
                if(aipOcr == null){
                    Properties pro = PropertiesUtil.getPropertiesInstance(ResourceUtil.getFileFromResources(BAIDU_APP_INFO_FILE));
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
    public JSONObject basicGeneral(String path, HashMap<String, String> options){
        if(aipOcr != null){
            return aipOcr.basicGeneral(path, options);
        }else{
            return null;
        }
    }

}
