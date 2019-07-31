package com.workhub.z.servicechat.service.impl;

import com.workhub.z.servicechat.dao.ZzLogDao;
import com.workhub.z.servicechat.entity.ZzChatLog;
import com.workhub.z.servicechat.service.ZzLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("zzLogService")
public class ZzLogServiceImpl implements ZzLogService {
    @Resource
    ZzLogDao zzLogDao;
   public int log(ZzChatLog zzChatLog){
        return  this.zzLogDao.log(zzChatLog);
    }
}
