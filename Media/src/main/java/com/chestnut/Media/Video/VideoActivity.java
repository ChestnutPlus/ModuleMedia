package com.chestnut.Media.Video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.chestnut.Common.utils.BarUtils;
import com.chestnut.Media.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class VideoActivity extends AppCompatActivity {

    public static String VIDEO_URL = "VIDEO_URL";
    public static String VIDEO_TITLE = "VIDEO_TITLE";
    public static String VIDEO_TYPE = "VIDEO_TYPE";

    public static int TYPE_ONLINE = -99;
    public static int TYPE_LOCAL = -111;

    private boolean OpenLog = true;
    private String TAG = "VideoActivity";

    private ImageView playIcon;
    private VideoView videoView;
    private SeekBar seekBar;
    private TextView totalTime;
    private TextView playedTime;
    private View bottom_view;
    private View top_view;

    private Subscription updatePositionTimerSubscription;
    private Subscription delayHideBottomViewSubscription;
    private boolean isTouchSeekBar = false;
    private int nowProgress = 0;
    private boolean isError = false;

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
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
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
                return false;
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
                videoView.seekTo(0);
                playedTime.setText("00:00");
                stopUpdatePositionTimer();
                showControlView();
            }
        });

        //开始播放
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (isError)
                    return;
                playIcon.setImageResource(R.drawable.media_pause);
                seekBar.setProgress(videoView.getCurrentPosition()/1000);
                seekBar.setMax(videoView.getDuration()/1000);
                totalTime.setText(TimeUtils.toMediaTime(videoView.getDuration()/1000));
                playedTime.setText(TimeUtils.toMediaTime(videoView.getCurrentPosition()/1000));
                startUpdatePositionTimer();
            }
        });

        //进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (isError)
                    return;
                if (isTouchSeekBar) {
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
                if (type==TYPE_LOCAL)
                    videoView.setVideoPath(url);
                else
                    videoView.setVideoURI(Uri.parse(url));
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
                        if (!isTouchSeekBar) {
                            seekBar.setProgress(videoView.getCurrentPosition() / 1000);
                            if (videoView.getCurrentPosition()/1000>videoView.getDuration()/1000)
                                playedTime.setText(TimeUtils.toMediaTime(videoView.getDuration()/100));
                            else
                                playedTime.setText(TimeUtils.toMediaTime(videoView.getCurrentPosition()/1000));
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
}
