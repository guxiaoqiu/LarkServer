package com.github.hollykunge.security.common.msg;

/**
 * Created by 协同设计小组 on 2017/8/23.
 */
public class BaseResponse {

    private int status = 200;
    private String message;
    private String timestamp = System.currentTimeMillis()+"";



    public BaseResponse(int status, String message,String timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public BaseResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
