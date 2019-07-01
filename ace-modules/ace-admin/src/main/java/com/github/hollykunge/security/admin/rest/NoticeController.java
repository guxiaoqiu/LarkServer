package com.github.hollykunge.security.admin.rest;

import com.github.hollykunge.security.admin.biz.NoticeBiz;

import com.github.hollykunge.security.admin.entity.Notice;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.msg.TableResultResponse;
import com.github.hollykunge.security.common.rest.BaseController;
import com.github.hollykunge.security.common.util.Query;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: yzq
 * @Date: 创建于 2019/6/4 16:37
 */

@Controller
@RequestMapping("notice")
@Api("公告模块")
public class NoticeController extends BaseController<NoticeBiz,Notice> {
//    @Override
//    @RequestMapping(value = "/page", method = RequestMethod.GET)
//    @ResponseBody
//    public TableResultResponse<Notice> page(@RequestParam Map<String, Object> params){
//        String userId =(String) params.get("userId");
//        String orgId = baseBiz.getOrgIdByUserId(userId);
//        params.remove("userId");
//        params.put("orgId", orgId);
//        return super.page(params);
//    }

//    @Override
//    @RequestMapping(value = "", method = RequestMethod.POST)
//    @ResponseBody
//    public ObjectRestResponse<Notice> add(@RequestBody Notice notice) {
//        baseBiz.insertSelective(notice);
//        String orgId = baseBiz.getOrgIdByUserId(notice.getCrtUser());
//        notice.setOrgId(orgId);
//        return new ObjectRestResponse<Notice>();
//
//    }

    //    @RequestMapping(value = "/no-page", method = RequestMethod.GET)
//    @ResponseBody
//    public ListRestResponse<Notice> noPage(@RequestParam("userId") String userId){
//        List<Notice> listNoticeTopSix = baseBiz.listNoticeTopSix(userId);
//
//        return new ListRestResponse("",listNoticeTopSix.size(),listNoticeTopSix);
//    }

    /**
     * 消息发布接口
     * @param notice 消息实体类必须带id
     * @return
     */
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<Boolean> sendNotice(@RequestBody Notice notice){
        baseBiz.sentNotice(notice);
        return new ObjectRestResponse().data(true);
    }
    @Override
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Notice> page(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        String userID = request.getHeader("userId");
        if(StringUtils.isEmpty(userID)){
            throw new BaseException("request contains no user...");
        }
        return baseBiz.pageList(query,userID);
    }
}
