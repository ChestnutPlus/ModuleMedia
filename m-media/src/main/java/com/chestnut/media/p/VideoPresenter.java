package com.chestnut.media.p;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.chestnut.media.contract.VideoBuilder;
import com.chestnut.media.contract.VideoContract;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnImageCapturedListener;
import com.pili.pldroid.player.PLOnSeekCompleteListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2018/12/25 16:22
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class VideoPresenter implements VideoContract.P,Runnable {

    private String TAG = "VideoPresenter";
    private VideoContract.V v;
    private VideoBuilder videoBuilder;
    private PLVideoTextureView mVideoView;
    private Handler handler = new Handler();

    private boolean isShowControlView = false;
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            isShowControlView = false;
            if (v!=null)
                v.hideControlView();
        }
    };

    public VideoPresenter(VideoContract.V v, PLVideoTextureView plVideoView) {
        this.v = v;
        mVideoView = plVideoView;
    }

    @Override
    public void onCreate(Context context) {
        AVOptions avOptions = new AVOptions();
        avOptions.setString(AVOptions.KEY_CACHE_DIR,context.getCacheDir().getAbsolutePath());
        mVideoView.setAVOptions(avOptions);
        //缓存监听和设置
        mVideoView.setOnBufferingUpdateListener(new PLOnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(int i) {
                int seconds = (int) (i*0.01*(mVideoView.getDuration()/1000));
                v.setBufferingUpdatePosition(seconds);
            }
        });
        //完成播放监听
        mVideoView.setOnCompletionListener(new PLOnCompletionListener() {
            @Override
            public void onCompletion() {
                v.setIsShowPlayIcon(true);
                v.playCompleteCleanFlags();
                v.setTimeCurrent(0);
            }
        });
        //seekToCallback
        mVideoView.setOnSeekCompleteListener(new PLOnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                v.setTimeCurrent((int) (mVideoView.getCurrentPosition()/1000));
            }
        });
        //截图成功callback
        mVideoView.setOnImageCapturedListener(new PLOnImageCapturedListener() {
            @Override
            public void onImageCaptured(byte[] bytes) {
                //data 参数为编码后的 jpeg 图像数据，可以直接保存为 jpeg 文件。
            }
        });
        //ErrorCallback
        mVideoView.setOnErrorListener(new PLOnErrorListener() {
            @Override
            public boolean onError(int i) {
                Log.i(TAG,"err:"+i);
                return false;
            }
        });
    }

    @Override
    public void setBuilder(VideoBuilder builder) {
        videoBuilder = (VideoBuilder) builder;
        if (!TextUtils.isEmpty(builder.title))
            v.setTitle(builder.title);
        if (!TextUtils.isEmpty(videoBuilder.url)) {
            if (builder.isAutoPlay) {
                playVideo(videoBuilder.url);
            }
        }
        else {
            v.showNotFoundVideo();
        }
    }

    @Override
    public void onPause() {
        pauseVideo();
    }

    @Override
    public void onResume() {
        resumeVideo();
    }

    @Override
    public void onDestroy() {
        stopVideo();
    }

    @Override
    public void playOrPause() {
        resetControlView();
        if (mVideoView.isPlaying()) {
            pauseVideo();
            v.setIsShowPlayIcon(true);
        }
        else {
            resumeVideo();
            v.setIsShowPlayIcon(false);
        }
    }

    @Override
    public void onViewDownTouch() {
        handler.removeCallbacks(hideRunnable);
        if (!isShowControlView) {
            v.showControlView();
            handler.postDelayed(hideRunnable,4000);
            isShowControlView = true;
        }
        else {
            v.hideControlView();
            isShowControlView = false;
        }
    }

    @Override
    public void seekToSecond(int seconds) {
        mVideoView.seekTo(seconds*1000);
        resumeVideo();
    }

    @Override
    public void pauseVideo() {
        resetControlView();
        mVideoView.pause();
        v.setIsShowPlayIcon(true);
        handler.removeCallbacks(this);
    }

    @Override
    public void resumeVideo() {
        resetControlView();
        mVideoView.start();
        v.setIsShowPlayIcon(false);
        handler.post(this);
    }

    @Override
    public void stopVideo() {
        resetControlView();
        mVideoView.stopPlayback();
        v.setIsShowPlayIcon(false);
        handler.removeCallbacks(this);
    }

    @Override
    public void playVideo(String url) {
        resetControlView();
        if (mVideoView!=null) {
            mVideoView.setVideoPath(url);
            mVideoView.start();
            v.setIsShowPlayIcon(false);
            handler.post(this);
        }
    }

    @Override
    public void run() {
        v.setTimeCurrent((int) (mVideoView.getCurrentPosition()/1000));
        v.setTimeTotal((int) (mVideoView.getDuration()/1000));
        handler.postDelayed(this,1000);
    }

    private void resetControlView() {
        handler.removeCallbacks(hideRunnable);
        v.showControlView();
        handler.postDelayed(hideRunnable,4000);
        isShowControlView = true;
    }
}
