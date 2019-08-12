package com.github.hollykunge.security.admin.rpc;

import com.alibaba.fastjson.JSON;
import com.github.hollykunge.security.admin.biz.OrgBiz;
import com.github.hollykunge.security.admin.biz.UserBiz;
import com.github.hollykunge.security.admin.constant.AdminCommonConstant;
import com.github.hollykunge.security.admin.entity.User;
import com.github.hollykunge.security.admin.rpc.service.PermissionService;
import com.github.hollykunge.security.api.vo.authority.FrontPermission;
import com.github.hollykunge.security.api.vo.user.UserInfo;
import com.github.hollykunge.security.common.vo.OrgUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author 协同设计小组
 * @create 2017-06-21 8:15
 */
@RestController
@RequestMapping("api")
public class UserRest {


    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserBiz userBiz;

    @Autowired
    private OrgBiz orgBiz;

//    @Cache(key="permission")
    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    public @ResponseBody
    List<FrontPermission> getAllPermission(){
        return permissionService.getAllPermission();
    }

    @RequestMapping(value = "/user/un/{userId}/permissions", method = RequestMethod.GET)
    public @ResponseBody List<FrontPermission> getPermissionByUserId(@PathVariable("userId") String userId){
        return permissionService.getPermissionByUserId(userId);
    }

    @RequestMapping(value = "/user/validate", method = RequestMethod.POST)
    public @ResponseBody UserInfo validate(String pid,String password){
        return permissionService.validate(pid,password);
    }

    @RequestMapping(value = "/user/info", method = RequestMethod.POST)
    public @ResponseBody UserInfo info(String userId){
        User user = userBiz.getUserByUserId(userId);
        UserInfo info = new UserInfo();
        if(user==null){
            return info;
        }
        BeanUtils.copyProperties(user, info);
        info.setId(user.getId());
        return info;
    }

    @RequestMapping(value = "/user/list", method = RequestMethod.POST)
    public @ResponseBody List<UserInfo> userList(@RequestParam String userIds){
        List<UserInfo> userInfos = new ArrayList<UserInfo>();
        if (!StringUtils.isEmpty(userIds)) {
            String[] ids = userIds.split(",");
            for (String m : ids) {
                User user = userBiz.getUserByUserId(m);
                UserInfo info = new UserInfo();
                if(user==null){
                    continue;
                }
                BeanUtils.copyProperties(user, info);
                info.setId(user.getId());
                userInfos.add(info);
            }
        }
        return userInfos;
    }

    @RequestMapping(value = "/user/all", method = RequestMethod.POST)
    public @ResponseBody List<User> all(){
        List<User> users = userBiz.getUsers();
//        List<User> infos = new ArrayList<User>();
//        BeanUtils.copyProperties(users, infos);
        return users;
    }

    /**
     * 组织用户树枝包含用户接口
     * @param parentTreeId 默认root
     * @return
     */
    @RequestMapping(value = "/orgUsers", method = RequestMethod.GET)
    public @ResponseBody String orgUsers(String parentTreeId) {
        if(StringUtils.isEmpty(parentTreeId)){
            parentTreeId = AdminCommonConstant.ROOT;
        }
        List<OrgUser> tree = orgBiz.getOrg(parentTreeId);
        return JSON.toJSONString(tree);
    }

    /**
    *@Description: 人员列表是否跨场所
    *@Param: String userId集合(","分隔)
    *@return: bool
    *@Author: 忠
    *@date: 2019/7/16
    */
    @RequestMapping(value = "/isCross", method = RequestMethod.GET)
    public @ResponseBody Boolean isCross(String userList){
        List<UserInfo> userInfos = new ArrayList<UserInfo>();
        String userOrg = "";
        if (!StringUtils.isEmpty(userList)) {
            String[] ids = userList.split(",");
            for (String m : ids) {
                User user = userBiz.getUserByUserId(m);
                if (userOrg.isEmpty()){
                    userOrg = user.getOrgCode();
                }else {
                    if (!userOrg.equals(user.getOrgCode())){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
