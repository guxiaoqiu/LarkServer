package com.github.hollykunge.security.admin.rest;

import java.util.ArrayList;
import java.util.List;

import com.github.hollykunge.security.admin.biz.ResourceRoleMapBiz;
import com.github.hollykunge.security.admin.constant.AdminCommonConstant;
import com.github.hollykunge.security.admin.entity.Role;
import com.github.hollykunge.security.admin.vo.AuthorityMenuTree;
import com.github.hollykunge.security.admin.vo.GroupUsers;
import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.github.hollykunge.security.admin.biz.RoleBiz;
import com.github.hollykunge.security.admin.vo.GroupTree;
import com.github.hollykunge.security.common.rest.BaseController;
import com.github.hollykunge.security.common.util.TreeUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * ${DESCRIPTION}
 *
 * @author 协同设计小组
 * @create 2017-06-12 8:49
 */
@Controller
@RequestMapping("role")
@Api("角色模块")
public class RoleController extends BaseController<RoleBiz, Role> {

    /**
     * 批量修改角色用户
     * @param id 角色id
     * @param users 用户ids
     * @return
     */
    @RequestMapping(value = "/{id}/user", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse modifyUsers(@PathVariable int id,String users){
        baseBiz.modifyRoleUsers(id, users);
        return new ObjectRestResponse().rel(true);
    }

    /**
     * 批量修改角色菜单
     * @param id
     * @param menuTrees
     * @return
     */
    @RequestMapping(value = "/{id}/menu", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse modifyMenuAuthority(@PathVariable  int id, String menuTrees){
        String [] menus = menuTrees.split(",");
        baseBiz.modifyAuthorityMenu(id, menus);
        return new ObjectRestResponse().rel(true);
    }

    /**
     * 获取菜单和关联功能
     * @param id 角色id
     * @return
     */
    @RequestMapping(value = "/{id}/authority/menu", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<List<AuthorityMenuTree>> getMenuAuthority(@PathVariable  int id){
        return new ObjectRestResponse().data(baseBiz.getAuthorityMenu(id)).rel(true);
    }

    /**
     * 增加角色功能
     * @param id 角色id
     * @param menuId 菜单id
     * @param elementId 功能id
     * @return
     */
    @RequestMapping(value = "/{id}/authority/element/add", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse addElementAuthority(@PathVariable  int id,int menuId, int elementId){
        baseBiz.modifyAuthorityElement(id,menuId,elementId);
        return new ObjectRestResponse().rel(true);
    }

    @RequestMapping(value = "/{id}/authority/element/remove", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse removeElementAuthority(@PathVariable int id,int menuId, int elementId){
        baseBiz.removeAuthorityElement(id,menuId,elementId);
        return new ObjectRestResponse().rel(true);
    }

    @RequestMapping(value = "/{id}/authority/element", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<List<Integer>> getElementAuthority(@PathVariable  int id){
        return new ObjectRestResponse().data(baseBiz.getAuthorityElement(id)).rel(true);
    }


    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    @ResponseBody
    public List<GroupTree> tree(String name,String groupType) {
        if(StringUtils.isBlank(name)&&StringUtils.isBlank(groupType)) {
            return new ArrayList<GroupTree>();
        }
        Example example = new Example(Role.class);
        if (StringUtils.isNotBlank(name)) {
            example.createCriteria().andLike("name", "%" + name + "%");
        }
        if (StringUtils.isNotBlank(groupType)) {
            example.createCriteria().andEqualTo("groupType", groupType);
        }
        return  getTree(baseBiz.selectByExample(example), AdminCommonConstant.ROOT);
    }

    private List<GroupTree> getTree(List<Role> groups,int root) {
        List<GroupTree> trees = new ArrayList<GroupTree>();
        GroupTree node = null;
        for (Role group : groups) {
            node = new GroupTree();
            node.setLabel(group.getName());
            BeanUtils.copyProperties(group, node);
            trees.add(node);
        }
        return TreeUtil.bulid(trees,root) ;
    }
    @GetMapping("/roleList")
    public ListRestResponse roleList(){
        return null;
    }
}