package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xiaoningmeng.bean.UserInfo;

import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.ForumLoginVar;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.ForumLoginEvent;
import com.xiaoningmeng.event.LoginEvent;
import com.xiaoningmeng.http.ConstantURL;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.DebugUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import de.greenrobot.event.EventBus;


public class LoginActivity extends BaseActivity implements OnClickListener {

    private UMShareAPI mShareAPI = null;
    private Context mContext;
    private String accessToken;
    private String openId;
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_login);
        mShareAPI = UMShareAPI.get(this);
    }

    /** auth callback interface**/
    //未处理微信的登录
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            switch (action) {

                //授权回调
                case 0:
                    if (data != null) {
                        accessToken = data.get("access_token");
                        openId = data.get("openid");
                    }
                    mShareAPI.getPlatformInfo(LoginActivity.this, platform, umAuthListener);
                    break;

                //获取用户信息回调
                case 2:
                    if (data!=null){

                        nickName = data.get("screen_name");
                        if (!accessToken.equals("") && !openId.equals("") && !nickName.equals("")) {
                            loginQQ(accessToken, openId, nickName);
                        }else {
                            Toast.makeText(getApplicationContext(), "param empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(LoginActivity.this, "无法授权", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            stopLoading();
            Toast.makeText(mContext, "授权取消", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void requestData() {

        LHttpRequest.getInstance().loginRequest(this,
                new JsonCallback<UserInfo>( this) {

                    @Override
                    public void onGetDataSuccess(UserInfo data) {
                        UserAuth.getInstance().login(mContext, data);
                        startActivityForNew(new Intent(LoginActivity.this, HomeActivity.class));
                        oldFinish();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        SHARE_MEDIA platform = null;
        switch (v.getId()) {
            case R.id.tv_login_agreement:
                WebViewActivity.openWebView(this, ConstantURL.Service);
                break;
            case R.id.tv_login_visitor:
                startActivityForNew(new Intent(this, HomeActivity.class));
                finish();
                break;
            case R.id.tv_login_weixin:
                setLoadingTip("正在登录");
                platform = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
                break;
            case R.id.tv_login_qq:
                setLoadingTip("正在登录");
                platform = SHARE_MEDIA.QQ;
                startLoading();
                mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
                break;
            default:
                break;
        }

    }

    private void loginQQ(final String accessToken, final String openId,
                         final String nickName) {

        LHttpRequest.getInstance().QQLoginRequest(this, accessToken, openId,
                new JsonCallback<UserInfo>() {

                    @Override
                    public void onGetDataSuccess(UserInfo data) {

                        UserAuth.getInstance().login(mContext, data);
                    }

                    @Override
                    public void onFailure(int statusCode,String responseString) {
                        if (statusCode == 100302) {
                            loginRegQQ(accessToken, openId, nickName);
                        } else {

                        }
                    }
                });
    }

    private void loginRegQQ(String accessToken, String openId, String nickName) {

        DebugUtils.e("QQ accessToken:" + accessToken + " openId" + openId
                + " nickName" + nickName);

        LHttpRequest.getInstance().QQLoginRegRequest(this, accessToken, openId,
                nickName, new JsonCallback<UserInfo>( this) {

                    @Override
                    public void onGetDataSuccess(UserInfo data) {

                        UserAuth.getInstance().login(mContext, data);
                    }
                });
    }

//    private void loginByWeixin() {
//        startLoading();
//        mController.doOauthVerify(mContext, SHARE_MEDIA.WEIXIN,
//                new UMAuthListener() {
//                    @Override
//                    public void onStart(SHARE_MEDIA platform) {
//                    }
//
//                    @Override
//                    public void onError(SocializeException e,
//                                        SHARE_MEDIA platform) {
//                        Toast.makeText(mContext, "授权错误", Toast.LENGTH_SHORT)
//                                .show();
//
//                    }
//
//                    @Override
//                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
//                        // 获取相关授权信息
//                        mController.getPlatformInfo(LoginActivity.this,
//                                SHARE_MEDIA.WEIXIN, new UMDataListener() {
//                                    @Override
//                                    public void onStart() {
//                                    }
//
//                                    @Override
//                                    public void onComplete(int status,
//                                                           Map<String, Object> info) {
//                                        if (status == 200 && info != null) {
//                                            StringBuilder sb = new StringBuilder();
//                                            Set<String> keys = info.keySet();
//                                            for (String key : keys) {
//                                                sb.append(key
//                                                        + "="
//                                                        + info.get(key)
//                                                        .toString()
//                                                        + "\r\n");
//                                            }
//                                            Log.d("TestData", sb.toString());
//                                        } else {
//                                            Log.d("TestData", "发生错误：" + status);
//                                        }
//                                    }
//                                });
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA platform) {
//                        stopLoading();
//                        Toast.makeText(mContext, "授权取消", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

//    private void loginWechat(final String accessToken, final String openId,
//                             final String nickName) {
//        LHttpRequest.getInstance().WechatLoginRequest(this, accessToken, openId,
//                new LHttpHandler<UserInfo>(this, this) {
//
//                    @Override
//                    public void onGetDataSuccess(UserInfo data) {
//                        UserAuth.getInstance().login(mContext, data);
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers,
//                                          String responseString, Throwable throwable) {
//                        if (statusCode == 100302) {
//                            loginRegWechat(accessToken, openId, nickName);
//                        } else {
//                            super.onFailure(statusCode, headers,
//                                    responseString, throwable);
//                        }
//                    }
//                });
//    }
//
//    private void loginRegWechat(String accessToken, String openId, String nickName) {
//        DebugUtils.e("QQ accessToken:" + accessToken + " openId" + openId
//                + " nickName" + nickName);
//        LHttpRequest.getInstance().WechatLoginRegRequest(this, accessToken, openId,
//                nickName, new LHttpHandler<UserInfo>(this, this) {
//
//                    @Override
//                    public void onGetDataSuccess(UserInfo data) {
//                        UserAuth.getInstance().login(mContext, data);
//                    }
//                });
//    }

    private void enterHomeActivity() {

        if (ActivityManager.getScreenManager().getActivity(HomeActivity.class) == null) {
            startActivityForNew(new Intent(LoginActivity.this,
                    HomeActivity.class));
        }
        finish();
    }

    //向uc同步用户登录信息
    public void onEventMainThread(LoginEvent event) {

        UserInfo userinfo = event.userInfo;
        String uc_callback = userinfo.getUcCallback();
        if (uc_callback != null && !uc_callback.equals("")) {
            LHttpRequest.getInstance().UCSyncLoginRequest(LoginActivity.this, uc_callback, new JsonCallback<String>(LoginActivity.this) {
                @Override
                public void onGetDataSuccess(String data) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONObject messageObject = new JSONObject(jsonObject.getString("Message"));
                        if (messageObject.has("messageval")) {

                            String messageval = messageObject.getString("messageval");
                            if (messageval != null && messageval.equals(Constant.FORUM_LOGIN_SUCCEED)) {

                                if (jsonObject.has("Variables")) {
                                    Gson gson = new Gson();
                                    ForumLoginVar forumLoginVar = gson.fromJson(jsonObject.getString("Variables"), ForumLoginVar.class);
                                    EventBus.getDefault().post(new ForumLoginEvent(forumLoginVar));
                                }
                            }
                            enterHomeActivity();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(String responseString) {
                    DebugUtils.e("UCSyncLoginRequest onFailure callback is run");
                }
            });
        }
    }

    @Override
    public void finish() {
        stopLoading();
        oldFinish();
        animationForOTop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            setStatusBarHide();
        }
    }
}
