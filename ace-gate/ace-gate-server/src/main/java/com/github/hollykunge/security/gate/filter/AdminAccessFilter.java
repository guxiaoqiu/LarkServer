package com.github.hollykunge.security.gate.filter;

import com.alibaba.fastjson.JSON;
import com.github.hollykunge.security.api.vo.authority.PermissionInfo;
import com.github.hollykunge.security.api.vo.log.LogInfo;
import com.github.hollykunge.security.auth.client.config.ServiceAuthConfig;
import com.github.hollykunge.security.auth.client.config.UserAuthConfig;
import com.github.hollykunge.security.auth.client.jwt.ServiceAuthUtil;
import com.github.hollykunge.security.auth.client.jwt.UserAuthUtil;
import com.github.hollykunge.security.auth.common.util.jwt.IJWTInfo;
import com.github.hollykunge.security.common.context.BaseContextHandler;
import com.github.hollykunge.security.common.msg.auth.TokenErrorResponse;
import com.github.hollykunge.security.common.msg.auth.TokenForbiddenResponse;
import com.github.hollykunge.security.common.util.ClientUtil;
import com.github.hollykunge.security.gate.feign.ILogService;
import com.github.hollykunge.security.gate.feign.IUserService;
import com.github.hollykunge.security.gate.utils.DBLog;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 请求权限验证过滤器
 *
 * @author 协同设计小组
 * @create 2017-06-23 8:25
 */
@Component
@Slf4j
public class AdminAccessFilter extends ZuulFilter {
    @Autowired
    @Lazy
    private IUserService userService;
    @Autowired
    @Lazy
    private ILogService logService;

    @Value("${gate.ignore.startWith}")
    private String startWith;

    @Value("${zuul.prefix}")
    private String zuulPrefix;
    @Autowired
    private UserAuthUtil userAuthUtil;

    @Autowired
    private ServiceAuthConfig serviceAuthConfig;

    @Autowired
    private UserAuthConfig userAuthConfig;

    @Autowired
    private ServiceAuthUtil serviceAuthUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        final String requestUri = request.getRequestURI().substring(zuulPrefix.length());
        final String method = request.getMethod();
        BaseContextHandler.setToken(null);
        // 不进行拦截的地址
        if (isStartWith(requestUri)) {
            return null;
        }
        IJWTInfo user = null;
        try {
            user = getJWTUser(request, ctx);
        } catch (Exception e) {
            setFailedRequest(JSON.toJSONString(new TokenErrorResponse(e.getMessage())), 200);
            return null;
        }
        //获取所有的资源信息，包括menu和element
        List<PermissionInfo> permissionIfs = userService.getAllPermissionInfo();
        // 判断资源是否启用权限约束
        Stream<PermissionInfo> stream = getPermissionIfs(requestUri, method, permissionIfs);
        List<PermissionInfo> result = stream.collect(Collectors.toList());
        PermissionInfo[] permissions = result.toArray(new PermissionInfo[]{});

        if (permissions.length > 0) {
            checkUserPermission(permissions, ctx, user);
        }
        // 申请客户端密钥头
        ctx.addZuulRequestHeader(serviceAuthConfig.getTokenHeader(), serviceAuthUtil.getClientToken());
        return null;
    }

    /**
     * 获取目标权限资源
     * 请求资源和权限列表匹配，并且与资源方法相同
     * @param requestUri
     * @param method
     * @param serviceInfo
     * @return
     */
    private Stream<PermissionInfo> getPermissionIfs(final String requestUri, final String method, List<PermissionInfo> serviceInfo) {
        return serviceInfo.parallelStream().filter(new Predicate<PermissionInfo>() {
            @Override
            public boolean test(PermissionInfo permissionInfo) {
                String url = permissionInfo.getUri();
                String uri = url.replaceAll("\\{\\*\\}", "[a-zA-Z\\\\d]+");
                String regEx = "^" + uri + "$";
                return (Pattern.compile(regEx).matcher(requestUri).find() || requestUri.startsWith(url + "/"))
                        && method.equals(permissionInfo.getMethod());
            }
        });
    }

    /**
     * 在上下文中设置当前用户信息和操作日志
     */
    private void setCurrentUserInfoAndLog(RequestContext ctx, IJWTInfo user, PermissionInfo pm) {
        String host = ClientUtil.getClientIp(ctx.getRequest());
        ctx.addZuulRequestHeader("userId", user.getId());
        ctx.addZuulRequestHeader("userName", URLEncoder.encode(user.getName()));
        ctx.addZuulRequestHeader("userHost", ClientUtil.getClientIp(ctx.getRequest()));
        LogInfo logInfo = new LogInfo(pm.getMenu(), pm.getName(), pm.getUri(), new Date(), user.getId(), user.getName(), host);
        DBLog.getInstance().setLogService(logService).offerQueue(logInfo);
    }

    /**
     * 返回token中的用户信息
     *
     * @param request
     * @param ctx
     * @return
     */
    private IJWTInfo getJWTUser(HttpServletRequest request, RequestContext ctx) throws Exception {
        String authToken = request.getHeader(userAuthConfig.getTokenHeader());
        if (StringUtils.isBlank(authToken)) {
            authToken = request.getParameter("token");
        }
        ctx.addZuulRequestHeader(userAuthConfig.getTokenHeader(), authToken);
        BaseContextHandler.setToken(authToken);
        return userAuthUtil.getInfoFromToken(authToken);
    }


    /**
     * 检查用户是否有该权限
     * @param permissions
     * @param ctx
     * @param user
     */
    private void checkUserPermission(PermissionInfo[] permissions, RequestContext ctx, IJWTInfo user) {
        //根据用户id获取资源列表，包括菜单和菜单功能
        List<PermissionInfo> permissionInfos = userService.getPermissionByUsername(user.getUniqueName());
        PermissionInfo current = null;
        for (PermissionInfo info : permissions) {
            boolean anyMatch = permissionInfos.parallelStream().anyMatch(new Predicate<PermissionInfo>() {
                @Override
                public boolean test(PermissionInfo permissionInfo) {
                    return permissionInfo.getCode().equals(info.getCode());
                }
            });
            if (anyMatch) {
                current = info;
                break;
            }
        }
        if (current == null) {
            setFailedRequest(JSON.toJSONString(new TokenForbiddenResponse("Token Forbidden!")), 200);
        } else {
            if (!RequestMethod.GET.toString().equals(current.getMethod())) {
                setCurrentUserInfoAndLog(ctx, user, current);
            }
        }
    }


    /**
     * URI是否以什么打头
     *
     * @param requestUri
     * @return
     */
    private boolean isStartWith(String requestUri) {
        boolean flag = false;
        for (String s : startWith.split(",")) {
            if (requestUri.startsWith(s)) {
                return true;
            }
        }
        return flag;
    }

    /**
     * 网关抛异常
     *
     * @param body
     * @param code
     */
    private void setFailedRequest(String body, int code) {
        log.debug("Reporting error ({}): {}", code, body);
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);
        }
    }

}
