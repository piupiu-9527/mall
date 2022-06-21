package com.hmall.user.utils;
/**
* @description: 操作ThreaLocal的工具类
* @ClassName UserHolder
* @author Zle
* @date 2022-06-20 16:11
* @version 1.0
*/
public class UserHolder {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    /**
    * @description: 暴露set
    * @param: [userId]
    * @return: void
    * @author Zle
    * @date: 2022-06-20 16:19
    */
    public static void setUser(Long userId){
        tl.set(userId);
    }

    /**
    * @description: 暴露get 从当前线程取userID
    * @param: []
    * @return: java.lang.Long
    * @author Zle
    * @date: 2022-06-20 16:20
    */
    public static Long getUser(){
        return tl.get();
    }

    /**
    * @description: 清理 防止内存泄露
    * @param: []
    * @return: void
    * @author Zle
    * @date: 2022-06-20 16:23
    */
    public static void removeUser(){
        tl.remove();
    }
}
