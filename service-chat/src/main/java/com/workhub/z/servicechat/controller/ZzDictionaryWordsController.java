package com.workhub.z.servicechat.controller;

import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.workhub.z.servicechat.config.RandomId;
import com.workhub.z.servicechat.config.common;
import com.workhub.z.servicechat.entity.ZzDictionaryWords;
import com.workhub.z.servicechat.service.ZzDictionaryWordsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 字典词汇表(ZzDictionaryWords)表控制层
 *
 * @author makejava
 * @since 2019-05-17 14:56:57
 */
@RestController
@RequestMapping("/zzDictionaryWords")
public class ZzDictionaryWordsController{
    private static Logger log = LoggerFactory.getLogger(ZzDictionaryWordsController.class);
    /**
     * 服务对象
     */
    @Autowired
    private ZzDictionaryWordsService zzDictionaryWordsService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/selectOne")
    public ObjectRestResponse selectOne(String id) {
        ZzDictionaryWords data = this.zzDictionaryWordsService.queryById(id);
        ObjectRestResponse res = new ObjectRestResponse();
        res.msg("200");
        res.rel(true);
        res.data(data);
        return res;
        //return this.zzDictionaryWordsService.queryById(id);
    }

    /**
     * 分页查询
     * 参数：pageNo pageSize wordType类型 wordCode编码 wordName词汇名称 isUse是否使用中 replaceWord替换词汇
     * @return
     */
    @PostMapping("/query")
    public TableResultResponse<ZzDictionaryWords> query(@RequestParam Map<String, Object> params){
        int page=Integer.valueOf(params.get("pageNo").toString());
        int size=Integer.valueOf( params.get("pageSize").toString());
        String type=(params.get("wordType"))==null?"":params.get("wordType").toString();
        String code=(params.get("wordCode"))==null?"":params.get("wordCode").toString();
        String name=(params.get("wordName"))==null?"":params.get("wordName").toString();
        String replace=(params.get("replaceWord"))==null?"":params.get("replaceWord").toString();
        String isUse=(params.get("isUse"))==null?"":params.get("isUse").toString();
        TableResultResponse<ZzDictionaryWords> pageInfo = null;
        try {
            pageInfo = this.zzDictionaryWordsService.query(page, size,type,code,name,replace,isUse);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        T data, int pageSize, int pageNo, int totalPage, int totalCount
        return pageInfo;
    }
    @PostMapping("/create")
    //参数：pageNo pageSize wordType类型 wordCode编码 wordName词汇名称 isUse是否使用中 replaceWord替换词汇
    public ObjectRestResponse insert(@RequestParam Map<String, Object> params){
        ZzDictionaryWords zzDictionaryWords = new ZzDictionaryWords();
        zzDictionaryWords.setWordName(common.nulToEmptyString(params.get("wordName")));
        zzDictionaryWords.setWordCode(common.nulToEmptyString(params.get("wordCode")));
        zzDictionaryWords.setWordType(common.nulToEmptyString(params.get("wordType")));
        zzDictionaryWords.setReplaceWord(common.nulToEmptyString(params.get("replaceWord")));
        String userID = request.getHeader("userId");
        zzDictionaryWords.setId(RandomId.getUUID());
        zzDictionaryWords.setCreateTime(new Date());
        zzDictionaryWords.setCreateUser(userID);
        try{
            common.putEntityNullToEmptyString(zzDictionaryWords);
        }catch(Exception e){
            e.printStackTrace();
            log.error(common.getExceptionMessage(e));
        }
        int insert=this.zzDictionaryWordsService.insert(zzDictionaryWords);

        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        objectRestResponse.data("成功");
        if (insert == 0){
            objectRestResponse.data("词汇已经存在");
            objectRestResponse.msg("500");
            objectRestResponse.rel(false);
        }

        return objectRestResponse;
    }

    @PutMapping ("/update")
    //参数：pageNo pageSize wordType类型 wordCode编码 wordName词汇名称 isUse是否使用中 replaceWord替换词汇,id主键
    public ObjectRestResponse update(@RequestParam Map<String, Object> params){
        ZzDictionaryWords zzDictionaryWords = new ZzDictionaryWords();
        zzDictionaryWords.setUpdateTime(new Date());
        String userID = request.getHeader("userId");
        zzDictionaryWords.setUpdateUser(userID);
        zzDictionaryWords.setWordName(common.nulToEmptyString(params.get("wordName")));
        zzDictionaryWords.setWordCode(common.nulToEmptyString(params.get("wordCode")));
        zzDictionaryWords.setWordType(common.nulToEmptyString(params.get("wordType")));
        zzDictionaryWords.setReplaceWord(common.nulToEmptyString(params.get("replaceWord")));
        zzDictionaryWords.setIsUse(common.nulToEmptyString(params.get("isUse")));
        zzDictionaryWords.setId(common.nulToEmptyString(params.get("id")));
        try{
            common.putEntityNullToEmptyString(zzDictionaryWords);
        }catch(Exception e){
            e.printStackTrace();
            log.error(common.getExceptionMessage(e));
        }
        /*Integer update = this.zzDictionaryWordsService.update(zzDictionaryWords);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        if (update == null){
            objectRestResponse.data("失败");
            return objectRestResponse;
        }*/

        int i = this.zzDictionaryWordsService.update(zzDictionaryWords);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        objectRestResponse.data("成功");
        if (i == 0){
            objectRestResponse.data("词汇已经存在");
            objectRestResponse.msg("500");
            objectRestResponse.rel(false);
        }
        return objectRestResponse;
    }
    //停用(这个接口暂时不用了，调用update)
    @PutMapping ("/stopUse")
    //flg=0停用1启用
    public ObjectRestResponse stopUse(@RequestParam Map<String, Object> params) throws Exception{
        String id = common.nulToEmptyString(params.get("id"));
        String flg = common.nulToEmptyString(params.get("isUse"));
        String userID = request.getHeader("userId");
        this.zzDictionaryWordsService.stopUse(id,flg,userID);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        objectRestResponse.data("成功");
        return objectRestResponse;
    }
    @DeleteMapping("/delete")
    public ObjectRestResponse delete(@RequestParam Map<String, Object> params){
        String id = common.nulToEmptyString(params.get("id"));
        /*boolean flag = this.zzDictionaryWordsService.deleteById(id);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.data(flag);*/
        this.zzDictionaryWordsService.deleteById(id);
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.msg("200");
        objectRestResponse.rel(true);
        objectRestResponse.data("成功");
        return objectRestResponse;
    }
    @RequestMapping("/importDictionaryWords")
    @ResponseBody
    //导入敏感词汇
    public ObjectRestResponse importDictionaryWords(@RequestParam("file") MultipartFile file) {
        String userID = request.getHeader("userId");
        if(userID==null){
            userID="";
        }
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        objectRestResponse.rel(true);
        objectRestResponse.msg("200");
        String resStr="";
        if (Objects.isNull(file) || file.isEmpty()) {
            objectRestResponse.rel(false);
            objectRestResponse.msg("500");
            throw new NullPointerException("Excel文件是空");
        }
        String fileName=file.getOriginalFilename();
        if(fileName.endsWith("xls")==false && fileName.endsWith("xlsx")==false ){
            objectRestResponse.rel(false);
            objectRestResponse.msg("500");
            objectRestResponse.data("请上传excel文件");
            return objectRestResponse;
        }
        try {
            resStr=this.zzDictionaryWordsService.importDictionaryWords(file,userID);
            objectRestResponse.data(resStr);
        } catch (Exception e) {
            e.printStackTrace();
            objectRestResponse.rel(false);
            objectRestResponse.msg("500");
            objectRestResponse.data("导入失败："+ common.getExceptionMessage(e));
        }
        return objectRestResponse;
    }
}