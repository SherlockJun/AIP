package com.chenankj.aip.exception;


/**
 * 错误码
 *
 * @author XieJun
 */
public enum ErrorCode {

    Err0001("Err0001","录音文件不存在!"),
    Err0002("Err0002","录音文件超过大小!"),
    Err0003("Err0003","不支持的录音文件格式!"),
    Err0004("Err0004","录音文件预处理失败!"),
    Err0005("Err0005","获取语音识别结果接口请求失败!"),
    Err0006("Err0006","获取语音识别结果失败!"),
    Err0007("Err0007","语音识别异常!"),
    Err008("Err0008","OCR识别文件不存在!"),
    Err009("Err009","OCR识别不支持的文件格式!"),
    Err010("Err010","OCR识别异常!"),
    Err011("Err011","未知异常!"),
    Err012("Err012","空的压缩文件!"),
    Err013("Err013","文件不存在!"),
    Err014("Err014","不支持的文件格式!"),
    Err015("Err015","amr格式转换wav格式错误!"),
    Err016("Err016","创建解压目标目录失败!");

    private String errCode;
    private String errMsg;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    ErrorCode(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
