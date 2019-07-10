package com.github.hollykunge.security.common.constant;

import org.tio.utils.time.Time;

/**
 *
 * @author 协同设计小组
 * @date 2017/8/29
 */
public class CommonConstants {
    public final static String RESOURCE_TYPE_MENU = "menu";
    public final static String RESOURCE_TYPE_BTN = "button";
    // 用户token异常
    public static final Integer EX_USER_INVALID_CODE = 40101;
    // 客户端token异常
    public static final Integer EX_CLIENT_INVALID_CODE = 40301;
    public static final Integer EX_CLIENT_FORBIDDEN_CODE = 40331;
    public static final Integer EX_OTHER_CODE = 500;
    public static final String CONTEXT_KEY_USER_ID = "currentUserId";
    public static final String CONTEXT_KEY_USERNAME = "currentUserName";
    public static final String CONTEXT_KEY_USER_NAME = "currentUser";
    public static final String CONTEXT_KEY_USER_TOKEN = "currentUserToken";
    public static final String JWT_KEY_USER_ID = "userId";
    public static final String JWT_KEY_NAME = "name";
    /** 公告通知交换机名称*/
    public static final String NOTICE_EXCHANGE = "noticeExchange";
    /** 公告队列名称 */
    public static final String NOTICE_QUEUE_NAMA = "noticeQueue";
    /**
     * 公告推送到研讨服务队列名称
     */
    public static final String NOTICE_TOWECHAT_QUEUE_NAMA = "noticeToChatService";
    /**
     * 通知公告发送到门户服务路由键
     */
    public static final String NOTICE_TOPORTAL_ROTEING_KEY = "notic_to_portal";
    /**
     * 通知公告发送到研讨服务路由键
     */
    public static final String NOTICE_TOCHAT_ROTEING_KEY = "notic_to_chat";
    /**
     * 通知死信队列名称
     */
    public final static String NOTICE_DEAD_QUEUENAME = "notic_dead_queue";

    /** tio用ip数据监控统计，时间段*/
    public static final Long DURATION_1 = Time.MINUTE_1 * 5;
    public static final Long[] IPSTAT_DURATIONS = new Long[]{DURATION_1};

    //密级判断
    public static final Integer EX_LEVELS = 40000;

    public static final String WEB_USERHOST = "userHost";
    public static final String WEB_USERNAME = "userName";
    public static final String WEB_USERID = "userId";
    /**
     * 院网关身份姓名
     */
    public static final String PERSON_ID_ARG = "dnname";
    /**
     * 院网关解码
     */
    public static final String PERSON_CHAR_SET = "ISO8859-1";
}
