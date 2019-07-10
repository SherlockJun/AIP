package com.chenankj.aip.speech.xunfei;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chenankj.aip.exception.AipException;
import com.chenankj.aip.exception.ErrorCode;
import com.chenankj.aip.util.AudioUtil;
import com.chenankj.aip.util.FileUtil;
import com.chenankj.aip.util.PropertiesUtil;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 讯飞语音识别client
 *
 * @author XieJun
 */
public class AsrClient {

    private static final String CONFIG_FILE = "config.properties";
    private static final String AUDIO_EXTENTION_WAV = ".wav";
    private static final Map<String, Boolean> supportedFileMap = new HashMap<>();

    static {
        Properties pro = PropertiesUtil.getPropertiesInstance(CONFIG_FILE);
        String supportedFiles = pro.getProperty("audio.support.ext");
        String[] supportedFileArray = supportedFiles.split(",");
        for(String supportedFile : supportedFileArray){
            supportedFileMap.put(supportedFile, true);
        }
    }

    /**
     * 讯飞语音识别
     *
     * 因为有可能校验文件格式后要转换文件格式，
     * 转换后的文件还需要校验文件大小，所以先校验文件格式，再校验文件大小
     *
     * @param path
     *          文件路径
     * @return
     */
    public static String asr(String path){

        String result = null;
        boolean tempFileFlag = false;
        try {
            File audio = new File(path);
            //校验文件是否存在
            if(!audio.exists()){
                throw new AipException(ErrorCode.Err0001);
            }
            //音频文件格式校验
            if(!isSupportedFile(audio)){
                throw new AipException(ErrorCode.Err0003);
            }
            //校验是否讯飞支持的音频文件格式
            if(!LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO.isSupportedAudios(audio)
                    && !LfasrType.LFASR_STANDARD_RECORDED_AUDIO.isSupportedAudios(audio)){
                path = convertAmrAudioToWav(audio);
                tempFileFlag = true;
            }
            //音频文件大小校验
            if(getFileSize(audio) > 500*1024*1024){
                throw new AipException(ErrorCode.Err0002);
            }
            LfasrClientImp lfasrClientImp = new LfasrClientImp();
            Message message = lfasrClientImp.lfasrUpload(path, LfasrType.LFASR_STANDARD_RECORDED_AUDIO);
            if (message.getOk() != 0 || StringUtils.isEmpty(message.getData())) {
                //录音文件预处理失败
                throw new AipException(ErrorCode.Err0004);
            }
            String taskId = message.getData();

            while (true) {
                System.out.println("sleep a while,wait for task progress complete!");
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message taskProgress = lfasrClientImp.lfasrGetProgress(taskId);
                if (taskProgress.getOk() == 0) {
                    if (taskProgress.getErr_no() != 0) {
                        //任务失败
                        System.out.println("任务失败：" + JSON.toJSONString(taskProgress));
                    }
                    String taskStatus = taskProgress.getData();
                    if (JSON.parseObject(taskStatus).getInteger("status") == 9) {
                        System.out.println("任务完成！");
                        break;
                    }

                    System.out.println("任务处理中：" + taskStatus);
                } else {
                    //获取任务进度失败
                    System.out.println("获取任务进度失败！");
                }
            }

            //获取结果
            Message taskResult = lfasrClientImp.lfasrGetResult(taskId);
            if(taskResult == null){
                throw new AipException(ErrorCode.Err0005);
            }
            if(taskResult.getOk() != 0){
                throw new AipException(ErrorCode.Err0006);
            }
            result = getContents(taskResult.getData());
            //删除格式转换后的临时文件
            if(tempFileFlag){
//                FileUtil.deleteFile(new File(path));
                try {
                    FileUtils.forceDeleteOnExit(new File(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (LfasrException e) {
            e.printStackTrace();
            throw new AipException(ErrorCode.Err0007, e.getMessage());
        }
        return result;
    }

    private static long getFileSize(File file){
        if(file != null && file.exists()){
            long length = file.length();
            return length;
        }else{
            return 0;
        }
    }

    private static String getContents(String data){
        if(StringUtils.isNotBlank(data) && StringUtils.isNotEmpty(data)){
            JSONArray jsonArray = JSONObject.parseArray(data);
            JSONObject jsonObject = null;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);
                stringBuilder.append(jsonObject.getString("onebest"));
            }
            return stringBuilder.toString();
        }else{
            return null;
        }
    }

    private static boolean isSupportedFile(File file){
        String ext = FileUtil.getFileExtention(file.getAbsolutePath());
        try {
            Boolean flag = supportedFileMap.get(ext);
            return flag != null ? flag : false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static String convertAmrAudioToWav(File sourceFile){
        Properties pro = PropertiesUtil.getPropertiesInstance(CONFIG_FILE);
        String dir = pro.getProperty("store_path");
        String wavFileName = "Wav_" + Calendar.getInstance().getTimeInMillis() + AUDIO_EXTENTION_WAV;
        File wavFile = new File(dir, wavFileName);
        AudioUtil.convertAmrToWav(sourceFile, wavFile);
        if(wavFile.length() > 0){
            return wavFile.getAbsolutePath();
        }
        throw new AipException(ErrorCode.Err015);
    }

}
