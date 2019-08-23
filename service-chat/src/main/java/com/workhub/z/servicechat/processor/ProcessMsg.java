package com.workhub.z.servicechat.processor;

import com.alibaba.fastjson.JSONObject;
import com.workhub.z.servicechat.config.RandomId;
import com.workhub.z.servicechat.config.common;
import com.workhub.z.servicechat.entity.ZzUploadFile;
import com.workhub.z.servicechat.service.ZzGroupFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;

import java.util.Date;

import static com.workhub.z.servicechat.config.MessageType.*;

@Service
public class ProcessMsg extends AbstractMsgProcessor{

    @Autowired
    private ProcessPrivateMsg processPrivateMsg;
    @Autowired
    private ProcessEditGroup processEditGroup;
    @Autowired
    private ProcessGroupMsg processGroupMsg;
    @Autowired
    private ZzGroupFileService zzGroupFileService;
    private static Logger log = LoggerFactory.getLogger(ProcessMsg.class);
    /**
     * 消息处理入口
     */

    public Object process(ChannelContext channelContext, String msg) {
        try{
            JSONObject jsonObject = JSONObject.parseObject(msg);
            String code = jsonObject.getString("code");
            String message = jsonObject.getString("data");

            //文件上传信息更新begin
            try {
                String msgType = common.nulToEmptyString(common.getJsonStringKeyValue(message,"content.type"));
                //如果是文件或者图片上传
                if("2".equals(msgType)||"3".equals(msgType)){
                    ZzUploadFile zzUploadFile = new ZzUploadFile();
                    zzUploadFile.setId(RandomId.getUUID());
                    zzUploadFile.setUploadTime(new Date());
                    boolean isGroup = (Boolean) common.getJsonStringKeyValue(message,"isGroup");
                    zzUploadFile.setIsGroup(isGroup?"1":"0");
                    zzUploadFile.setFileId(common.nulToEmptyString(common.getJsonStringKeyValue(message,"content.id")));
                    zzUploadFile.setLevels(common.nulToEmptyString(common.getJsonStringKeyValue(message,"content.secretLevel")));
                    zzUploadFile.setReceiver(common.nulToEmptyString(common.getJsonStringKeyValue(message,"toId")));
                    zzUploadFile.setUserId(common.nulToEmptyString(common.getJsonStringKeyValue(message,"fromId")));
                    zzUploadFile.setUserName(common.nulToEmptyString(common.getJsonStringKeyValue(message,"username")));
                    zzUploadFile.setReceiverName(common.nulToEmptyString(common.nulToEmptyString(common.getJsonStringKeyValue(message,"toName"))));//接收人姓名
                    zzUploadFile.setSuccessFlg("1");//是否发送成功
                    zzUploadFile.setApproveFlg("0");//默认都是审批不通过
                    if(zzUploadFile.getIsGroup().equals("1")){
                        if(zzUploadFile.getLevels().equals("30")){//如果是非密文件
                            zzUploadFile.setApproveFlg("1");//直接审批通过
                        }
                    }
                    zzGroupFileService.fileRecord(zzUploadFile);
                }
            } catch (Exception e) {
                //异常记录到日志
                log.error(common.getExceptionMessage(e));
            }
            //文件上传信息更新end

//            processMsg(channelContext,msg,Integer.parseInt(code));

            switch (Integer.parseInt(code)){
                case SYS_MSG:
//                    Tio.sendToAll(channelContext.getGroupContext(),wsResponse);
                    break;
                case GROUP_MSG:
                   return processGroupMsg.sendMsg(channelContext,msg);
                case PRIVATE_MSG:
                    return  processPrivateMsg.sendMsg(channelContext,msg);
                case GROUP_EDIT:
                     processEditGroup.processManage(channelContext,message);
                    break;
                case GROUP_CREATE:
                    return processEditGroup.createGroup(channelContext,msg);
                case MSG_EDIT_READ:
                    JSONObject temp = JSONObject.parseObject(message);
                    super.deleteNoReadMsg(temp.getString("sender"),temp.getString("reviser"));
                    break;
                default:
                    System.out.println("你说的什么鬼");
                    break;
            }
        }catch (Exception e){
            System.out.println("别提了又错了"+ e.getMessage());
            return null;
        }
        return null;
    }
}
