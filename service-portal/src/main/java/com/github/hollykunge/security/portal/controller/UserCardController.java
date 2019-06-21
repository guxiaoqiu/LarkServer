package com.github.hollykunge.security.portal.controller;

import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.msg.ListRestResponse;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.rest.BaseController;
import com.github.hollykunge.security.entity.UserCard;
import com.github.hollykunge.security.feign.IUserService;
import com.github.hollykunge.security.portal.service.UserCardService;
import com.github.hollykunge.security.vo.UserCardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户卡片接口
 * @author zhhongyu
 * @since 2019-06-11
 */
@RestController
@RequestMapping("userCard")
public class UserCardController extends BaseController<UserCardService, UserCard> {
    @Autowired
    private IUserService userService;
    /**
     * 给用户设置卡片接口
     * @param userCard 卡片实体类
     * @return
     */
    @Override
    @RequestMapping(value = "/myself", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<UserCard> add(@RequestBody UserCard userCard) {
        String userID = request.getHeader("userId");
        if(StringUtils.isEmpty(userID)){
            throw new BaseException("request contains no user...");
        }
        userCard.setUserId(userID);
       if(baseBiz.selectCount(userCard) > 0){
           return new ObjectRestResponse<>().rel(false).msg("设置卡片失败，已经添加过了...");
       }
        return super.add(userCard).rel(true).msg("设置成功...");
    }

    /**
     * 用户删除显示卡片接口
     * @param cardId 卡片id
     * @return
     */
    @RequestMapping(value = "/myself", method = RequestMethod.DELETE)
    @ResponseBody
    public ObjectRestResponse<Boolean> removeUserCard(@RequestParam String cardId) {
        String userID =  request.getHeader("userId");
        if(StringUtils.isEmpty(userID)){
            throw new BaseException("request contains no user...");
        }
        UserCard userCard = new UserCard();
        userCard.setUserId(userID);
        userCard.setCardId(cardId);
        if(baseBiz.selectCount(userCard) == 0){
            return new ObjectRestResponse<>().msg("用户不显示该卡片,不需要移除...");
        }
        baseBiz.delete(userCard);
        return new ObjectRestResponse<>().msg("移除成功..").rel(true);
    }

    /**
     * 获取用户要展示的卡片
     * @return
     */
    @RequestMapping(value = "/myself", method = RequestMethod.GET)
    @ResponseBody
    public ListRestResponse<List<UserCardVO>> userCards(HttpServletRequest request) {
        String userID =  request.getHeader("userId");
        if(StringUtils.isEmpty(userID)){
            throw new BaseException("request contains no user...");
        }
        List<UserCardVO> userCardVOS = baseBiz.userCards(userID);
        return new ListRestResponse("",userCardVOS.size(),userCardVOS);
    }
    /**
     * 获取卡片集，如果用户点击展示该卡片，
     * 该实体类defaultChecked字段为true
     * @return
     */
    @RequestMapping(value = "/cards", method = RequestMethod.GET)
    @ResponseBody
    public ListRestResponse<List<UserCardVO>> allCard() {
        String userID =  request.getHeader("userId");
        if(StringUtils.isEmpty(userID)){
            throw new BaseException("request contains no user...");
        }
        List<UserCardVO> list = baseBiz.allCard(userID);
        return new ListRestResponse("",list.size(),list);
    }
}
