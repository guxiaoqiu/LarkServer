package com.workhub.z.servicechat.service.imp;

//import com.github.hollykunge.security.api.vo.user.UserInfo;
import com.github.hollykunge.security.api.vo.user.UserInfo;
import com.workhub.z.servicechat.mapper.GroupMapper;
import com.workhub.z.servicechat.model.*;
import com.workhub.z.servicechat.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 讨论组内功能服务类
 * 
 * @author hanxu
 */
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupMapper groupMapper;
//
//	@Autowired
//	private MessageMapper messageMapper;

	@Override
	public List<GroupModel> queryGroupByUser(String userid) {
		return groupMapper.queryGroupByUser(userid);
	}

	/**
	 * 获得讨论组成员
	 * 
	 * @param groupId
	 *            :讨论组id
	 * @return
	 */
	@Override
	public List<UserInfo> queryGroupUser(String groupId) {
		return groupMapper.queryGroupUser(groupId);
	}
	
	/**
	 * 获取重要信息
	 * 
	 * @return
	 */
	@Override
	public List<GroupMsgModel> queryImportantInfo(String groupId) {
		return groupMapper.queryImportantInfo(groupId);
	}

	@Override
	public List<GroupMsgModel> queryGInfo(String groupId) {
		return null;
	}

	/**
	 * 导出讨论组聊天信息
	 * @return
	 */
//	@Override
//	public List<GroupMsgModel> queryGroupByUserqueryGInfo(String groupId) {
//		return groupMapper.queryGInfo(groupId);
//	}
	
	/**
	 * 获得讨论组信息
	 * 
	 * @return
	 */
	@Override
	public List<GroupModel> queryGroup() {
		return groupMapper.queryGroup();
	}


	/**
	 * 获得讨论组密级
	 *
	 * @return
	 */
	@Override
	public String queryGroupLevelsByGroupId(String groupid) {
		return groupMapper.queryGroupLevelsByGroupId(groupid);
	}

	/**
	 * 查询讨论组信息
	 * 
	 * @param groupId
	 *            :讨论组id
	 * @return
	 */
	@Override
	public GroupInfoModel queryGroupInfo(String groupId) {
		return groupMapper.queryGroupInfo(groupId);
	}

	/**
	 * 分页查询讨论组文件
	 * 
	 * @param groupId
	 *            ：讨论组id
	 * @param page
	 *            ：页面
	 * @param pageRow
	 *            ：每页显示数量
	 * @return
	 */


	/**
	 * 保存讨论组文件到数据库
	 * 
	 * @param groupFile
	 *            :文件对象
	 * @return
	 */
