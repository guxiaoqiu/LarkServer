package com.github.hollykunge.security.auth.client.interceptor;

import com.github.hollykunge.security.auth.client.annotation.IgnoreClientToken;
import com.github.hollykunge.security.auth.client.config.ServiceAuthConfig;
import com.github.hollykunge.security.auth.client.jwt.ServiceAuthUtil;
import com.github.hollykunge.security.auth.common.util.jwt.IJWTInfo;
import com.github.hollykunge.security.common.exception.auth.ClientForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 服务鉴权拦截器
 * Created by 协同设计小组 on 2017/9/12.
 */
@SuppressWarnings("ALL")
public class ServiceAuthRestInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(ServiceAuthRestInterceptor.class);

    @Autowired
    private ServiceAuthUtil serviceAuthUtil;

    @Autowired
    private ServiceAuthConfig serviceAuthConfig;

    private List<String> allowedClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 配置该注解，说明不进行服务拦截
        IgnoreClientToken annotation = handlerMethod.getBeanType().getAnnotation(IgnoreClientToken.class);
        if (annotation == null) {
            annotation = handlerMethod.getMethodAnnotation(IgnoreClientToken.class);
        }
        if(annotation!=null) {
            return super.preHandle(request, response, handler);
        }

        String token = request.getHeader(serviceAuthConfig.getTokenHeader());
        //解析获取当前发起请求的服务
        IJWTInfo infoFromToken = serviceAuthUtil.getInfoFromToken(token);
        String uniqueName = infoFromToken.getUniqueName();
        //判断当前被调用服务的可被访问列表是否包含发起请求的服务
        for(String client:serviceAuthUtil.getAllowedClient()){
            if(client.equals(uniqueName)){
                return super.preHandle(request, response, handler);
            }
        }
        throw new ClientForbiddenException("Client is Forbidden!");
    }
}
