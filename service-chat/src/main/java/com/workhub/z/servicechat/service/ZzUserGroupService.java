package com.workhub.z.servicechat.service;

import com.workhub.z.servicechat.entity.ZzUserGroup;
import java.util.List;

/**
 * 用户群组映射表(ZzUserGroup)表服务接口
 *
 * @author 忠
 * @since 2019-05-10 14:22:54
 */
public interface ZzUserGroupService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ZzUserGroup queryById(String id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<ZzUserGroup> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param zzUserGroup 实例对象
     * @return 实例对象
     */
    ZzUserGroup insert(ZzUserGroup zzUserGroup);

    /**
     * 修改数据
     *
     * @param zzUserGroup 实例对象
     * @return 实例对象
     */
    ZzUserGroup update(ZzUserGroup zzUserGroup);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String id);

}