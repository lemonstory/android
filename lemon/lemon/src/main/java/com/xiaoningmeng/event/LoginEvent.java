package com.xiaoningmeng.event;

import com.xiaoningmeng.bean.UserInfo;

public class LoginEvent {

    public UserInfo userInfo;
    public LoginEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
