package com.workhub.z.servicechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.hollykunge.security.api.vo.user.UserInfo;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.workhub.z.servicechat.VO.GroupUserListVo;
import com.workhub.z.servicechat.VO.GroupVO;
import com.workhub.z.servicechat.config.common;
import com.workhub.z.servicechat.dao.ZzGroupDao;
import com.workhub.z.servicechat.dao.ZzUserGroupDao;
import com.workhub.z.servicechat.entity.ZzGroup;
import com.workhub.z.servicechat.entity.ZzUserGroup;
import com.workhub.z.servicechat.feign.IUserService;
import com.workhub.z.servicechat.service.ZzGroupService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.workhub.z.servicechat.config.RandomId.getUUID;
import static com.workhub.z.servicechat.config.common.putEntityNullToEmptyString;

/**
 * 群组表(ZzGroup)表服务实现类
 *
 * @author 忠
 * @since 2019-05-10 14:29:32
 */
@Service("zzGroupService")
public class ZzGroupServiceImpl implements ZzGroupService {
    @Resource
    private ZzGroupDao zzGroupDao;

    @Resource
    private ZzUserGroupDao zzUserGroupDao;

    @Autowired
    private IUserService iUserService;

    /**
     * 通过ID查询单条数据
     *
     * @param groupId 主键
     * @return 实例对象
     */
    @Override
    public ZzGroup queryById(String groupId) {
        return this.zzGroupDao.queryById(groupId);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<ZzGroup> queryAllByLimit(int offset, int limit) {
        return this.zzGroupDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param zzGroup 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public void insert(ZzGroup zzGroup) {
        try {
            putEntityNullToEmptyString(zzGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.zzGroupDao.addGroup(zzGroup);
//        return insert;
    }

    /**
     * 修改数据
     *
     * @param zzGroup 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public Integer update(ZzGroup zzGroup) {
        int update = this.zzGroupDao.update(zzGroup);
        return update;
    }

    /**
     * 通过主键删除数据
     *
     * @param groupId 主键
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deleteById(String groupId) {
        return this.zzGroupDao.deleteById(groupId) > 0;
    }

    @Override
    public PageInfo<GroupUserListVo> groupUserList(String id, int page, int size) throws Exception {
//        if (StringUtil.isEmpty(id)) throw new NullPointerException("id is null");
//        Page<Object> pageMassage = PageHelper.startPage(page, size);
//        pageMassage.setTotal(this.zzGroupDao.groupUserListTotal(id));
//        int startRow = pageMassage.getStartRow();
//        int endRow = pageMassage.getEndRow();
//        List<GroupUserListVo> groupUserListVos = this.zzGroupDao.groupUserList(id, startRow, endRow);
//        PageInfo<GroupUserListVo> pageInfoGroupInfo = new PageInfo<GroupUserListVo>();
//        if (groupUserListVos ==null || groupUserListVos.isEmpty()) return pageInfoGroupInfo;
//        List<String> setStr = new ArrayList<>();
//        groupUserListVos.stream().forEach(groupUserListVosList -> {
//            setStr.add(groupUserListVosList.getUserId());
//        });
//        List<UserInfo> userInfos = iUserService.userList(setStr);
//        groupUserListVos.stream().forEach(groupUserListVosList ->{
//            userInfos.stream().filter(userInfosFilter ->userInfosFilter.getId().equals(groupUserListVosList.getUserId())).forEach(userInfosList ->{
//                groupUserListVosList.setLevels("1"/*TODO*/);
//                groupUserListVosList.setFullName(userInfosList.getName());
//                groupUserListVosList.setPassword(userInfosList.getPassword());
//                groupUserListVosList.setVip(userInfosList.getDemo());//TODO
//            });
//        });
//        ZzGroup zzGroup = this.zzGroupDao.queryById(id);
//        if (zzGroup ==null) throw new RuntimeException("未查询到群组记录");
//
//        List<GroupUserListVo> resultList = this.orderByGroupUser(groupUserListVos, zzGroup.getCreator());
//
//        pageInfoGroupInfo.setList(resultList);
//        pageInfoGroupInfo.setTotal(pageMassage.getTotal());
//        pageInfoGroupInfo.setStartRow(startRow);
//        pageInfoGroupInfo.setEndRow(endRow);
//        pageInfoGroupInfo.setPages(pageMassage.getPages());
//        pageInfoGroupInfo.setPageNum(page);
//        pageInfoGroupInfo.setPageSize(size);
//        return pageInfoGroupInfo;
        return null;
    }

    @Override
    public Long groupUserListTotal(String groupId) throws Exception {
        return this.zzGroupDao.groupUserListTotal(groupId);
    }

    @Override
    public List<String> queryGroupUserIdListByGroupId(String groupId) {
        return this.zzGroupDao.queryGroupUserIdListByGroupId(groupId);
    }

    @Override
    public List<ZzGroup> queryGroupListByUserId(String id) throws Exception {
        return this.zzGroupDao.queryGroupListByUserId(id);
    }

    private List<GroupUserListVo> orderByGroupUser(List<GroupUserListVo> groupUserListVos,String creator) throws RuntimeException{
        List<GroupUserListVo> resultList = groupUserListVos.stream()
                .filter(listf -> !listf.getUserId().equals(creator)&&listf.getVip()!=0)
                .sorted((a, b) -> a.getVip() - b.getVip())
                .collect(Collectors.toList());
        try {
            resultList.add(0,groupUserListVos.stream()
                    .filter(listf -> listf.getUserId().equals(creator))
                    .collect(Collectors.toList())
                    .get(0));
        } catch (Exception e) {
            throw new RuntimeException("人员排序时未找到创建人");
        }
        resultList.addAll(groupUserListVos.stream()
                .filter(listf -> !listf.getUserId().equals(creator) && listf.getVip() != 0)
                .collect(Collectors.toList()));
        return resultList;
    }
    /**
     * 逻辑删除群
     * @param groupId 群id
     * @return  1成功；-1错误
     * @author zhuqz
     * @since 2019-06-11
     */
    @Override
    public String deleteGroupLogic(String groupId, String delFlg) {
        int i=this.zzGroupDao.deleteGroupLogic( groupId, delFlg);
        return  "1";
    }
    /**
     * 获取群成员列表
     */
    @Override
    public String getGroupUserList(String groupId) {

            List<String> userIdList = this.zzGroupDao.queryGroupUserIdListByGroupId(groupId);
            StringBuilder ids = new StringBuilder();
            System.out.println(ids.length());
            for(String temp:userIdList){
                ids.append(temp+",");
            }
            if(ids.length()>0){
                ids.setLength(ids.length()-1);
            }
            //测试使用，给前端返回测试数据
           /* List<UserInfoVO> data=new ArrayList<>();
            for(int i=0;i<5;i++){
                UserInfoVO vo=new UserInfoVO();
                vo.setId("id"+i);
                vo.setName("name"+i);
                vo.setOnline((i%2)+"");
                vo.setAvartar("http://10.11.24.5:80/group1/M00/00/00/CgxhIl0N9FOALihUAADc6-sdZkU837.jpg");
                data.add(vo);
            }
            ListRestResponse res = new ListRestResponse("200",data.size(),data);*/
            return  ids.toString();
    }
    //群组信息监控
    //param:page 页码 size 每页几条;group_name群组名称；creator创建人姓名；level密级；
    // dateBegin创建时间开始；dateEnd创建时间结束；pname项目名称；isclose是否关闭
    @Override
    public TableResultResponse<GroupVO> groupListMonitoring(Map<String,String> params) throws Exception{
        int page=Integer.valueOf(common.nulToEmptyString(params.get("page")));
        int size=Integer.valueOf(common.nulToEmptyString(params.get("size")));
        PageHelper.startPage(page, size);
        List<GroupVO> dataList =this.zzGroupDao.groupListMonitoring(params);
        //null的String类型属性转换空字符串
        common.putVoNullStringToEmptyString(dataList);
        UserInfo userInfo=null;
        for(GroupVO groupVO:dataList){
            userInfo=iUserService.info(common.nulToEmptyString(groupVO.getCreator()));
            groupVO.setCreatorName(userInfo==null?"":userInfo.getName());
        }
        PageInfo<GroupVO> pageInfo = new PageInfo<>(dataList);
        TableResultResponse<GroupVO> res = new TableResultResponse<GroupVO>(
                pageInfo.getPageSize(),
                pageInfo.getPageNum(),
                pageInfo.getPages(),
                pageInfo.getTotal(),
                pageInfo.getList()
        );
        return res;
    }

    @Override
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public void dissolveGroup(String groupId){
            zzUserGroupDao.deleteByGroupId(groupId);
            ZzGroup group = new ZzGroup();
            group.setGroupId(groupId);
            group.setIsdelete("1");
            zzGroupDao.update(group);
    }

    @Override
    public void removeMember(String groupId, String userId){
        zzUserGroupDao.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Override
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public void addMember(String groupId, String userIds){
        String userIdsTemp = "";
//        JSONArray userIdJson = JSONObject.parseArray(userIds);
        if(StringUtils.isEmpty(userIds)){
            throw new BaseException("添加的人员不能为空");
        }
        List<String> userIdJson = Arrays.asList(userIds.split(","));

        //更新关联表
        zzUserGroupDao.deleteByGroupId(groupId);
        for (int i = 0; i < userIdJson.size(); i++) {
            ZzUserGroup userGroup = new ZzUserGroup();
            userGroup.setCreatetime(new Date());
            userGroup.setId(getUUID());
            userGroup.setGroupId(groupId);
//            userGroup.setUserId(userIdJson.getString(i));
            userGroup.setUserId(userIdJson.get(i));
            zzUserGroupDao.insert(userGroup);
        }

        for (int i = 0; i < userIdJson.size(); i++) {
//            userIdsTemp += ","+userIdJson.getString(i);
            userIdsTemp += ","+userIdJson.get(i);
        }
        //判断是否跨域场所,直接用的ProcessEditGroup里面那个
        boolean isCross = false;
        if(!userIdsTemp.equals("")){
            userIdsTemp = userIdsTemp.substring(1);
            List<UserInfo> userInfoList = iUserService.userList(userIdsTemp);
            isCross = common.isGroupCross(userInfoList);
        }
        ZzGroup zzGroupInfo = new ZzGroup();
        zzGroupInfo.setGroupId(groupId);
        zzGroupInfo.setIscross(isCross?"1":"0");
        zzGroupDao.update(zzGroupInfo);

    }
}