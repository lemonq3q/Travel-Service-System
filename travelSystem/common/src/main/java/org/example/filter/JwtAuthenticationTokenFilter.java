package org.example.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.authenticate.LoginUser;
import org.example.domain.user.User;
import org.example.utils.JwtUtil;
import org.example.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Value("${system.authenticate.token}")
    private String SYSTEM_AUTHENTICATE_TOKEN;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String systemToken = request.getHeader("system-token");
        if( systemToken != null && systemToken.equals(SYSTEM_AUTHENTICATE_TOKEN)){
            // 创建系统用户
            LoginUser loginUser = new LoginUser();
            User user = new User();
            user.setId(0L);
            user.setUsername("system");
            user.setName("system");

            loginUser.setUser(user);
            loginUser.setPermissions(List.of("all"));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("token");
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String userid;
        String jti;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
            jti = claims.getId();
        } catch (Exception e) {
//            e.printStackTrace();
            throw new RuntimeException("token illegal");
        }

        String redisKey = "login:" + userid + ":" + jti;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if (loginUser == null) {
            throw new RuntimeException("user not login");
        }
        // 刷新过期时间
        redisCache.setCacheObject("login:"+userid+":"+jti, loginUser, 24 , TimeUnit.HOURS);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
