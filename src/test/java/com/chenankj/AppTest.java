package com.chenankj;

import static org.junit.Assert.assertTrue;
import com.chenankj.aip.ocr.OcrClient;
import com.chenankj.aip.util.ImageUtil;
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
        String jpgFile = "E:\\chenankeji\\广东省应急厅\\ocr识别\\7082137cbf89dbddef7384556ea216a.jpg";
        String tifFile = "E:\\chenankeji\\广东省应急厅\\ocr识别\\7082137cbf89dbddef7384556ea216a.tif";
        String res = OcrClient.basicGeneral(tifFile,new HashMap<String, String>());
        System.out.println(res);
    }

    @Test
    public void testConvertImage(){
        String path = "E:\\chenankeji\\广东省应急厅\\ocr识别\\7082137cbf89dbddef7384556ea216a.tif";
        ImageUtil.convertToJpg(path);
    }

}
