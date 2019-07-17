package com.github.hollykunge.security.auth.util.client;

import com.github.hollykunge.security.auth.common.util.jwt.IJWTInfo;
import com.github.hollykunge.security.auth.common.util.jwt.JWTHelper;
import com.github.hollykunge.security.auth.configuration.KeyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author 协同设计小组
 * @date 2017/9/10
 */
@Configuration
public class ClientTokenUtil {
    private Logger logger = LoggerFactory.getLogger(ClientTokenUtil.class);

    @Value("${client.expire}")
    private int expire;
    @Autowired
    private KeyConfiguration keyConfiguration;

    public String generateToken(IJWTInfo jwtInfo) throws Exception {
        return JWTHelper.generateToken(jwtInfo, keyConfiguration.getServicePriKey(), expire);
    }

    public IJWTInfo getInfoFromToken(String token) throws Exception {
        return JWTHelper.getInfoFromToken(token, keyConfiguration.getServicePubKey());
    }

}
