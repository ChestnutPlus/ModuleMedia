package com.chestnut.media.p;

import android.content.Context;
import android.os.Handler;

import com.chestnut.media.contract.MusicBuilder;
import com.chestnut.media.contract.MusicContract;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.PLOnSeekCompleteListener;

import java.io.IOException;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2018/12/27 22:53
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */

public class MusicPresenter implements MusicContract.P{

    private PLMediaPlayer plMediaPlayer;
    private MusicBuilder.Callback callback;
    private int seekToSeconds = 0;
    private boolean isPrepared = false;
    private Handler handler;
    private Runnable updatePlayProgress = new Runnable() {
        @Override
        public void run() {
            if (callback!=null) {
                callback.onProgressChange(getCurrentSecond(),getTotalSecond());
            }
            handler.postDelayed(updatePlayProgress,1000);
        }
    };

    public MusicPresenter(Context context) {
        if (handler==null)
            handler = new Handler();
        if (plMediaPlayer==null) {
            AVOptions avOptions = new AVOptions();
            avOptions.setString(AVOptions.KEY_CACHE_DIR,context.getCacheDir().getAbsolutePath());
            plMediaPlayer = new PLMediaPlayer(context.getApplicationContext(), avOptions);
            //准备好的回调
            plMediaPlayer.setOnPreparedListener(new PLOnPreparedListener() {
                @Override
                public void onPrepared(int i) {
                    isPrepared = true;
                    handler.removeCallbacks(updatePlayProgress);
                    handler.post(updatePlayProgress);
//                plMediaPlayer.start();
                }
            });
            //缓存监听和设置
            plMediaPlayer.setOnBufferingUpdateListener(i -> {
                if (callback != null)
                    callback.onBuffering(i);
            });
            //完成播放监听
            plMediaPlayer.setOnCompletionListener(() -> {
                handler.removeCallbacks(updatePlayProgress);
                if (callback != null)
                    callback.onCompletion();
            });
            //seekToCallback
            plMediaPlayer.setOnSeekCompleteListener(new PLOnSeekCompleteListener() {
                @Override
                public void onSeekComplete() {
                    if (callback != null)
                        callback.onSeekTo(seekToSeconds);
                    seekToSeconds = 0;
                }
            });
            //ErrorCallback
            plMediaPlayer.setOnErrorListener(i -> {
                if (callback != null)
                    callback.onErr(i, "null");
                return false;
            });
        }
    }

    @Override
    public void setBuilder(MusicBuilder builder) {
        this.callback = builder.callback;
        if (plMediaPlayer.isPlaying()) {
            plMediaPlayer.stop();
        }
        try {
            isPrepared = false;
            handler.removeCallbacks(updatePlayProgress);
            plMediaPlayer.setDataSource(builder.url);
            plMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (plMediaPlayer!=null) {
            plMediaPlayer.setBufferingEnabled(true);
            plMediaPlayer.start();
            if (isPrepared)
                handler.post(updatePlayProgress);
        }
        if (callback!=null)
            callback.onStart();
    }

    @Override
    public void stop() {
        if (plMediaPlayer!=null) {
            plMediaPlayer.stop();
        }
        handler.removeCallbacks(updatePlayProgress);
        if (callback!=null)
            callback.onStop();
    }

    @Override
    public void pause() {
        if (plMediaPlayer!=null) {
            plMediaPlayer.pause();
        }
        handler.removeCallbacks(updatePlayProgress);
        if (callback!=null)
            callback.onPause();
    }

    @Override
    public int getCurrentSecond() {
        if (plMediaPlayer!=null && isPrepared) {
            return (int) (plMediaPlayer.getCurrentPosition()/1000);
        }
        else
            return 0;
    }

    @Override
    public int getTotalSecond() {
        if (plMediaPlayer!=null && isPrepared) {
            return (int) (plMediaPlayer.getDuration()/1000);
        }
        else
            return 0;
    }

    @Override
    public void seekToSecond(int seconds) {
        if (plMediaPlayer!=null) {
            plMediaPlayer.seekTo(seconds * 1000);
            seekToSeconds = seconds;
        }
    }

    @Override
    public void release() {
        if (plMediaPlayer!=null)
            plMediaPlayer.release();
        handler.removeCallbacks(updatePlayProgress);
    }
}
