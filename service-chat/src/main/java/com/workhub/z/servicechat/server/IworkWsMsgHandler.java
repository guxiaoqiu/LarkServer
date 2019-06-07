package com.workhub.z.servicechat.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.workhub.z.servicechat.VO.MessageVO;
import com.workhub.z.servicechat.config.AsyncTaskConfig;
import com.workhub.z.servicechat.config.AsyncTaskService;
import com.workhub.z.servicechat.config.MessageType;
import com.workhub.z.servicechat.entity.ZzGroup;
import com.workhub.z.servicechat.entity.ZzGroupMsg;
import com.workhub.z.servicechat.entity.ZzPrivateMsg;
import com.workhub.z.servicechat.feign.IValidateService;
import com.workhub.z.servicechat.model.GroupTaskDto;
import com.workhub.z.servicechat.model.UserGroupDto;
import com.workhub.z.servicechat.model.UserListDto;
import com.workhub.z.servicechat.processor.ProcessMsg;
import com.workhub.z.servicechat.service.ZzGroupMsgService;
import com.workhub.z.servicechat.service.ZzGroupService;
import com.workhub.z.servicechat.service.ZzPrivateMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.server.handler.IWsMsgHandler;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

import static com.workhub.z.servicechat.config.MessageType.*;
import static com.workhub.z.servicechat.config.VoToEntity.GroupMsgVOToModel;
import static com.workhub.z.servicechat.config.VoToEntity.MsgVOToModel;
import static com.workhub.z.servicechat.config.common.checkUserOnline;

@Component
public class IworkWsMsgHandler implements IWsMsgHandler {
    private static Logger log = LoggerFactory.getLogger(IworkWsMsgHandler.class);
    private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncTaskConfig.class);
    private static AsyncTaskService asyncTaskService = context.getBean(AsyncTaskService.class);
    @Autowired
    protected ProcessMsg processMsg;

    @Autowired
    protected ZzGroupService groupService;
    @Autowired
    protected ZzPrivateMsgService privateMsgService;
    @Autowired
    protected ZzGroupMsgService groupMsgService;
    protected IValidateService iValidateService;
    private static IworkWsMsgHandler  serverHandler ;

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        serverHandler = this;
        serverHandler.groupService = this.groupService;
        serverHandler.privateMsgService = this.privateMsgService;
        serverHandler.iValidateService = this.iValidateService;
        serverHandler.processMsg = this.processMsg;
        // 初使化时将已静态化的Service实例化
    }

    public static IworkWsMsgHandler me = new IworkWsMsgHandler();

    private IworkWsMsgHandler() {

    }

    /**
     * 握手时走这个方法，业务可以在这里获取cookie，request参数等
     */
    @Override
    public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        String clientip = request.getClientIp();
        String token = request.getParam("token");
        String userid=request.getParam("userId");
        // 获取用户在线信息，如在线，踢掉他
        if(checkUserOnline(channelContext,userid)){
            Tio.unbindUser(channelContext.getGroupContext(),userid);
        }
//      用户token验证
//      iValidateService.validate(token);
        Tio.bindToken(channelContext,token);
//      前端 参数 绑定信息
        Tio.bindUser(channelContext,userid);
//      加入系统消息组
        Tio.bindGroup(channelContext, Const.GROUP_SYS);
//       根据握手信息，将用户绑定到群组
        // TODO: 2019/5/13 获取当前登录用户所有群组 绑定群组
//        List<ZzGroup> listGroupModel =  serverHandler.groupService.queryGroupListByUserId("11");
//        for (int i = 0; i < listGroupModel.size() ; i++) {
//            String groupid  =listGroupModel.get(i).getGroupId();
//            Aio.bindGroup(channelContext,groupid);
//            System.out.println("join group "+ listGroupModel.get(i).getGroupName());
//        }

        log.info("收到来自{}的ws握手包\r\n{}", clientip, request.toString());
        return httpResponse;
    }

    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        log.info("握手结束了\r\n{}", httpRequest.getClientIp(), httpRequest.toString());
    }

    /**
     * 字节消息（binaryType = arraybuffer）过来后会走这个方法
     */
    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }

    /**
     * 当客户端发close flag时，会走这个方法
     */
    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        Tio.remove(channelContext, "receive close flag");
        return
                null;
    }

    /*
     * 字符消息（binaryType = blob）过来后会走这个方法
     */
    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception {
        WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getAttribute();
        HttpRequest httpRequest = wsSessionContext.getHandshakeRequest();//获取websocket握手包
        try{
//            JSONObject jsonObject = JSONObject.parseObject(text);
//            String code = jsonObject.getString("
//
// code");
            if (Objects.equals("心跳内容", text)) {
                return null;
            }
//            String msg = jsonObject.getString("data");
            serverHandler.processMsg.process(channelContext,text);



//      Aio.bindUser(channelContext,userid);
//      Aio.bindGroup(channelContext, Const.GROUP_ID);

        //获取前端消息 展示
        if (log.isDebugEnabled()) {
            log.debug("握手包:{}", httpRequest);
        }

        log.info("收到ws消息:{}", text);
        String t = String.valueOf(text);
//        JSONObject jsonObject = JSON.parseObject(text);


        System.out.println(t);
//      String msg = channelContext.getClientNode().toString() + " 说：" + text;
//        String msg = t;
        //用tio-websocket，服务器发送到客户端的Packet都是WsResponse
        WsResponse wsResponse = WsResponse.fromText(text, IworkServerConfig.CHARSET);
//            Tio.sendToAll(channelContext.getGroupContext(),wsResponse);
//        //群发
//        Tio.bSendToGroup(channelContext.getGroupContext(), Const.GROUP_SYS, wsResponse);
        }catch (Exception e){
            log.error("发的是什么鬼东西"+text);
            return false;
        }
        //系统消息

//        Tio.sendToUser(channelContext.getGroupContext(),"123",wsResponse);
        //返回值是要发送给客户端的内容，一般都是返回null
        return null;
    }
}