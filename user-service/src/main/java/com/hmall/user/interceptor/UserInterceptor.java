package com.hmall.user.interceptor;


import com.hmall.user.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @description: 拦截器
* @ClassName UserInterceptor
* @author Zle
* @date 2022-06-20 16:24
* @version 1.0
*/
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头
        String authorization = request.getHeader("authorization");
        if (StringUtils.isBlank(authorization)){
            log.warn("非法用户访问!请求路径：{}",request.getRequestURI());
            // 没有用户信息 未登录
//            throw new RuntimeException("用户未登录");
            response.setStatus(403);
            return false;
        }
        //2. 转换用户id
        Long userId = Long.valueOf(authorization);
        //2.存入ThreadLocal
        UserHolder.setUser(userId);
        //3.放行
        return true;
    }

/*    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }*/

    //用完不要忘了清理
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
