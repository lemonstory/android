package com.xiaoningmeng.player;

import android.database.sqlite.SQLiteFullException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.tencent.bugly.crashreport.BuglyLog;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.ReminderActivity;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayStory;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.db.HistoryDao;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.download.DownLoadObserver;
import com.xiaoningmeng.download.DownLoadState;
import com.xiaoningmeng.utils.AudioUtils;
import com.xiaoningmeng.utils.PreferenceUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerManager extends PlayerObservable implements
        OnCompletionListener, OnBufferingUpdateListener, OnErrorListener, OnSeekCompleteListener,
        DownLoadObserver<AudioDownLoad> {

    public static final int PER_UPDATE_TIME = 1000;
    public static final String RECORD_POSITION = "record_position";
    public static final String RECORD_SECONDS = "record_seconds";
    public static final String RECORD_CURRENT = "record_current";
    public static final String RECORD_MODE = "record_mode";
    private List<PlayStory> playStories = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private PlayNotificationManager mNotificationManager;
    private static PlayerManager mInstance;
    private int mPlayMode = PlayMode.CYCLE;
    public int position = 0;
    private PlayingStory mPlayingStory = new PlayingStory();
    private int reminderSong = -1;
    private int computeReminderSong = 0;
    private HistoryDao mHistoryDao;
    public OnPlayingDownloadListener mOnPlayingDownloadListener;
    private PlayErrorMonitor mErrorMonitor;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private ArrayList<String> recentsPlayErrorStoryIds = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (mPlayingStory.albumSource != AlbumSource.SEARCH) {
                    if (mNotificationManager != null) {
                        mNotificationManager.show(mPlayingStory);
                    }
                }
            }
        }
    };

    public static PlayerManager getInstance() {
        if (mInstance == null) {
            synchronized (PlayerManager.class) {
                if (mInstance == null) {
                    mInstance = new PlayerManager();
                }
            }
        }
        return mInstance;
    }

    private PlayerManager() {
        initMediaPlay();
        recovery();
        mNotificationManager = PlayNotificationManager.getInstance();
        DownLoadClientImpl.getInstance().registerObserver(this);
        mHistoryDao = HistoryDao.getInstance();
        mErrorMonitor = new PlayErrorMonitor(this);
    }

    private void initMediaPlay() {

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(MyApplication.getInstance()
                .getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
    }

    // 开始
    public void startPlay(int pos, int current) {

        if (playStories != null && playStories.size() != 0) {
            this.position = checkPositionBoundary(pos);
            PlayStory playStory = playStories.get(position);
            Story story = playStory.getStory();
            if (!playStory.isError() && story.getMediapath() != null) {
                if (checkReminder()) {
                    return;
                }
                mHandler.removeCallbacks(updateRunnable);
                mPlayingStory.playState = PlayState.START;
                mPlayingStory.setStory(story);
                mPlayingStory.current = current;
                mPlayingStory.buffer = mPlayingStory.playType == PlayType.LOCAL ? mPlayingStory.times : 0;
                notifyDataChanged(mPlayingStory);
                Message msg = mHandler.obtainMessage(1);
                mHandler.sendMessageDelayed(msg, 50);
                String playUrl = searchStoryFile(story.getMediapath());
                play(playUrl, current);
                recordHistoryData();
            } else {

                String playErrorStoryId = story.getStoryId();
                if (!recentsPlayErrorStoryIds.contains(playErrorStoryId)) {
                    recentsPlayErrorStoryIds.add(playErrorStoryId);
                    BuglyLog.w("xnm", "PlayerManager->startPlay fail. go into next. story = " + story.toString());
                    //如果是单曲循环,播放失败后,继续播下一首放仍会继续失败进入死循环
                    if (mPlayMode != PlayMode.SINGLE) {
                        nextPlay();
                    }else {
                        position =+1;
                        nextPlay();
                    }
                }else {
                    BuglyLog.w("xnm", "PlayerManager->startPlay fail. recentsPlayErrorStoryIds = " + recentsPlayErrorStoryIds.toString());
                    Toast.makeText(MyApplication.getInstance()
                            .getApplicationContext(),"哎呀,该专辑无法播放,正在紧急修复中",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkReminder() {

        if (reminderSong > 0) {
            computeReminderSong++;
            if (computeReminderSong >= reminderSong) {
                PreferenceUtil.removeString(ReminderActivity.REMINDER_INT);
                PreferenceUtil.removeString(ReminderActivity.REMINDER_TIME);
                reminderSong = -1;
                computeReminderSong = 0;
                close();
                return true;
            }
        }
        return false;
    }

    private void close() {
        com.xiaoningmeng.application.ActivityManager activityManager = com.xiaoningmeng.application.ActivityManager
                .getScreenManager();
        if (activityManager.getActivitiesSize() == 0) {
            MusicService.stopService(MyApplication.getInstance());
            System.exit(0);
        } else {
            pausePlay();
        }
    }

    // 开始
    public void startPlay(int pos) {
        startPlay(pos, 0);

    }

    // 检查位置的边界
    private synchronized int checkPositionBoundary(int position) {

        int realPos = 0;
        int total = playStories.size();
        if (position < 0) {
            realPos = total - 1;
        } else if (position >= total) {
            realPos = 0;
        } else {
            realPos = position;
        }
        return realPos;
    }

    // 暂停
    public void pausePlay() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mPlayingStory.playState = PlayState.PAUSE;
            mHandler.removeCallbacks(updateRunnable);
            mNotificationManager.show(mPlayingStory);
            notifyDataChanged(mPlayingStory);
            recordHistoryData();
            recordPlayingData();
        }
    }

    // 停止
    public void stopPlay() {
        recordHistoryData();
        recordPlayingData();
        if (mediaPlayer != null) {
            mHandler.removeCallbacks(updateRunnable);
            mediaPlayer.stop();
            mPlayingStory.playState = PlayState.STOP;
            mediaPlayer.release();
            mNotificationManager.show(mPlayingStory);
            notifyDataChanged(mPlayingStory);
        }
        mNotificationManager.cancel();
        DownLoadClientImpl.getInstance().unregisterObserver(this);
    }

    // 前一首
    public void prevPlay() {

        switch (mPlayMode) {
            case PlayMode.CYCLE:
                position--;
                startPlay(position);
                break;
            case PlayMode.SINGLE:
                startPlay(position);
                break;
            case PlayMode.RANDOM:
                int size = playStories.size();
                if (size > 0) {
                    position = new java.util.Random().nextInt(playStories.size());
                }
                startPlay(position);
                break;
        }
    }

    // 下一首
    public void nextPlay() {
        switch (mPlayMode) {
            case PlayMode.CYCLE:
                position++;
                startPlay(position);
                break;
            case PlayMode.SINGLE:
                startPlay(position);
                break;
            case PlayMode.RANDOM:
                int size = playStories.size();
                if (size > 0) {
                    position = new java.util.Random().nextInt(playStories.size());
                }
                startPlay(position);
                break;
        }
    }

    public void seekPlay(int current) {

        if (current >= mPlayingStory.times - 2) {
            mPlayingStory.current = (mPlayingStory.times - 2);
        } else {
            mPlayingStory.current = current;
        }
        mediaPlayer.seekTo(mPlayingStory.current * 1000);
        notifyDataChanged(mPlayingStory);
    }

    // 继续播放
    public void resumePlay() {

        if (mPlayingStory.playState == PlayState.PAUSE) {
            mediaPlayer.start();
            mPlayingStory.playState = PlayState.RESUME;
            notifyDataChanged(mPlayingStory);
            mNotificationManager.show(mPlayingStory);
            mHandler.postDelayed(updateRunnable, PER_UPDATE_TIME);
        } else {
            startPlay(position, mPlayingStory.current);
        }
    }

    public void play(final String address, final int current) {
        pool.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(address); // 设置数据源
                    mediaPlayer.prepare(); // prepare自动播放
                    if (current != 0) {
                        mediaPlayer.seekTo(current * 1000);
                    } else {
                        mediaPlayer.start();
                        mHandler.postDelayed(updateRunnable, PER_UPDATE_TIME);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        MobclickAgent.onEvent(MyApplication.getContext(),"event_play");
    }


    public static enum PlayState {
        START, STOP, PLAY, PAUSE, RESUME, ERROR;
    }

    public static class PlayMode {

        public static final int CYCLE = 0;
        public static final int SINGLE = 1;
        public static final int RANDOM = 2;
    }

    public static class AlbumSource {
        public static final int ALBUM_DETAIL = 0;
        public static final int DOWNLOAD = 1;
        public static final int SEARCH = 2;
    }

    public static enum PlayType {
        NET, LOCAL;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (mErrorMonitor.isUpperPlayErrorCountLimit()) {
            //专辑所有音频出现错误
        } else {
            if (mErrorMonitor.isPlayCompleteTooFast()) {
                playStories.get(position).setError(true);

            } else {
                nextPlay();
            }

        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mPlayingStory.buffer = mPlayingStory.times * percent / 100;
        notifyDataChanged(mPlayingStory);
    }

    public void playStory(AlbumInfo albumInfo, List<Story> stories,
                          int position, int albumSource) {
        playStory(albumInfo, stories, position, 0, albumSource);
    }

    public void playStory(AlbumInfo albumInfo, List<Story> stories,
                          int position, int current, int albumSource) {
        if (albumInfo == null || stories == null || stories.size() == 0) {
            return;
        }
        recordHistoryData();
        mErrorMonitor.clearError();
        if (!albumInfo.getAlbumid().equals(mPlayingStory.albumid)
                || albumSource != mPlayingStory.albumSource) {
            mPlayingStory.albumSource = albumSource;
            newStoryList(albumInfo, stories, position, current);
            return;
        }
        if (stories.size() != playStories.size()) {
            newStoryList(albumInfo, stories, position, current);
            return;
        }
        startPlay(position, current);
    }

    public void playSearchStory(List<Story> stories, int position) {
        if (stories == null || stories.size() == 0) {
            return;
        }
        recordHistoryData();
        mErrorMonitor.clearError();
        mPlayingStory.albumSource = AlbumSource.SEARCH;
        newStoryList(null, stories, position, 0);
    }


    private void newStoryList(final AlbumInfo albumInfo, List<Story> stories, int pos, int current) {
        playStories.clear();
        mPlayingStory.albumInfo = albumInfo;
        for (Story story : stories) {
            playStories.add(new PlayStory(story));
        }
        startPlay(pos, current);
        if (mPlayingStory.albumSource != AlbumSource.SEARCH) {
            try{
                DataSupport.deleteAll(PlayStory.class);
                DownLoadClientImpl.getInstance().addAlbum(albumInfo);
                DataSupport.saveAll(playStories);

            }catch (SQLiteFullException e) {
                //clear image cache
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.clearCaches();

                DataSupport.deleteAll(PlayStory.class);
                DownLoadClientImpl.getInstance().addAlbum(albumInfo);
                DataSupport.saveAll(playStories);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clear() {
        this.playStories.clear();
    }

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            mPlayingStory.current = mediaPlayer.getCurrentPosition() / 1000;
            mPlayingStory.playState = PlayState.PLAY;
            notifyDataChanged(mPlayingStory);
            mHandler.postDelayed(this, PER_UPDATE_TIME);
        }
    };

    private String searchStoryFile(String address) {
        String playUrl = null;
        if (address != null) {
            if (address.contains("http") || address.contains("https")) {
                String fileName = AudioUtils.getVideoAbsoluteFileNameByUrl(MyApplication.getContext(), address);
                if (new File(fileName).exists()) {
                    mPlayingStory.playType = PlayType.LOCAL;
                    playUrl = fileName;
                } else {
                    mPlayingStory.playType = PlayType.NET;
                    playUrl = address;
                }
            } else {
                mPlayingStory.playType = PlayType.LOCAL;
                playUrl = address;
            }
        }
        return playUrl;

    }

    public int getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(int playMode) {
        mPlayMode = playMode;
        PreferenceUtil.putInt(RECORD_MODE, mPlayMode);
    }

    public PlayingStory getPlayingStory() {
        return mPlayingStory;
    }

    public List<PlayStory> getPlayList() {
        return playStories;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        playStories.get(position).setError(true);
        mErrorMonitor.incrementTotalError();
        mPlayingStory.playState = PlayState.ERROR;
        notifyDataChanged(mPlayingStory);
        return true;
    }

    public void setReminderSong(int reminderSong) {
        this.reminderSong = reminderSong;
        this.computeReminderSong = 0;
    }

    // 记录
    private void recordPlayingData() {

        PreferenceUtil.putInt(RECORD_POSITION, position);
        PreferenceUtil.putInt(RECORD_SECONDS, mPlayingStory.times);
        PreferenceUtil.putInt(RECORD_CURRENT, mPlayingStory.current);
    }

    private void recordHistoryData() {
        final PlayingStory playingStory = mPlayingStory;
        if (playingStory.albumSource != AlbumSource.SEARCH && playingStory.albumInfo != null) {
            playingStory.albumInfo.setStoryinfo(playingStory.getStory());
            mHistoryDao.add(playingStory.albumInfo, playingStory.storyId,
                    playingStory.current);
        }
    }

    // 恢复
    public void recovery() {

        pool.execute(new Runnable() {

            @Override
            public void run() {
                recoveryPlayingData();
                recoveryPlayData();
            }
        });
    }

    private void recoveryPlayingData() {

        mPlayMode = PreferenceUtil.getInt(RECORD_MODE);
        position = PreferenceUtil.getInt(RECORD_POSITION);
        mPlayingStory.times = PreferenceUtil.getInt(RECORD_SECONDS);
        mPlayingStory.current = PreferenceUtil.getInt(RECORD_CURRENT);


    }

    private void recoveryPlayData() {

        List<PlayStory> playList = DataSupport.findAll(PlayStory.class);
        playStories.clear();
        if (playList != null) {
            playStories.addAll(playList);
            if (playStories.size() > position) {
                PlayStory playMusic = playStories.get(position);
                mPlayingStory.setStory(playMusic.getStory());
                mPlayingStory.albumInfo = DownLoadClientImpl.getInstance().getAlbum(mPlayingStory.albumid);
                searchStoryFile(mPlayingStory.mediapath);
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        notifyDataChanged(mPlayingStory);
                    }
                });
            }
        }
    }

    public boolean isPlaying() {
        if (mPlayingStory.playState == PlayState.PLAY
                || mPlayingStory.playState == PlayState.RESUME) {
            return true;
        }
        return false;
    }


    @Override
    public void notifyData(AudioDownLoad t) {

        String url = t.getUrl();
        if (t.getStatus() == DownLoadState.DOWN_SUC) {
            if (url != null && url.equals(mPlayingStory.mediapath)) {
                mPlayingStory.playType = PlayType.LOCAL;
                if (mOnPlayingDownloadListener != null) {
                    mOnPlayingDownloadListener.notifyDownload();
                }
            }
        }
    }


    @Override
    public void notifyCancel(AudioDownLoad t) {

        String url = t.getUrl();
        if (url != null && url.equals(mPlayingStory.mediapath)) {
            mPlayingStory.playType = PlayType.NET;
            if (mOnPlayingDownloadListener != null) {
                mOnPlayingDownloadListener.notifyDownload();
            }
        }
    }

    public void setOnPlayingDownloadListener(
            OnPlayingDownloadListener onPlayingDownloadListener) {
        this.mOnPlayingDownloadListener = onPlayingDownloadListener;
    }


    public static interface OnPlayingDownloadListener {

        public void notifyDownload();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mp.start();
        mHandler.postDelayed(updateRunnable, PER_UPDATE_TIME);
    }
}
