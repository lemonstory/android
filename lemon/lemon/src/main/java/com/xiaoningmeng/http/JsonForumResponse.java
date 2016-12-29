package com.xiaoningmeng.http;

/**
 * Created by gaoyong on 2016/12/27.
 */

public class JsonForumResponse<T> {


    /**
     * Version : 4
     * Charset : UTF-8
     * Variables : {"cookiepre":"28aX_2132_","auth":null,"saltkey":"qZD5xUkd","member_uid":"10111","member_username":"mT","member_avatar":"http://uc.xiaoningmeng.net/avatar.php?uid=10111&size=small","groupid":"7","formhash":"d495462d","ismoderator":null,"readaccess":"1","notice":{"newpush":"0","newpm":"0","newprompt":"2","newmypost":"0"},"loginUrl":"http://wsq.discuz.qq.com/?c=site&a=waplogin&siteuid=10111&type=json&tt=1482826678&signature=e9f9bbdeb55f134476f5b0bb47000fd97d2fdd0b"}
     * Message : {"messageval":"login_succeed","messagestr":"欢迎您回来，游客 mT，现在将转入登录前页面"}
     */

    private String Version;
    private String Charset;
    private T Variables;
    private MessageBean Message;

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }

    public String getCharset() {
        return Charset;
    }

    public void setCharset(String Charset) {
        this.Charset = Charset;
    }

    public T getVariables() {
        return Variables;
    }

    public void setVariables(T Variables) {
        this.Variables = Variables;
    }

    public MessageBean getMessage() {
        return Message;
    }

    public void setMessage(MessageBean Message) {
        this.Message = Message;
    }

    public static class MessageBean {
        /**
         * messageval : login_succeed
         * messagestr : 欢迎您回来，游客 mT，现在将转入登录前页面
         */

        private String messageval;
        private String messagestr;

        public String getMessageval() {
            return messageval;
        }

        public void setMessageval(String messageval) {
            this.messageval = messageval;
        }

        public String getMessagestr() {
            return messagestr;
        }

        public void setMessagestr(String messagestr) {
            this.messagestr = messagestr;
        }
    }
}
