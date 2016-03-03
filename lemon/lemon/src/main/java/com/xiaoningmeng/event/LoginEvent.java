package com.xiaoningmeng.event;

import com.xiaoningmeng.bean.UserInfo;

public class LoginEvent {

    //小柠檬登录
    public UserInfo userInfo;
    public LoginEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
