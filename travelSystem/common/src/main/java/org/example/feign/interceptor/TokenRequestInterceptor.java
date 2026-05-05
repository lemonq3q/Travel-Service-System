package org.example.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.example.utils.SystemCommonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenRequestInterceptor implements RequestInterceptor {

    @Value("${system.authenticate.token}")
    private String SYSTEM_AUTHENTICATE_TOKEN;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = SystemCommonUtil.getNowUserToken();
        if(token == null){
            requestTemplate.header("system-token", SYSTEM_AUTHENTICATE_TOKEN);
        }
        else {
            requestTemplate.header("token", token);
        }
    }
}
