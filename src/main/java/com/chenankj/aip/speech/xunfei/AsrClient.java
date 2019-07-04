package com.chenankj.aip.speech.xunfei;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chenankj.aip.exception.AipException;
import com.chenankj.aip.exception.ErrorCode;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 讯飞语音识别client
 *
 * @author XieJun
 */
public class AsrClient {

    /**
     * 讯飞语音识别
     *
     * @param path
     *          文件路径
     * @return
     */
    public static String asr(String path){

        String result = null;
        try {
            File audio = new File(path);
            //校验文件是否存在
            if(!audio.exists()){
                throw new AipException(ErrorCode.Err0001);
            }
            //音频文件大小校验
            if(getFileSize(audio) > 500*1024*1024){
                throw new AipException(ErrorCode.Err0002);
            }
            //音频文件格式校验
            if(!LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO.isSupportedAudios(audio)
                    && !LfasrType.LFASR_STANDARD_RECORDED_AUDIO.isSupportedAudios(audio)){
                throw new AipException(ErrorCode.Err0003);
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

}
