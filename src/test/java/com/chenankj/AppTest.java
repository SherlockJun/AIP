package com.chenankj;

import static org.junit.Assert.assertTrue;

import com.chenankj.aip.ocr.OcrClient;
import com.chenankj.aip.speech.xunfei.AsrClient;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testBaiduOcr(){
        String path = "E:\\chenankeji\\广东省应急厅\\ocr识别\\7082137cbf89dbddef7384556ea216a.jpg";
        OcrClient ocrClient = new OcrClient();
        JSONObject res = ocrClient.basicGeneral(path,new HashMap<String, String>());
    }

    @Test
    public void testXunfeiAsr(){
       String res =  AsrClient.asr("lfasr.wav");
    }

}
