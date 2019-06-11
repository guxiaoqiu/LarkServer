package com.github.hollykunge.security.entity;

import lombok.Data;
import org.nutz.dao.entity.annotation.Table;

import javax.persistence.Column;

/**
 * @description: 卡片设置- 卡片信息主表
 * @author zhhongyu
 * @since 2019年06月1日
 */
@Data
@Table("PORTAL_CARD_INFO")
public class CardInfo {
    @Column(name = "ID")
    private String id;

    @Column(name = "TITLE ")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "URL")
    private String url;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DELETED")
    private String deleted;

    @Column(name = "ICON")
    private String icon;

}
