package com.workhub.larktools.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * author:admin
 * description:
 * date:2019/8/13 13:49
 **/
public class CommonUtil {
    private static Logger log = LoggerFactory.getLogger(CommonUtil.class);
    //objec转字符串 null-> ""
    public static String nulToEmptyString(Object object){
        try{
            if(object==null){
                return "";
            }
            return  object.toString();
        }catch (Exception e){
            e.printStackTrace();
            log.error(getExceptionMessage(e));
        }
        return  null;
    }
    //异常信息获取
    public static String getExceptionMessage(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
