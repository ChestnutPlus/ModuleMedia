package com.chestnut.Media.Video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.chestnut.Common.utils.BarUtils;
import com.chestnut.Common.utils.ConvertUtils;
import com.chestnut.Common.utils.LogUtils;
import com.chestnut.Common.utils.ScreenUtils;
import com.chestnut.Media.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/6/2 23:03
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 *          2017年6月16日23:54:45  栗子
 *              1.  基本完成了UI和功能测试
 * </pre>
 */
public class VideoActivity extends AppCompatActivity {

    public static final String VIDEO_URL = "VIDEO_URL";
    public static final String VIDEO_TITLE = "VIDEO_TITLE";
    public static final String VIDEO_TYPE = "VIDEO_TYPE";
    public static final int TYPE_ONLINE = -99;
    public static final int TYPE_LOCAL = -111;

    private boolean OpenLog = true;
    private String TAG = "VideoActivity";

    private XToast xToast;
    private ImageView playIcon;
    private VideoView videoView;
    private SeekBar seekBar;
    private TextView totalTime;
    private TextView playedTime;
    private View bottom_view;
    private View top_view;
    private ProgressBar progressBarLoading;
    private MediaPlayer.OnPreparedListener onPreparedListener;

    private Subscription updatePositionTimerSubscription;
    private Subscription delayHideBottomViewSubscription;
    private boolean isTouchSeekBar = false;
    private boolean isEnd = true;
    private int nowProgress = 0;
    private boolean isError = false;
    private AudioMngHelper audioMngHelper;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BarUtils.hideStatusBar(this);
        BarUtils.hideNotificationBar(this);
        setContentView(R.layout.activity_video);
        videoView = (VideoView) findViewById(R.id.videoView);
        playIcon = (ImageView) findViewById(R.id.img_pause);
        seekBar = (SeekBar) findViewById(R.id.progress);
        playedTime= (TextView) findViewById(R.id.txt_progress);
        totalTime = (TextView) findViewById(R.id.txt_total);
        bottom_view = findViewById(R.id.bottom_view);
        top_view = findViewById(R.id.top_view);
        View layout_view = findViewById(R.id.layout_view);
        progressBarLoading = (ProgressBar) findViewById(R.id.progress_loading);
        xToast = new XToast(this);
        audioMngHelper = new AudioMngHelper(this);
        gestureDetector = new GestureDetector(this,simpleOnGestureListener);

        //退出按钮
        findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying())
                    videoView.pause();
                videoView = null;
                if (updatePositionTimerSubscription!=null && !updatePositionTimerSubscription.isUnsubscribed())
                    updatePositionTimerSubscription.unsubscribe();
                if (delayHideBottomViewSubscription!=null && !delayHideBottomViewSubscription.isUnsubscribed())
                    delayHideBottomViewSubscription.unsubscribe();
                finish();
            }
        });

        //隐藏ControlView
        layout_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        //控制播放按钮
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isError)
                    return;
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playIcon.setImageResource(R.drawable.media_play);
                    stopUpdatePositionTimer();
                    showControlView();
                }
                else {
                    if (videoView.getCurrentPosition()==1213) {
                        videoView.seekTo(0);
                    }
                    videoView.start();
                    playIcon.setImageResource(R.drawable.media_pause);
                    startUpdatePositionTimer();
                    startNewDelayHideControlView();
                }
            }
        });

        //播放完毕
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (isError)
                    return;
                playIcon.setImageResource(R.drawable.media_play);
                seekBar.setProgress(0);
                videoView.seekTo(1213);
                playedTime.setText("00:00");
                stopUpdatePositionTimer();
                showControlView();
                progressBarLoading.setVisibility(View.INVISIBLE);
                isEnd = true;
            }
        });

        //开始播放
        onPreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (isError || onPause) {
                    onPause = false;
                    return;
                }
                playIcon.setImageResource(R.drawable.media_pause);
                seekBar.setProgress(videoView.getCurrentPosition()/1000);
                seekBar.setMax(videoView.getDuration()/1000);
                totalTime.setText(TimeUtils.toMediaTime(videoView.getDuration()/1000));
                playedTime.setText(TimeUtils.toMediaTime(videoView.getCurrentPosition()/1000));
                startUpdatePositionTimer();
                startNewDelayHideControlView();
                isEnd = false;
