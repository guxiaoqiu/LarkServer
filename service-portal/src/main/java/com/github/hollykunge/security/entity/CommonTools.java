package com.github.hollykunge.security.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @description: 常用工具
 * @author: dd
 * @since: 2019-06-08
 */
@Data
@Table(name = "PORTAL_COMMONTOOLS")
public class CommonTools {
    @Column(name = "ID")
    @Id
    private String id;
    @Column(name = "URI")
    private String uri;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "ORG_CODE")
    private String orgCode;

    @Column(name = "ORG_NAME")
    private String orgName;

}
