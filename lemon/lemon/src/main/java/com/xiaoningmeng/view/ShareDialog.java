package com.xiaoningmeng.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.view.dialog.BaseDialog;

/**
 * Created by feishang on 2015/12/24.
 */
public class ShareDialog implements View.OnClickListener{

    private UMSocialService mController;
    private ShareBean mShareBean;
    private Activity mContext;
    private BaseDialog mDialog;
    private Handler mHandler = new Handler();

    public UMSocialService show(Activity activity,ShareBean shareBean){
        mShareBean = shareBean;
        mContext = activity;
        mShareBean.setTitle(mShareBean.getTitle()+" - (小柠檬 - 儿童智能故事机)");
        setShareParams();
        mDialog = new BaseDialog.Builder(mContext).setGravity(Gravity.BOTTOM).create();
        View shareView  = View.inflate(mContext,R.layout.activity_share,null);
        shareView.findViewById(R.id.share_rl_qq).setOnClickListener(this);
        shareView.findViewById(R.id.share_rl_circle).setOnClickListener(this);
        shareView.findViewById(R.id.share_rl_wechat).setOnClickListener(this);
        shareView.findViewById(R.id.share_rl_qzone).setOnClickListener(this);
        shareView.findViewById(R.id.share_rl_weibo).setOnClickListener(this);
        shareView.findViewById(R.id.share_ll_copy).setOnClickListener(this);
        shareView.findViewById(R.id.share_tv_cancel).setOnClickListener(this);
        mDialog.show(shareView);
        return mController;
    }
    private void setShareParams() {
        // 首先在您的Activity中添加如下成员变量
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        // 设置分享内容
        mController.setShareContent("来自小柠檬客户端");
        mController.getConfig().setSinaCallbackUrl("http://sns.whalecloud.com/sina2/callback");
        // 设置分享图片, 参数2为图片的url地址
        if(mShareBean.getIconUrl() == null) {
            BitmapDrawable bd = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.logo);
            Bitmap mbitmap = bd.getBitmap();
            mController.setShareMedia(new UMImage(mContext, mbitmap));
        }else{
            mController.setShareMedia(new UMImage(mContext,mShareBean.getIconUrl()));
        }
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mContext, Constant.WEI_XIN_APP_ID,
                Constant.WEIN_XIN_APP_SECRET);
        wxHandler.setTitle(mShareBean.getTitle());
        wxHandler.setTargetUrl(mShareBean.getUrl());
        wxHandler.addToSocialSDK();
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mContext,
                Constant.WEI_XIN_APP_ID, Constant.WEIN_XIN_APP_SECRET);
        wxCircleHandler.setTitle(mShareBean.getTitle());
        wxCircleHandler.setTargetUrl(mShareBean.getUrl());
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext,
                Constant.QQ_APP_ID, Constant.QQ_APP_KEY);
        qqSsoHandler.addToSocialSDK();
        // 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext,Constant.QQ_APP_ID, Constant.QQ_APP_KEY);
        qqSsoHandler.setTitle(mShareBean.getTitle());
        qqSsoHandler.setTargetUrl(mShareBean.getUrl());
        qZoneSsoHandler.setTargetUrl(mShareBean.getUrl());
        qZoneSsoHandler.addToSocialSDK();
        SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
        sinaSsoHandler.setTargetUrl(mShareBean.getUrl());
        mController.getConfig().setSsoHandler(sinaSsoHandler);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_rl_qq:
                share(SHARE_MEDIA.QQ);
                MobclickAgent.onEvent(mContext, "Share:QQ");
                break;
            case R.id.share_rl_circle:
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                MobclickAgent.onEvent(mContext, "Share:WEIXIN_CIRCLE");

                break;
            case R.id.share_rl_wechat:
                share(SHARE_MEDIA.WEIXIN);
                MobclickAgent.onEvent(mContext, "Share:WEIXIN");
                break;
            case R.id.share_rl_qzone:
                share(SHARE_MEDIA.QZONE);
                MobclickAgent.onEvent(mContext, "Share:QZONE");
                break;
            case R.id.share_rl_weibo:
                share(SHARE_MEDIA.SINA);
                MobclickAgent.onEvent(mContext, "Share:SINA");
                break;
            case R.id.share_ll_copy:
                copyUrl(mShareBean.getUrl());
                mDialog.dismiss();
                break;
            case R.id.share_tv_cancel:
                break;
            default:
                break;
        }
        mDialog.dismiss();
    }
    private void share(final SHARE_MEDIA platform) {

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mController.postShare(mContext, platform,
                        new SocializeListeners.SnsPostListener() {
                            @Override
                            public void onStart() {
                                Toast.makeText(mContext, "开始分享.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete(SHARE_MEDIA platform,
                                                   int eCode, SocializeEntity entity) {
                                if (eCode == 200) {
                                    Toast.makeText(mContext, "分享成功.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    String eMsg = "";
                                    if (eCode == -101) {
                                        eMsg = "没有授权";
                                    }
                                    Toast.makeText(mContext,
                                            "分享失败[" + eCode + "] " + eMsg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        }, 100);
    }


    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void copyUrl(String url) {
        if(android.os.Build.VERSION.SDK_INT > 11){
            // 得到剪贴板管理器
            android.content.ClipboardManager cmb = (android.content.ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(url);
        }else{
            android.text.ClipboardManager cmb = (android.text.ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(url);
        }
        Toast.makeText(mContext, "复制链接成功", Toast.LENGTH_SHORT).show();
    }
}
