package com.chestnut.media.v.fragment;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chestnut.media.R;
import com.chestnut.media.contract.IMediaControlView;
import com.chestnut.media.contract.IMediaIcon;
import com.chestnut.media.contract.MediaManager;
import com.chestnut.media.contract.VideoBuilder;
import com.chestnut.media.contract.VideoContract;
import com.chestnut.media.utils.AudioMngHelper;
import com.chestnut.media.utils.LightUtils;
import com.chestnut.media.utils.ScreenUtils;
import com.chestnut.media.utils.TimeUtils;
import com.chestnut.media.v.view.MediaControlView;
import com.chestnut.media.v.view.MediaIconView;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.IOException;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2018/12/25 17:30
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class VideoFragment extends Fragment implements VideoContract.V, View.OnClickListener, View.OnTouchListener{

    private ImageView imgPausePlay;
    private TextView tvTotal, tvProgress, tvTitle;
    private SeekBar seekBarProgress;
    private IMediaIcon<MediaIconView> mediaDialog;
    private IMediaControlView mediaControlView;
    private AudioMngHelper audioMngHelper;
    private GestureDetector gestureDetector;
    private boolean isDragSeekBarByUser = false;
    private boolean hasNavigationBar = false;
    private FrameLayout frameLayout;
    private HttpProxyCacheServer httpProxyCacheServer;
    //缓存监听和设置
    private CacheListener cacheListener = (cacheFile, url, percentsAvailable) -> {
        seekBarProgress.setSecondaryProgress((int) (getDurationSecond()*percentsAvailable*0.01f));
    };

    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private boolean isReady = false;
    private boolean isCompletePlay = false;
    private boolean isRelease = false;
    private boolean isStopState = false;
    private Runnable currentPositionRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRelease) {
                if (!isDragSeekBarByUser) {
                    int current = getCurrentSecond();
                    tvProgress.setText(TimeUtils.toMediaTime(current));
                    seekBarProgress.setProgress(current);
                }
                imgPausePlay.postDelayed(currentPositionRunnable, 1000);
            }
        }
    };

    private VideoBuilder videoBuilder;

    //屏幕调节模式：-1无，0：音量，1：亮度，2：快进退
    private int selectMode = -1;
    private int currentPointWhenScroll = 0;
    private int screenSlideNum = 0;

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            mediaControlView.onDown();
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (!videoBuilder.isShowControlView)
                return super.onScroll(e1, e2, distanceX, distanceY);

            if (selectMode == -1) {
                if (Math.abs(distanceY) > 8) {
                    if (e1.getRawX() >= ScreenUtils.getScreenWidth_PX(getActivity()) / 2) {
                        selectMode = 0;
                        currentPointWhenScroll = audioMngHelper.get100CurrentVolume();
                    }
                    else {
                        selectMode = 1;
                        currentPointWhenScroll = LightUtils.getAppLight100(getActivity());
                    }
                }
                else if (Math.abs(distanceX) > 8) {
                    selectMode = 2;
                    if (isReady)
                        currentPointWhenScroll = (int) (mediaPlayer.getCurrentPosition()/1000);
                    else
                        currentPointWhenScroll = 0;
                }
            }

            switch (selectMode) {
                case 0:
                case 1:
                    int y = 30;//上下调节量
                    int temp;
                    float e1e2YSpace = e2.getRawY() - e1.getRawY();
                    int height = frameLayout.getHeight();
                    if (e1e2YSpace <0) {//+
                        temp = (int) (Math.abs(e1e2YSpace) / (height - y * 2) * 100 + currentPointWhenScroll);
                    }
                    else {//-
                        temp = (int) ( currentPointWhenScroll - Math.abs(e1e2YSpace) / (height - y * 2) * 100);
                    }
                    if (selectMode == 0)
                        mediaDialog.setIcon(R.drawable.media_music)
                                .setTxt(audioMngHelper.setVoice100(temp))
                                .show();
                    else
                        mediaDialog.setIcon(R.drawable.media_light)
                                .setTxt(LightUtils.setAppLight100(getActivity(),temp))
                                .show();
                    break;
                case 2:
                    int x = 30;//左右调节量
                    int width = frameLayout.getWidth();
                    float e1e2XSpace = e2.getRawX() - e1.getRawX();
                    if (e1e2XSpace < 0) {//快退
                        screenSlideNum = (int) (currentPointWhenScroll - Math.abs(e1e2XSpace) / (width - 2 * x) * getDurationSecond());
                        screenSlideNum = screenSlideNum <= 0 ? 0 : screenSlideNum;
                        mediaDialog.setIcon(R.drawable.media_to_left)
                                .setTxt(TimeUtils.toMediaTime(screenSlideNum))
                                .show();
                    }
                    else {//快进
                        int duration = getDurationSecond();
                        screenSlideNum = (int) (currentPointWhenScroll + Math.abs(e1e2XSpace) / (width - 2 * x) * duration);
                        screenSlideNum = screenSlideNum >= duration ? duration : screenSlideNum;
                        mediaDialog.setIcon(R.drawable.media_to_right)
                                .setTxt(TimeUtils.toMediaTime(screenSlideNum))
                                .show();
                    }
                    break;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (selectMode == 2) {
                    seekToSecond(screenSlideNum);
                    tvProgress.setText(TimeUtils.toMediaTime(screenSlideNum));
                    seekBarProgress.setProgress(screenSlideNum);
                }
                selectMode = -1;
                currentPointWhenScroll = 0;
                screenSlideNum = 0;
                mediaDialog.dismiss();
                mediaControlView.onUp();
                break;
        }
        return true;
    }

    public VideoFragment() {}

    public void setVideoBuilder(VideoBuilder videoBuilder) {
        this.videoBuilder = videoBuilder;
        tvTitle.setText(videoBuilder.title);
        mediaControlView.onSetBuilder(videoBuilder.isShowControlView);
        // set data source
        try {
            String url;
            if (videoBuilder.url.contains("http") && videoBuilder.cacheAble) {
                httpProxyCacheServer = MediaManager.getInstance().getProxy(videoBuilder.context.getApplicationContext());
                if (!httpProxyCacheServer.isCached(videoBuilder.url)) {
                    httpProxyCacheServer.registerCacheListener(cacheListener,videoBuilder.url);
                }
                url = httpProxyCacheServer.getProxyUrl(videoBuilder.url);
            }
            else {
                url = videoBuilder.url;
            }
            mediaPlayer.setDataSource(getActivity(), Uri.parse(url));
            mediaPlayer.setLooping(videoBuilder.isLoop);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.media_fragment_video, container, false);

        mediaPlayer = new MediaPlayer();
        mediaDialog = new MediaIconView(rootView.findViewById(R.id.ll_media_icon),rootView.findViewById(R.id.img_icon),rootView.findViewById(R.id.tv_txt));
        audioMngHelper = new AudioMngHelper(getActivity());
        audioMngHelper.setVoiceStep100(1);
        gestureDetector = new GestureDetector(getContext(),simpleOnGestureListener);
        mediaControlView = new MediaControlView(rootView.findViewById(R.id.bottom_view), rootView.findViewById(R.id.top_view));

        imgPausePlay = (ImageView) rootView.findViewById(R.id.img_pause_play);
        ImageView imgBack = (ImageView) rootView.findViewById(R.id.img_back);
        tvTotal = (TextView) rootView.findViewById(R.id.tv_total);
        tvProgress = (TextView) rootView.findViewById(R.id.tv_progress);
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        seekBarProgress = (SeekBar) rootView.findViewById(R.id.seekBar_progress);
        frameLayout = rootView.findViewById(R.id.frame_layout);

        frameLayout.setOnTouchListener(this);
        imgPausePlay.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser)
                    tvProgress.setText(TimeUtils.toMediaTime(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragSeekBarByUser = true;
                mediaControlView.onSeekBarStartTrackingTouch();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragSeekBarByUser = false;
                seekToSecond(seekBar.getProgress());
                mediaControlView.onSeekBarStopTrackingTouch();
            }
        });

        //底部状态栏
        hasNavigationBar = ScreenUtils.checkDeviceHasNavigationBar(getActivity());
        setViewParamsByVirtualBtn(hasNavigationBar);

        // mediaPlayer callback
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBarProgress.setSecondaryProgress((int) (getDurationSecond()*percent*0.01f));
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isReady = true;
                if (!isCompletePlay) {
                    if (videoBuilder.isAutoPlay) {
                        playVideo();
                    }
                    seekBarProgress.setMax(getDurationSecond());
                    tvTotal.setText(TimeUtils.toMediaTime(getDurationSecond()));
                }
                isCompletePlay = false;
                isStopState = false;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isCompletePlay = true;
                int current = 0;
                tvProgress.setText(TimeUtils.toMediaTime(current));
                seekBarProgress.setProgress(current);
                stopVideo();
                if (!isRelease && isReady)
                    mediaPlayer.prepareAsync();
                isReady = false;
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                int current = 0;
                tvProgress.setText(TimeUtils.toMediaTime(current));
                seekBarProgress.setProgress(current);
                stopVideo();
                return false;
            }
        });

        // surface view
        surfaceView = (SurfaceView) rootView.findViewById(R.id.surface_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (!isRelease)
                    mediaPlayer.setDisplay(null);
            }
        });

        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setViewParamsByVirtualBtn(hasNavigationBar);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        resumeVideo();
        mediaDialog.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        release();
        mediaDialog.dismiss();
    }

    private void setViewParamsByVirtualBtn(boolean hasNavigationBar) {
        //底部状态栏
        if (hasNavigationBar) {
            int params = ScreenUtils.getActionBarHeight(getActivity());
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // 当前为横屏
                frameLayout.setPadding(0,0,params,0);
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // 当前为竖屏
                frameLayout.setPadding(0,0,0,params);
            }
        }
    }

    @Override
    public void seekToSecond(int seconds) {
        if (isReady && !isRelease) {
            mediaPlayer.seekTo(seconds*1000);
        }
    }

    @Override
    public int getDurationSecond() {
        if (isRelease) return 0;
        return isReady ? mediaPlayer.getDuration()/1000 : 0;
    }

    @Override
    public int getCurrentSecond() {
        if (isRelease) return 0;
        return isReady ? mediaPlayer.getCurrentPosition()/1000 : 0;
    }

    @Override
    public void pauseVideo() {
        if (!isRelease && !isStopState) {
            mediaControlView.onControlViewClick();
            imgPausePlay.removeCallbacks(currentPositionRunnable);
            imgPausePlay.setImageResource(R.drawable.media_play);
            mediaPlayer.pause();
        }
    }

    @Override
    public void resumeVideo() {
        playVideo();
    }

    @Override
    public void stopVideo() {
        if (!isRelease) {
            imgPausePlay.removeCallbacks(currentPositionRunnable);
            imgPausePlay.setImageResource(R.drawable.media_play);
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            tvProgress.setText(TimeUtils.toMediaTime(0));
            seekBarProgress.setProgress(0);
            isStopState = true;
            isReady = false;
        }
    }

    @Override
    public void playVideo() {
        if (!isRelease) {
            mediaControlView.onControlViewClick();
            if (!isStopState)
                mediaPlayer.start();
            else {
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isReady)
                imgPausePlay.post(currentPositionRunnable);
            imgPausePlay.setImageResource(R.drawable.media_pause);
        }
    }

    @Override
    public void release() {
        if (!isRelease) {
            isRelease = true;
            imgPausePlay.removeCallbacks(currentPositionRunnable);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnInfoListener(null);
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.setOnBufferingUpdateListener(null);
            mediaPlayer.stop();
            mediaPlayer.release();
            if (httpProxyCacheServer!=null)
                httpProxyCacheServer.unregisterCacheListener(cacheListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_pause_play) {
            mediaControlView.onControlViewClick();
            if (isReady) {
                if (mediaPlayer.isPlaying())
                    pauseVideo();
                else
                    playVideo();
            }
        }
        else if (i == R.id.img_back) {
            release();
            getActivity().finish();

        }
    }
}
