package com.github.hollykunge.servicewebservice.controller;

import com.github.hollykunge.servicewebservice.model.EryuanUser;
import com.github.hollykunge.servicewebservice.service.EryuanUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "eryuan")
@EnableAutoConfiguration
public class EryuanUserController {

    @Autowired
    EryuanUserService eryuanUserService;

   // HttpServletResponse response;
    @RequestMapping(value = "/save")
public String saveEryuanUser(){
          System.out.println("开始执行导入........");
            eryuanUserService.saveEryuanUser();
            return "数据导入成功";

          }
    @GetMapping("/savexmlfile")
    public String saveOneXmlEryuanUser(){
        System.out.println("开始执行目录文件读取........");
         eryuanUserService.saveXmlFileEryuanUser();
          return "读文件目录的数据，导入成功";
    }
}