//	@Override
//	public int saveGroupFile(GroupFileModel groupFile) {
//		return groupMapper.saveGroupFile(groupFile);
//	}

	/**
	 * 创建讨论组
	 * 
	 * @param groupName
	 *            ：讨论组名称
	 * @param groupDescribe
	 *            ：讨论组描述
	 * @param userIdArray
	 *            ：讨论组成员列表
	 * @param userId
	 *            ：创建者
	 */
	@Override
	public String createGroup(String groupName, String groupDescribe, String[] userIdArray, String userId, String choose_name, String scop, String ispublic,String levels) {
		String groupId = String.valueOf((int) (Math.random() * 10000)) + String.valueOf(new Date().getTime());
		GroupInfoModel groupInfo = new GroupInfoModel();
		groupInfo.setGroupId(groupId);
		groupInfo.setGroupName(groupName);
		groupInfo.setGroupDescribe(groupDescribe);
		groupInfo.setCreator(userId);
		groupInfo.setIsdelete("0");
		groupInfo.setIsclose("0");
		groupInfo.setPname(choose_name);
		groupInfo.setScop(scop);
		groupInfo.setIspublic(ispublic);
		groupInfo.setLevels(levels);
		groupMapper.saveGroupInfo(groupInfo);

		for (int i = 0; i < userIdArray.length; i++) {
			GroupUserRefModel groupUserRef = new GroupUserRefModel();
			groupUserRef.setGroupId(groupId);
			groupUserRef.setUserId(userIdArray[i]);
			groupMapper.saveGroupUser(groupUserRef);
		}

		return groupId;
	}

	/**
	 * 更新讨论组
	 * 
	 * @param groupId
	 *            :讨论组id
	 * @param groupName
	 *            ：讨论组名称
	 * @param groupDescribe
	 *            ：讨论组描述
	 * @param userIdArray
	 *            ：讨论组人员
	 * @param userId
	 *            ：当前用户
	 */
	@Override
	public void updateGroup(String groupId, String groupName, String groupDescribe, String[] userIdArray, String userId) {
		// 新增讨论组信息
		GroupInfoModel groupInfo = new GroupInfoModel();
		groupInfo.setGroupId(groupId);
		groupInfo.setGroupName(groupName);
		groupInfo.setGroupDescribe(groupDescribe);
		groupInfo.setUpdator(userId);
		groupMapper.updateGroupInfo(groupInfo);

		// 删除讨论组内全部成员
		groupMapper.deleteGroupAllUser(groupId);

		// 新增讨论组新成员
		for (int i = 0; i < userIdArray.length; i++) {
			GroupUserRefModel groupUserRef = new GroupUserRefModel();
			groupUserRef.setGroupId(groupId);
			groupUserRef.setUserId(userIdArray[i]);
			groupMapper.saveGroupUser(groupUserRef);
		}
	}
	
	/**
	 * 新增讨论组成员
	 * 
	 * @param groupId
	 *            :讨论组id
	 * @param userId
	 *            ：当前用户
	 */
	@Override
	public void addGroupPerson(String groupId, String userId) {
		GroupUserRefModel groupUserRef = new GroupUserRefModel();
		groupUserRef.setGroupId(groupId);
		groupUserRef.setUserId(userId);
		groupMapper.saveGroupUser(groupUserRef);
	}
	
	/**
	 * 添加为重要信息
	 * 
	 */
	@Override
	public void updateImportantFlag(String meg) {
		groupMapper.updateImportantFlag(meg);
	}
	
	/**
	 *  更新系统信息已读\未读标识
	 */
	@Override
	public void updateSysMsgFlag(String notificationId) {
//		messageMapper.updateSysMsgFlag(notificationId);
	}

	/**
	 * 删除讨论组文件
	 * 
	 * @param groupId
	 *            :讨论组id
	 * @param fileId
	 *            ：文件id
	 * @param path
	 *            ：文件路径
	 * @param readPath
	 *            ：转码后的文件路径
	 */
	@Override
	public void deleteGroupFile(String groupId, String fileId, String path, String readPath) {
		// 删除数据库中记录
		groupMapper.deleteGroupFile(groupId, fileId);
		// 删除硬盘上的文件
//		FileUtil.deleteFile(path);
//		FileUtil.deleteFile(readPath);
	}
	
	/**
	 * 移除重要信息标记
	 * 
	 * @param msgId
	 *            :信息Id
	 * @param msg
	 *            ：信息内容
	 */
	@Override
	public void removeImportantInfo(String msgId, String msg) {
		// 删除数据库中记录
		groupMapper.removeImportantInfo(msgId, msg);
	}

	/**
	 * 逻辑删除讨论组
	 * 
	 * @param groupId
	 *            :讨论组id
	 * @param userId
	 *            ：用户id
	 */
	@Override
	public void deleteGroup(String groupId, String userId) {
		// 将讨论组是否删除字段改为1
		 groupMapper.deleteGroup(groupId, userId);
		// 删除最近联系人列表中该讨论组信息
		groupMapper.deleteGroupLatelyLinkman(groupId);
	}

	/**
	 * 通过讨论组文件id获得文件读取路径
	 * 
	 * @param fileId
	 *            :文件id
	 * @return
	 */
	@Override
	public String queryReadPathById(String fileId) {
		GroupFileModel file = groupMapper.queryGroupFileById(fileId);
		if (file != null) {
			return file.getReadPath();
		} else {
			return null;
		}
	}

	/**
	 * 更新讨论组上传文件转码路径信息，转码线程调用，更新转码路径信息
	 * 
	 * @param readPath
	 *            ：转码后文件路径
	 * @param fileId
	 *            ：文件id
	 * @param updator
	 *            ：更新人
	 */
	@Override
	public Boolean updateGroupFile(String readPath, String fileId, String updator) {
		int count = groupMapper.updateGroupFile(readPath, fileId, updator);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获得所有讨论组（不包含已删除的）
	 * @return
	 */
	@Override
	public List<GroupInfoModel> queryAllGroup() {
		return groupMapper.queryAllGroup();
	}

	/**
	 * 分页查询讨论组消息
	 * @param groupId	:讨论组id
	 * @param msg	：查找消息内容
	 * @param page	：页码
	 * @param rows	：每页显示行数
	 * @param startDate	:开始时间
	 * @param endDate	:结束时间
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<GroupMsgModel> queryGroupMsg(String groupId, String msg, int page, int rows, Date startDate,
			Date endDate) {
//		return (List<GroupMsgModel>)messageMapper.groupHiMsg(msg, groupId, (rows * (page-1)+1), rows*page, startDate, endDate);
	return null;
	}
	
	/**
	 * 分页查询时获得总数
	 * @param groupId	:讨论组id
	 * @param msg	：查找消息内容
	 * @param startDate	:开始时间
	 * @param endDate	:结束时间
	 * @return
	 */
	@Override
	public int queryGroupMsgCount(String groupId, String msg, Date startDate, Date endDate) {
//		return messageMapper.groupHiMsgCount(msg, groupId, startDate, endDate);
		return 0;
	}
	
	/**
	 * 分页获得讨论组信息
	 * @return
	 */
//	@Override
//	public PageObject queryGroupPage(int page, int rows, String name, String isDelete, Date startTime, Date endTime) {
//		PageObject pageObject = new PageObject();
//
//		pageObject.setObjectList(groupMapper.queryGroupPage(name, isDelete, startTime, endTime, (rows * (page - 1)+1), page*rows));
//		pageObject.setTotal(groupMapper.queryGroupPageCount(name, isDelete, startTime, endTime));
//
//		return pageObject;
//	}

	
	/**
	 * 退出群组
	 */
	@Override
	public void outGroup(String userId, String groupId) {
		groupMapper.outGroup(userId, groupId);
		
	}

	/**
	 * 根据groupID获得讨论组名称
	 * @param receiver
	 * @return
	 */
	@Override
	public GroupModel queryGroupInfoById(String receiver) {
		return groupMapper.queryGroupInfoById(receiver);
	}
	
	/**
	 * 关闭讨论组
	 * @return
	 */
	@Override
	public void closedGroup(String groupId) {
		groupMapper.closedGroup(groupId);
	}

	@Override
	public List<Map<String, Object>> queryGroupByUserAndGroupName(String userId, String groupName) {
		Map parm = new HashMap();
		parm.put("userId", userId);
		parm.put("groupName", groupName);
		return groupMapper.queryGroupByUserAndGroupName(parm);
	}


	@Override
	public void getBestTeams(String userId){

	}

	@Override
	public Object groupMsgsList(int page, String msgName, String sendUser, String msgLevels) {
		Integer row = 10;
//		PageObject pageObject = new PageObject();
//		pageObject.setPage(page);
		//获取群组消息
//		pageObject.setObjectList(groupMapper.groupMsgsList((row * (page - 1)+1), page*row, msgName, sendUser, msgLevels));
		//获取群组消息数量
//		pageObject.setTotal(groupMapper.groupMsgsListCount(msgName, sendUser, msgLevels));
		return null;
	}

	/**
     * 全部群组消息
     * @param page
     * @param msgName
     * @param sendUser
     * @param msgLevels
     * @return
     */
//	@Override
//	public Object groupMsgsList(int page, String msgName, String sendUser, String msgLevels) {
//		Integer row = 10;
//		PageObject pageObject = new PageObject();
//		pageObject.setPage(page);
//		//获取群组消息
//		pageObject.setObjectList(groupMapper.groupMsgsList((row * (page - 1)+1), page*row, msgName, sendUser, msgLevels));
//		//获取群组消息数量
//		pageObject.setTotal(groupMapper.groupMsgsListCount(msgName, sendUser, msgLevels));
//		return pageObject;
//	}
}




