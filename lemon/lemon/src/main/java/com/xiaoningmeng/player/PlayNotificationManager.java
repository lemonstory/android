package com.xiaoningmeng.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.xiaoningmeng.R;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.player.PlayerManager.PlayState;
import com.xiaoningmeng.reminder.RemindBroadcastReceiver;

public class PlayNotificationManager {

    private NotificationManager mNotificationManager;

    private Context mContext;
    private static PlayNotificationManager mInstance;
    public Notification notification;
    private RemoteViews notifyView;

    private PlayNotificationManager() {
        mContext = MyApplication.getInstance().getApplicationContext();
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static PlayNotificationManager getInstance() {
        if (mInstance == null) {
            synchronized (PlayNotificationManager.class) {
                if (mInstance == null) {
                    mInstance = new PlayNotificationManager();
                }
            }
        }
        return mInstance;
    }

    @SuppressWarnings("deprecation")
    public void show(PlayingStory playingMusic) {

        if (playingMusic.playState == PlayState.STOP) {
            return;
        }
        if (notification == null) {
            String title = (playingMusic.albumInfo != null ? playingMusic.albumInfo.getTitle() : mContext.getString(R.string.app_name));
            notification = new Notification(R.drawable.ic_app_logo, title, System.currentTimeMillis());
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notifyView = new RemoteViews(mContext.getPackageName(),
                    R.layout.notify_play);
            notifyView.setOnClickPendingIntent(R.id.notify_next, PendingIntent
                    .getBroadcast(mContext, 0, new Intent(
                                    RemindBroadcastReceiver.NOTIFICATION_NEXT),
                            PendingIntent.FLAG_UPDATE_CURRENT));

            notifyView.setOnClickPendingIntent(R.id.notify_pause, PendingIntent
                    .getBroadcast(mContext, 0, new Intent(
                                    RemindBroadcastReceiver.NOTIFICATION_PAUSE),
                            PendingIntent.FLAG_UPDATE_CURRENT));

            notifyView.setOnClickPendingIntent(R.id.notify_resume,
                    PendingIntent.getBroadcast(mContext, 0, new Intent(
                                    RemindBroadcastReceiver.NOTIFICATION_RESUME),
                            PendingIntent.FLAG_UPDATE_CURRENT));
            notifyView.setOnClickPendingIntent(R.id.notify_per, PendingIntent
                    .getBroadcast(mContext, 0, new Intent(
                                    RemindBroadcastReceiver.NOTIFICATION_PER),
                            PendingIntent.FLAG_UPDATE_CURRENT));
            PendingIntent contentIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(RemindBroadcastReceiver.NOTIFICATION_PLAY),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notification.contentIntent = contentIntent;
            //notification.setLatestEventInfo(mContext, "点击查看", "点击查看详细内容", contentIntent);
            notifyView.setTextViewText(R.id.notify_title, playingMusic.title);
            notifyView.setTextViewText(R.id.notify_album, title);
        }
        showNotify(playingMusic);

    }

    private void showNotify(PlayingStory playingMusic) {

        if (playingMusic.playState == PlayState.PAUSE) {
            notifyView.setViewVisibility(R.id.notify_resume, View.VISIBLE);
            notifyView.setViewVisibility(R.id.notify_pause, View.GONE);
        } else {
            notifyView.setViewVisibility(R.id.notify_resume, View.GONE);
            notifyView.setViewVisibility(R.id.notify_pause, View.VISIBLE);
        }
        notifyView.setTextViewText(R.id.notify_title, playingMusic.title);
        String title = (playingMusic.albumInfo != null ? playingMusic.albumInfo.getTitle() : mContext.getString(R.string.app_name));
        notifyView.setTextViewText(R.id.notify_album, title);
        String url = playingMusic.cover != null ? playingMusic.cover : (playingMusic.albumInfo != null ? playingMusic.albumInfo.getS_cover() : null);
        if(url != null) {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(url))
                    .setProgressiveRenderingEnabled(true)
                    .build();
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            DataSource<CloseableReference<CloseableImage>>
                    dataSource = imagePipeline.fetchDecodedImage(imageRequest, mContext);

            dataSource.subscribe(new BaseBitmapDataSubscriber() {

                                     @Override
                                     public void onNewResultImpl(Bitmap bitmap) {
                                         if (null != bitmap && !bitmap.isRecycled()) {
                                             notifyView.setImageViewBitmap(R.id.notify_logo, bitmap);
                                             mNotificationManager.notify(MusicService.PLAYING_NOTIFY_ID, notification);
                                         }
                                     }

                                     @Override
                                     public void onFailureImpl(DataSource dataSource) {
                                         notifyView.setImageViewResource(R.id.notify_logo, R.drawable.ic_notice_clock);
                                         mNotificationManager.notify(MusicService.PLAYING_NOTIFY_ID, notification);
                                     }
                                 },
                    CallerThreadExecutor.getInstance());
        }
        notification.contentView = notifyView;
        if (mNotificationManager != null) {
            mNotificationManager.notify(MusicService.PLAYING_NOTIFY_ID, notification);
        }
        MusicService.startService(mContext);
    }

    public void cancel() {
        mNotificationManager.cancelAll();
    }

}
