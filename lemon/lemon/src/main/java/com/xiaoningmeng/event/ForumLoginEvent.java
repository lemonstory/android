package com.xiaoningmeng.event;

import com.xiaoningmeng.bean.ForumLoginVar;

/**
 * Created by gaoyong on 16/3/3.
 */
public class ForumLoginEvent {

    //社区登录
    public ForumLoginVar forumLoginVar;
    public ForumLoginEvent(ForumLoginVar forumLoginVar) {

        this.forumLoginVar = forumLoginVar;
    }
}
