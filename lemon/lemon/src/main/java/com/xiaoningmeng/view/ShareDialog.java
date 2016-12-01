package com.xiaoningmeng.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMusic;

import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.view.dialog.BaseDialog;

public class ShareDialog implements View.OnClickListener{

    private ShareAction mController;
    private ShareBean mShareBean;
    private Activity mContext;
    private BaseDialog mDialog;

    public ShareAction show(Activity activity,ShareBean shareBean){
        mShareBean = shareBean;
        mContext = activity;
        mShareBean.setTitle(mShareBean.getTitle());
        mDialog = new BaseDialog.Builder(mContext).setGravity(Gravity.BOTTOM).create();
        View shareView  = View.inflate(mContext, R.layout.activity_share,null);
        shareView.findViewById(R.id.share_rl_qq).setOnClickListener(this);
        shareView.findViewById(R.id.share_rl_circle).setOnClickListener(this);
        shareView.findViewById(R.id.share_rl_wechat).setOnClickListener(this);
        shareView.findViewById(R.id.share_rl_qzone).setOnClickListener(this);
        shareView.findViewById(R.id.share_rl_weibo).setOnClickListener(this);
        shareView.findViewById(R.id.share_ll_copy).setOnClickListener(this);
        shareView.findViewById(R.id.share_tv_cancel).setOnClickListener(this);
        mDialog.show(shareView);
        mController = new ShareAction(mContext);
        return mController;


    }
    private void setShareParams() {

        // 设置分享内容
        mController.withText(mShareBean.getText());
        mController.withTitle(mShareBean.getTitle());
        // 设置分享图片, 参数2为图片的url地址
        if(mShareBean.getIconUrl() == null) {
            UMImage image = new UMImage(mContext,BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_app_logo));
            mController.withMedia(image);
        } else{
            UMImage image = new UMImage(mContext, mShareBean.getIconUrl());
            image.setTargetUrl(mShareBean.getUrl());
            mController.withMedia(image);
        }
        //测试新浪微博不能分享音频
        if (mController.getPlatform() != null && !mController.getPlatform().toString().equals("SINA")) {
            if (mShareBean.getMusicUrl() != null) {
                UMusic music = new UMusic(mShareBean.getMusicUrl());
                music.setTitle(mShareBean.getTitle());
                music.setThumb(new UMImage(mContext,mShareBean.getIconUrl()));
                music.setTargetUrl(mShareBean.getUrl());
                mController.withMedia(music);
            }
        }
        mController.withTargetUrl(mShareBean.getUrl());
        mController.setCallback(umShareListener);
    }

    public UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(mContext,platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(mContext,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(mContext,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onClick(View v) {
        //开启回流统计
        Config.isloadUrl = true;
        //关闭Toast
        Config.IsToastTip = false;
        //修改dialog
        ProgressDialog dialog =  new ProgressDialog(mContext);
        dialog.setMessage("正在打开...");
        Config.dialog = dialog;

        switch (v.getId()) {
            case R.id.share_rl_qq:
                share(SHARE_MEDIA.QQ);
                MobclickAgent.onEvent(mContext, "Share_QQ");
                break;
            case R.id.share_rl_circle:
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                MobclickAgent.onEvent(mContext, "Share_WEIXIN_CIRCLE");
                break;
            case R.id.share_rl_wechat:
                share(SHARE_MEDIA.WEIXIN);
                MobclickAgent.onEvent(mContext, "Share_WEIXIN");
                break;
            case R.id.share_rl_qzone:
                share(SHARE_MEDIA.QZONE);
                MobclickAgent.onEvent(mContext, "Share_QZONE");
                break;
            case R.id.share_rl_weibo:
                share(SHARE_MEDIA.SINA);
                MobclickAgent.onEvent(mContext, "Share_SINA");
                break;
            case R.id.share_ll_copy:
                copyUrl(mShareBean.getUrl());
                MobclickAgent.onEvent(mContext, "Share_COPY_URL");
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

        mController.setPlatform(platform);
        setShareParams();
        mController.share();
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
        Toast.makeText(mContext, "链接已复制", Toast.LENGTH_SHORT).show();
    }
}
