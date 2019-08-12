package com.github.hollykunge.servicewebservice.config.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.hollykunge.security.common.constant.CommonConstants;
import com.github.hollykunge.security.common.vo.mq.AdminOrgVO;
import com.github.hollykunge.security.common.vo.mq.AdminUserVO;
import com.github.hollykunge.servicewebservice.dao.EryuanOrgDao;
import com.github.hollykunge.servicewebservice.dao.EryuanUserDao;
import com.github.hollykunge.servicewebservice.model.EryuanOrg;
import com.github.hollykunge.servicewebservice.model.EryuanUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @Author: yzq
 * @Date: 创建于 2019/7/23 14:06
 */
@Configuration
@Slf4j
public class RabbitMqConfig {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ExchangeConfig exchangeConfig;

    @Autowired
    private QueueConfig queueConfig;

    @Autowired
    private EryuanOrgDao eryuanOrgDao;

    @Autowired
    private EryuanUserDao eryuanUserDao;

    @Bean
    public Binding orgBinding(){
        Binding binding = BindingBuilder.bind(queueConfig.adminOrgQueue()).to(exchangeConfig.adminUserAndOrgExchange()).with(CommonConstants.ADMINORG_ROTEING_KEY);
        return binding;
    }
    @Bean
    public Binding userBinding(){
        Binding binding = BindingBuilder.bind(queueConfig.adminUserQueue()).to(exchangeConfig.adminUserAndOrgExchange()).with(CommonConstants.ADMINUSER_ROTEING_KEY);
        return binding;
    }
    @Bean
    @Qualifier("orgRabbitTemplate")
    @Transactional(rollbackFor = Exception.class)
    public RabbitTemplate orgRabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (!ack) {
                    if (!StringUtils.isEmpty(correlationData.getId())) {
                        String[] ids = correlationData.getId().split(",");
//                        EryuanOrg eryuanOrg = new EryuanOrg();
                        for (String id : ids) {
//                            Example example = new Example(EryuanOrg.class);
//                            Example.Criteria criteria = example.createCriteria();
//                            criteria.andEqualTo("CASIC_ORG_CODE",id);
//                            eryuanOrg.setIsSuccess("0");
//                            eryuanOrgDao.updateByExampleSelective(eryuanOrg,example);
                            String isSuccess = "0";
                            eryuanOrgDao.updateOrgIsSuccess(isSuccess,id);
                        }
                        log.warn("message>>> "+correlationData.getId()+">>>未被发布到mq服务,已经补偿到了EryuanOrg信息集...");
                    }
                }
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message,int replyCode, String replyText, String exchange, String routingKey) {
                try {
                    String json = new String(message.getBody(),"utf-8");
                    List<AdminOrgVO> orgList = JSONArray.parseArray(json,AdminOrgVO.class);
//                    EryuanOrg eryuanOrg = new EryuanOrg();
                    for (AdminOrgVO adminOrg:orgList) {
//                        Example example = new Example(EryuanOrg.class);
//                        Example.Criteria criteria = example.createCriteria();
//                        criteria.andEqualTo("CASIC_ORG_CODE",adminOrg.getId());
//                        eryuanOrg.setIsSuccess("0");
//                        eryuanOrgDao.updateByExampleSelective(eryuanOrg,example);
                        String isSuccess = "0";
                        eryuanOrgDao.updateOrgIsSuccess(isSuccess,adminOrg.getId());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        return rabbitTemplate;
    }
    @Bean
    @Qualifier("userRabbitTemplate")
    @Transactional(rollbackFor = Exception.class)
    public RabbitTemplate userRabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (!ack) {
                    if (!StringUtils.isEmpty(correlationData.getId())) {
                        String[] ids = correlationData.getId().split(",");
                        EryuanUser eryuanUser = new EryuanUser();
                        for (String id : ids) {
                            eryuanUser.setIsSuccess("0");
                            eryuanUser.setID(id);
                            eryuanUserDao.updateByPrimaryKeySelective(eryuanUser);
                        }
                        log.warn("message>>> "+correlationData.getId()+">>>未被发布到mq服务,已经补偿到了EryuanUser信息集...");
                    }
                }
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                try {
                    String json = new String(message.getBody(),"utf-8");
                    List<AdminUserVO> userList = JSONArray.parseArray(json,AdminUserVO.class);
                    EryuanUser eryuanUser = new EryuanUser();
                    for (AdminUserVO adminUser:userList) {
                        eryuanUser.setIsSuccess("0");
                        eryuanUser.setID(adminUser.getId());
                        eryuanUserDao.updateByPrimaryKeySelective(eryuanUser);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        return rabbitTemplate;
    }
}
