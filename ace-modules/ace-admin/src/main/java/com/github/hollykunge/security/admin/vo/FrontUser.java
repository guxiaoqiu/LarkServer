package com.github.hollykunge.security.admin.vo;

import lombok.Data;

/**
 * 业务对象：前端获取用户信息
 * @author 协同设计小组
 * @date 2017/8/22
 */
@Data
public class FrontUser {
    //系统内id
    private String id;
    //身份证号 username->pId
    private String pId;
    //用户姓名
    private String name;
    //性别
    private String gender;

    private String orgCode;
    //自述信息
    private String description;
    //密级
    private String secretLevel;
    //头像
    private String avatar;
    //状态0离职1在职
    private Integer status;
    //电话 telephone->mobile
    private Integer mobile;
    //系统内删除状态0未删除 暂无
    private Integer deleted;
    //角色
    private String roleId;

    private UserRole userRole;

}
