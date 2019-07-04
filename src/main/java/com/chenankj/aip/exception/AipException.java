package com.chenankj.aip.exception;

/**
 * AipException
 *
 * @author XieJun
 */
public class AipException extends RuntimeException {

    private ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public AipException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public AipException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AipException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AipException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public AipException(ErrorCode errorCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}
