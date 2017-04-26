package com.xiaoningmeng;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.http.ConstantURL;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.Map;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.constant.Constant.REQ_REG_QQ_STATUS;

public class LoginActivity extends BaseActivity implements OnClickListener {

    private UMShareAPI mShareAPI = null;
    private Context mContext;
    private String accessToken;
    private String openId;
    private String nickName;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_login);
        mShareAPI = UMShareAPI.get(this);
        mDialog = new ProgressDialog(mContext);
    }

    /**
     * auth callback interface
     **/
    //未处理微信的登录
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            switch (action) {

                //授权回调
                case 0:
                    if (data != null) {
                        DebugUtils.d("data = " + data.toString());
                        accessToken = data.get("access_token");
                        openId = data.get("openid");
                    }
                    mShareAPI.getPlatformInfo(LoginActivity.this, platform, umAuthListener);
                    break;

                //获取用户信息回调
                case 2:
                    if (data != null) {
                        mDialog.setMessage("正在登录中...");
                        mDialog.show();
                        nickName = data.get("screen_name");
                        if (!accessToken.equals("") && !openId.equals("") && !nickName.equals("")) {
                            loginQQ(accessToken, openId, nickName);
                        } else {
                            Toast.makeText(getApplicationContext(), "param empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

            mDialog.dismiss();
            DebugUtils.e(t.toString());
            Toast.makeText(mContext, "无法授权", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            mDialog.dismiss();
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
        mDialog.dismiss();
        EventBus.getDefault().unregister(this);
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
                mDialog.setMessage("准备跳转至微信登录...");
                mDialog.show();
                platform = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
                break;
            case R.id.tv_login_qq:
                mDialog.setMessage("准备跳转至QQ登录...");
                mDialog.show();
                platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
                break;
            default:
                break;
        }
    }

    private void loginQQ(final String accessToken, final String openId, final String nickName) {

        LHttpRequest.QQLoginRequest QQLoginRequest = LHttpRequest.mRetrofit.create(LHttpRequest.QQLoginRequest.class);
        Call<JsonResponse<UserInfo>> call = QQLoginRequest.getResult(accessToken, openId);
        call.enqueue(new Callback<JsonResponse<UserInfo>>() {
            @Override
            public void onResponse(Call<JsonResponse<UserInfo>> call, Response<JsonResponse<UserInfo>> response) {

                mDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().isSuccessful()) {
                        UserAuth.getInstance().saveLoginUserInfo(mContext, response.body().getData());
                        startActivityForNew(new Intent(LoginActivity.this, HomeActivity.class));
                        oldFinish();
                    } else if (REQ_REG_QQ_STATUS == response.body().getCode()) {

                        loginRegQQ(accessToken, openId, nickName);
                    } else {

                        Toast.makeText(mContext, response.body().getDesc(), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    String message = response.code() + " : " + response.message();
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<UserInfo>> call, Throwable t) {

                mDialog.dismiss();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TODO:未测试
    private void loginRegQQ(String accessToken, String openId, String nickName) {

        LHttpRequest.QQLoginRegRequest qqLoginRegRequest =
                LHttpRequest.mRetrofit.create(LHttpRequest.QQLoginRegRequest.class);
        Call<JsonResponse<UserInfo>> call = qqLoginRegRequest.getResult(accessToken, openId, nickName);
        call.enqueue(new Callback<JsonResponse<UserInfo>>() {

            @Override
            public void onResponse(Call<JsonResponse<UserInfo>> call,
                                   Response<JsonResponse<UserInfo>> response) {
                mDialog.dismiss();
                UserAuth.getInstance().saveLoginUserInfo(mContext, response.body().getData());
            }

            @Override
            public void onFailure(Call<JsonResponse<UserInfo>> call, Throwable t) {
                mDialog.dismiss();
                DebugUtils.e("loginRegQQ onFailure callback is run : message " + t.toString());
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
            startActivityForNew(new Intent(LoginActivity.this, HomeActivity.class));
        }
        finish();
    }

    @Override
    public void finish() {
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
        if (hasFocus) {
            setStatusBarHide();
        }
    }
}