//                LogUtils.e(OpenLog,TAG,"setOnPreparedListener");
            }
        };
        videoView.setOnPreparedListener(onPreparedListener);

        //播放Error
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                isError = true;
                showControlView();
                return false;
            }
        });

        //缓冲监听
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                    if (!isEnd)
                        progressBarLoading.setVisibility(View.VISIBLE);
                    showControlView();
                    LogUtils.i(OpenLog,TAG,"");
                }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                    //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                    if(mp.isPlaying()){
                        if (!isEnd)
                            progressBarLoading.setVisibility(View.INVISIBLE);
                        startNewDelayHideControlView();
                    }
                }
                return true;
            }
        });

        //进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (isError)
                    return;
                if (isTouchSeekBar) {
                    if (i-nowProgress>0) {
                        xToast.setIcon(R.drawable.media_to_right).setTxt(TimeUtils.toMediaTime(i)).show();
                    }
                    else {
                        xToast.setIcon(R.drawable.media_to_left).setTxt(TimeUtils.toMediaTime(i)).show();
                    }
                    nowProgress = i;
                    playedTime.setText(TimeUtils.toMediaTime(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isError)
                    return;
                isTouchSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isError)
                    return;
                videoView.seekTo(nowProgress*1000);
                isTouchSeekBar = false;
                nowProgress = 0;
                startNewDelayHideControlView();
            }
        });

        //尝试获取Title & URL
        if (getIntent().getExtras()!=null) {
            String title = getIntent().getExtras().getString(VIDEO_TITLE, null);
            if (title != null && title.length() != 0) {
                TextView titleView = (TextView) findViewById(R.id.txt_title);
                titleView.setText(title);
            }

            String url = getIntent().getExtras().getString(VIDEO_URL, null);
            int type = getIntent().getExtras().getInt(VIDEO_TYPE,-1);
            if (url != null && url.length() != 0 && (type==TYPE_ONLINE || type==TYPE_LOCAL)) {
                if (type==TYPE_LOCAL) {
                    videoView.setVideoPath(url);
                    progressBarLoading.setVisibility(View.GONE);
                }
                else {
                    videoView.setVideoURI(Uri.parse(url));
                    progressBarLoading.setVisibility(View.VISIBLE);
                }
                videoView.start();
                videoView.requestFocus();
                delayHideControlView();
            }
            else {
                isError = true;
            }
        }
        else {
            isError = true;
        }
    }

    /**
     * 开启更新进度条倒计时
     */
    private void startUpdatePositionTimer() {
        if (isError)
            return;
        updatePositionTimerSubscription = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (!isTouchSeekBar && videoView!=null) {
                            seekBar.setProgress(videoView.getCurrentPosition() / 1000);
                            if (videoView.getCurrentPosition()/1000>videoView.getDuration()/1000)
                                playedTime.setText(TimeUtils.toMediaTime(videoView.getDuration()/1000));
                            else
                                playedTime.setText(TimeUtils.toMediaTime(videoView.getCurrentPosition()/1000));
//                            LogUtils.e(OpenLog,TAG,"getCurrentPosition:"+videoView.getCurrentPosition());
                        }
                    }
                });
    }

    /**
     * 停止更新进度条倒计时
     */
    private void stopUpdatePositionTimer() {
        if (isError)
            return;
        if (updatePositionTimerSubscription!=null && !updatePositionTimerSubscription.isUnsubscribed())
            updatePositionTimerSubscription.unsubscribe();
        updatePositionTimerSubscription = null;
    }

    /**
     * 倒计时，最终隐藏Bottom View
     */
    private void delayHideControlView() {
        if (isError)
            return;
        delayHideBottomViewSubscription = Observable.interval(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (!isTouchSeekBar) {
                            bottom_view.setVisibility(View.INVISIBLE);
                            top_view.setVisibility(View.INVISIBLE);
                        }
                        if (delayHideBottomViewSubscription!=null && !delayHideBottomViewSubscription.isUnsubscribed())
                            delayHideBottomViewSubscription.unsubscribe();
                        delayHideBottomViewSubscription = null;
                    }
                });
    }

    /**
     * 取消前一个隐藏倒计时，开启新的一个倒计时
     */
    private void startNewDelayHideControlView() {
        if (isError)
            return;
        bottom_view.setVisibility(View.VISIBLE);
        top_view.setVisibility(View.VISIBLE);
        if (delayHideBottomViewSubscription!=null && !delayHideBottomViewSubscription.isUnsubscribed())
            delayHideBottomViewSubscription.unsubscribe();
        delayHideBottomViewSubscription = null;
        delayHideControlView();
    }

    /**
     * 显示BottomView
     */
    private void showControlView() {
        if (isError)
            return;
        bottom_view.setVisibility(View.VISIBLE);
        top_view.setVisibility(View.VISIBLE);
        if (delayHideBottomViewSubscription!=null && !delayHideBottomViewSubscription.isUnsubscribed())
            delayHideBottomViewSubscription.unsubscribe();
        delayHideBottomViewSubscription = null;
    }

    @Override
    public void onBackPressed() {
        if (videoView.isPlaying())
            videoView.pause();
        videoView = null;
        if (updatePositionTimerSubscription!=null && !updatePositionTimerSubscription.isUnsubscribed())
            updatePositionTimerSubscription.unsubscribe();
        if (delayHideBottomViewSubscription!=null && !delayHideBottomViewSubscription.isUnsubscribed())
            delayHideBottomViewSubscription.unsubscribe();
        finish();
    }

    //保存播放进度：
    private boolean onPause = false;
    private boolean onPauseStorePlaying = false;
    private int onPauseStoreProgress = 0;
    @Override
    protected void onPause() {
        super.onPause();
        onPause = true;
        if (videoView!=null) {
            onPauseStorePlaying = videoView.isPlaying();
            onPauseStoreProgress = videoView.getCurrentPosition();
            videoView.pause();
            videoView.setOnPreparedListener(null);
        }
        if (updatePositionTimerSubscription!=null && !updatePositionTimerSubscription.isUnsubscribed())
            updatePositionTimerSubscription.unsubscribe();
        if (delayHideBottomViewSubscription!=null && !delayHideBottomViewSubscription.isUnsubscribed())
            delayHideBottomViewSubscription.unsubscribe();
        LogUtils.e(OpenLog,TAG,"onPause-info:"+onPauseStorePlaying+","+onPauseStoreProgress);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e(OpenLog,TAG,"onRestart-info:"+onPauseStorePlaying+","+onPauseStoreProgress);
        if (videoView!=null) {
            videoView.setOnPreparedListener(onPreparedListener);
            videoView.seekTo(onPauseStoreProgress);
            if (onPauseStorePlaying) {
                videoView.start();
                videoView.requestFocus();
                playIcon.setImageResource(R.drawable.media_pause);
                startUpdatePositionTimer();
                startNewDelayHideControlView();
            }
            else {
                playIcon.setImageResource(R.drawable.media_play);
            }
        }
    }

    /**
     * 监听音量实体按键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int a;
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                xToast.setIcon(R.drawable.media_music).setTxt(audioMngHelper.setVoiceStep100(2).subVoice100()).show();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                xToast.setIcon(R.drawable.media_music).setTxt(audioMngHelper.setVoiceStep100(2).addVoice100()).show();
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 屏幕左右活动，上下滑动监听
     */
    private int temp_y = 0;
    private int temp_x = 0;
    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        //单击显示UI，进度条
        @Override
        public boolean onDown(MotionEvent e) {
            temp_y = 0;
            if (isError)
                return false;
            if (bottom_view.getVisibility()==View.VISIBLE) {
                bottom_view.setVisibility(View.INVISIBLE);
                top_view.setVisibility(View.INVISIBLE);
            }
            else {
                if (videoView.isPlaying()) {
                    startNewDelayHideControlView();
                }
                else {
                    showControlView();
                }
            }
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (isError)
                return super.onScroll(e1, e2, distanceX, distanceY);

            int y = (int) Math.ceil(distanceY);
            int x = (int) Math.ceil(distanceX);

            //大于10px的时候，默认是快进/快退
            if (Math.abs(distanceX)>10) {
                temp_y = 0;
                temp_x += Math.abs(x);
                //为一次调节
                if (temp_x>15) {
                    temp_x = 0;
                    //快进
                    if (distanceX<0) {
                        int a = videoView.getCurrentPosition() + 1000;
                        a = a >= videoView.getDuration() ? videoView.getDuration() : a;
                        videoView.seekTo(a);
                        xToast.setIcon(R.drawable.media_to_right).setTxt(TimeUtils.toMediaTime((int) (a*0.001))).show();
                        seekBar.setProgress(a/1000);
                    }
                    //快退
                    else {
                        int a = videoView.getCurrentPosition() - 1000;
                        a = a <= 0 ? 0 : a;
                        videoView.seekTo(a);
                        xToast.setIcon(R.drawable.media_to_left).setTxt(TimeUtils.toMediaTime((int) (a*0.001))).show();
                        seekBar.setProgress(a/1000);
                    }
                }
            }
            else if (Math.abs(distanceY) > 10) {
                temp_x = 0;
                temp_y += Math.abs(y);
                //在屏幕的上下正负60dp内才可以调节。
                if (e2.getRawY() > ConvertUtils.dp2px(VideoActivity.this, 60)
                        && e2.getRawY() < ScreenUtils.getScreenHeight_PX(VideoActivity.this) - ConvertUtils.dp2px(VideoActivity.this, 60)
                        && temp_y > 15
                        ) {
                    temp_y = 0;
                    //调节音量
                    if (e1.getRawX() >= ScreenUtils.getScreenWidth_PX(VideoActivity.this) / 2) {
                        xToast.setIcon(R.drawable.media_music);
                        if (distanceY > 0)
                            xToast.setTxt(audioMngHelper.setVoice100(audioMngHelper.setVoiceStep100(1).addVoice100())).show();
                        else
                            xToast.setTxt(audioMngHelper.setVoice100(audioMngHelper.setVoiceStep100(1).subVoice100())).show();
                    }
                    //调节亮度
                    else {
                        xToast.setIcon(R.drawable.media_light);
                        if (distanceY > 0)
                            xToast.setTxt(LightUtils.addLight100(VideoActivity.this)).show();
                        else
                            xToast.setTxt(LightUtils.subLight100(VideoActivity.this)).show();
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };
}
