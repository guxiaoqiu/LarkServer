package com.workhub.z.servicechat.service.impl;

import com.workhub.z.servicechat.entity.ZzGroupMsg;
import com.workhub.z.servicechat.dao.ZzGroupMsgDao;
import com.workhub.z.servicechat.service.ZzGroupMsgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 群组消息表(ZzGroupMsg)表服务实现类
 *
 * @author 忠
 * @since 2019-05-10 11:38:02
 */
@Service("zzGroupMsgService")
public class ZzGroupMsgServiceImpl implements ZzGroupMsgService {
    @Resource
    private ZzGroupMsgDao zzGroupMsgDao;

    /**
     * 通过ID查询单条数据
     *
     * @param msgId 主键
     * @return 实例对象
     */
    @Override
    public ZzGroupMsg queryById(String msgId) {
        return this.zzGroupMsgDao.queryById(msgId);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<ZzGroupMsg> queryAllByLimit(int offset, int limit) {
        return this.zzGroupMsgDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param zzGroupMsg 实例对象
     * @return 实例对象
     */
    @Override
    public ZzGroupMsg insert(ZzGroupMsg zzGroupMsg) {
        this.zzGroupMsgDao.insert(zzGroupMsg);
        return zzGroupMsg;
    }

    /**
     * 修改数据
     *
     * @param zzGroupMsg 实例对象
     * @return 实例对象
     */
    @Override
    public ZzGroupMsg update(ZzGroupMsg zzGroupMsg) {
        this.zzGroupMsgDao.update(zzGroupMsg);
        return this.queryById(zzGroupMsg.getMsgId());
    }

    /**
     * 通过主键删除数据
     *
     * @param msgId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String msgId) {
        return this.zzGroupMsgDao.deleteById(msgId) > 0;
    }
}