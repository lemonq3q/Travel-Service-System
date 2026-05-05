package org.example.utils;

import org.example.domain.authenticate.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SystemCommonUtil {

    private static final String ACCESS_KEY_ID = "";

    private static final String ACCESS_KEY_SECRET = "";

    public static String getAccessKeyId(){
        return ACCESS_KEY_ID;
    }

    public static String getAccessKeySecret(){
        return ACCESS_KEY_SECRET;
    }


    /**
     * 未确定算法，暂时用时间戳代替
     * @return 不重复的code
     */
    public static String buildCode(){
        return UUID.randomUUID().toString()
                .replace("-", "")
                .toUpperCase()
                .substring(0, 10);
    }

    /**
     * 获取进行操作的用户id
     * @return 当前上下文认证的用户id
     */
    public static Long getNowUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            LoginUser nowUser = (LoginUser) authentication.getPrincipal();
            return nowUser.getUser().getId();
        }
        else{
            return 0L;
        }
    }

    public static String getNowUserToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof LoginUser) {
            LoginUser nowUser = (LoginUser) principal;
            return nowUser.getToken();
        }

        return null;
    }

    public static List<String> getNowUserPerms(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            LoginUser nowUser = (LoginUser) authentication.getPrincipal();
            return nowUser.getPermissions();
        }
        else{
            return new ArrayList<>();
        }
    }

    public static boolean hasPerm(String perm){
        List<String> perms = getNowUserPerms();
        return perms.contains(perm);
    }

}
