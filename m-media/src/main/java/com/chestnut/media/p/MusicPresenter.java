package com.chestnut.media.p;

import android.media.MediaPlayer;
import android.os.Handler;

import com.chestnut.media.contract.MediaManager;
import com.chestnut.media.contract.MusicBuilder;
import com.chestnut.media.contract.MusicContract;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;
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

    private MediaPlayer mediaPlayer;
    private MusicBuilder.Callback callback;
    private boolean isPrepared = false;
    private boolean isSetPlay = false;
    private Handler handler;
    private HttpProxyCacheServer httpProxyCacheServer;
    private MusicBuilder musicBuilder;
    private Runnable updatePlayProgress = new Runnable() {
        @Override
        public void run() {
            if (callback!=null) {
                callback.onProgressChange(getCurrentSecond(),getTotalSecond());
            }
            handler.postDelayed(updatePlayProgress,1000);
        }
    };
    //缓存监听和设置
    private CacheListener cacheListener = new CacheListener() {
        @Override
        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            if (callback != null)
                callback.onBuffering(percentsAvailable);
        }
    };

    public MusicPresenter() {
        if (handler==null)
            handler = new Handler();
        initPlayer();
    }

    private void initPlayer() {
        if (mediaPlayer!=null)
            mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        //准备好的回调
        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            isPrepared = true;
            handler.removeCallbacks(updatePlayProgress);
            if (isSetPlay) {
                handler.post(updatePlayProgress);
                mediaPlayer.start();
            }
        });
        //完成播放监听
        mediaPlayer.setOnCompletionListener((mediaPlayer) -> {
            handler.removeCallbacks(updatePlayProgress);
            if (callback != null)
                callback.onCompletion();
        });
        //ErrorCallback
        mediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
            if (callback != null)
                callback.onErr(i, "code:"+i+","+i1);
            return false;
        });
        //buff
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            if (callback != null)
                callback.onBuffering(percent);
        });
    }

    @Override
    public void setBuilder(MusicBuilder builder) {
        initPlayer();
        this.callback = builder.callback;
        this.musicBuilder = builder;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        try {
            isPrepared = false;
            handler.removeCallbacks(updatePlayProgress);
            String url;
            if (builder.url.contains("http") && builder.cacheAble) {
                httpProxyCacheServer = MediaManager.getInstance().getProxy(builder.context.getApplicationContext());
                if (!httpProxyCacheServer.isCached(builder.url)) {
                    httpProxyCacheServer.registerCacheListener(cacheListener,builder.url);
                }
                url = httpProxyCacheServer.getProxyUrl(builder.url);
            }
            else {
                url = builder.url;
            }
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (mediaPlayer!=null) {
            if (isPrepared) {
                handler.post(updatePlayProgress);
                mediaPlayer.start();
            }
            else {
                isSetPlay = true;
            }
        }
        if (callback!=null)
            callback.onStart();
    }

    @Override
    public void stop() {
        if (mediaPlayer!=null) {
            mediaPlayer.pause();
            seekToSecond(0);
        }
        handler.removeCallbacks(updatePlayProgress);
        if (callback!=null)
            callback.onStop();
    }

    @Override
    public void pause() {
        if (mediaPlayer!=null) {
            mediaPlayer.pause();
        }
        handler.removeCallbacks(updatePlayProgress);
        if (callback!=null)
            callback.onPause();
    }

    @Override
    public int getCurrentSecond() {
        if (mediaPlayer!=null && isPrepared) {
            return (int) (mediaPlayer.getCurrentPosition()/1000);
        }
        else
            return 0;
    }

    @Override
    public int getTotalSecond() {
        if (mediaPlayer!=null && isPrepared) {
            return (int) (mediaPlayer.getDuration()/1000);
        }
        else
            return 0;
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    public void seekToSecond(int seconds) {
        if (mediaPlayer!=null) {
            mediaPlayer.seekTo(seconds * 1000);
        }
    }

    @Override
    public void release() {
        if (mediaPlayer!=null)
            mediaPlayer.release();
        handler.removeCallbacks(updatePlayProgress);
        if (httpProxyCacheServer!=null)
            httpProxyCacheServer.unregisterCacheListener(cacheListener);
    }

    @Override
    public void onResume() {
        if (musicBuilder!=null) {
            if (musicBuilder.isAutoPlay)
                start();
        }
    }

    @Override
    public void onPause() {
        pause();
    }
}
