package com.xiaoningmeng.manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.xiaoningmeng.PlayActivity;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.R;
import com.xiaoningmeng.view.dialog.TipDialog;

import java.lang.ref.SoftReference;
import java.util.HashMap;

//舞动动画管理
public class PlayWaveManager implements PlayObserver {

    private AnimationDrawable mUniformRotateAnimDrawable;
    private HashMap<ImageView, SoftReference<AnimationDrawable>> map;
    private static PlayWaveManager mInstance;
    public Activity mContext;
    private ImageView mPerImageView;
    private PlayerManager mPlayerManager;
    private OnClickListener mWaveClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mPlayerManager.getPlayList().size() > 0 && null != mContext) {
                Intent i = new Intent(mContext, PlayActivity.class);
                mContext.startActivity(i);
                mContext.overridePendingTransition(R.anim.main_translatey100to0,
                        R.anim.main_translatey0tof100);
            } else {
                new TipDialog.Builder(mContext).setAutoDismiss(true)
                        .setTransparent(false).setTipText("嗯哈，你还没有开始播放故事呢")
                        .create().show();
            }
        }
    };

    public static PlayWaveManager getInstance() {
        if (mInstance == null) {
            synchronized (PlayWaveManager.class) {
                if (mInstance == null) {
                    mInstance = new PlayWaveManager();
                }
            }
        }
        return mInstance;
    }

    private PlayWaveManager() {
        map = new HashMap<>();
        mPlayerManager = PlayerManager.getInstance();
    }

    public void loadWaveAnim(Activity context, final ImageView imageView) {
        mContext = context;
        if (imageView != null) {
            imageView.setOnClickListener(mWaveClickListener);
            imageView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (mPerImageView != null) {
                        SoftReference<AnimationDrawable> reference = map.get(mPerImageView);
                        if (reference != null) {
                            AnimationDrawable drawable = reference.get();
                            if (drawable != null) {
                                drawable.stop();
                            }
                        }
                    }
                    SoftReference<AnimationDrawable> reference = map.get(imageView);
                    AnimationDrawable drawable = null;
                    if ((reference == null || reference.get() == null) && mContext != null) {
                        drawable = (AnimationDrawable) ContextCompat.getDrawable(mContext,R.drawable.play_anim_list);
                        reference = new SoftReference<AnimationDrawable>(drawable);
                        imageView.setImageDrawable(drawable);
                    }

                    if (reference != null && reference.get() != null) {
                        drawable = reference.get();
                    }
                    mPerImageView = imageView;
                    mUniformRotateAnimDrawable = drawable;
                    PlayWaveManager.this.notify(PlayerManager.getInstance().getPlayingStory());
                }
            }, 100);
        }
    }

    @Override
    public void notify(PlayingStory music) {
        if (null != mUniformRotateAnimDrawable && null != music) {

            switch (music.playState) {
                case START:
                case PLAY:
                    mUniformRotateAnimDrawable.start();
                    break;
                case PAUSE:
                    mUniformRotateAnimDrawable.stop();
                    break;
                case RESUME:
                    mUniformRotateAnimDrawable.start();
                    break;
                case STOP:
                    mUniformRotateAnimDrawable.stop();
                    break;
                default:
                    break;
            }
        }
    }
}
