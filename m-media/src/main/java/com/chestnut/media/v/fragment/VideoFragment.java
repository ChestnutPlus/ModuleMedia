package com.chestnut.media.v.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chestnut.media.R;
import com.chestnut.media.contract.Builder;
import com.chestnut.media.contract.MediaManager;
import com.chestnut.media.contract.VideoBuilder;
import com.chestnut.media.contract.VideoContract;
import com.chestnut.media.p.VideoPresenter;
import com.chestnut.media.utils.ScreenUtils;
import com.chestnut.media.utils.TimeUtils;
import com.chestnut.media.utils.AudioMngHelper;
import com.chestnut.media.utils.LightUtils;
import com.chestnut.media.v.view.MyToast;
import com.pili.pldroid.player.widget.PLVideoTextureView;

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
public class VideoFragment extends Fragment implements VideoContract.V, View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener{

    private VideoContract.P p;
    private ImageView imgPausePlay;
    private TextView tvTotal, tvProgress, tvTitle;
    private SeekBar seekBarProgress;
    private View bottomLayout, topLayout;
    private MyToast xToast;
    private AudioMngHelper audioMngHelper;
    private GestureDetector gestureDetector;
    private int currentPosition = 0;
    private boolean isDragSeekBarByUser = false;
    private boolean hasNavigationBar = false;
    private FrameLayout frameLayout;
    private PLVideoTextureView mVideoView;

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            p.onViewDownTouch();
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (Math.abs(distanceY)>5) {
                //调节音量：屏幕的右一半
                if (e1.getRawX() >= ScreenUtils.getScreenWidth_PX(getActivity()) / 2) {
                    xToast.setIcon(R.drawable.media_music);
                    if (distanceY > 0)
                        xToast.setTxt(audioMngHelper.addVoice100()).show();
                    else
                        xToast.setTxt(audioMngHelper.subVoice100()).show();
                }
                //调节亮度：屏幕的左一半
                else {
                    xToast.setIcon(R.drawable.media_light);
                    if (distanceY > 0)
                        xToast.setTxt(LightUtils.addLight100(getActivity())).show();
                    else
                        xToast.setTxt(LightUtils.subLight100(getActivity())).show();
                }
            }
//            else if (Math.abs(distanceX)>5) {
//                int screenWidth = ScreenUtils.getScreenWidth_PX(getActivity());
//                float x = Math.abs(distanceX)/screenWidth;
//                int max = (int) mVideoView.getDuration();
//                float temp = max * x * 0.001f;
//                //快进
//                if (distanceX<0) {
//                    currentPosition += temp;
//                    currentPosition = currentPosition > max ? max : currentPosition;
//                    p.seekToSecond(currentPosition);
//                    xToast.setIcon(R.drawable.media_to_right)
//                            .setTxt(TimeUtils.toMediaTime(currentPosition))
//                            .show();
//                }
//                //快退
//                else {
//                    currentPosition -= temp;
//                    currentPosition = currentPosition < 0 ? 0 : currentPosition;
//                    p.seekToSecond(currentPosition);
//                    xToast.setIcon(R.drawable.media_to_left)
//                            .setTxt(TimeUtils.toMediaTime(currentPosition))
//                            .show();
//                }
//            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    public VideoFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.media_fragment_video, container, false);

        xToast = new MyToast(getActivity());
        audioMngHelper = new AudioMngHelper(getActivity());
        audioMngHelper.setVoiceStep100(1);
        gestureDetector = new GestureDetector(getContext(),simpleOnGestureListener);

        mVideoView = (PLVideoTextureView) rootView.findViewById(R.id.pl_video_view);
        View loadingView = rootView.findViewById(R.id.progress_loading);
        mVideoView.setBufferingIndicator(loadingView);
        imgPausePlay = (ImageView) rootView.findViewById(R.id.img_pause_play);
        ImageView imgBack = (ImageView) rootView.findViewById(R.id.img_back);
        tvTotal = (TextView) rootView.findViewById(R.id.tv_total);
        tvProgress = (TextView) rootView.findViewById(R.id.tv_progress);
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        seekBarProgress = (SeekBar) rootView.findViewById(R.id.seekBar_prgress);
        bottomLayout = rootView.findViewById(R.id.bottom_view);
        topLayout = rootView.findViewById(R.id.top_view);
        frameLayout = rootView.findViewById(R.id.frame_layout);

        frameLayout.setOnTouchListener(this);
        imgPausePlay.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        seekBarProgress.setOnSeekBarChangeListener(this);

        p = new VideoPresenter(this, mVideoView);
        p.onCreate(getActivity());
        Intent intent = getActivity().getIntent();
        if (intent!=null && intent.getExtras()!=null) {
            long longKey = intent.getLongExtra(MediaManager.Key_Builder, -1);
            Builder builder = MediaManager.getInstance().popAndClean(longKey);
            if (builder instanceof VideoBuilder) {
                p.setBuilder((VideoBuilder) builder);
            }
        }

        //底部状态栏
        hasNavigationBar = ScreenUtils.checkDeviceHasNavigationBar(getActivity());
        setViewParamsByVirtualBtn(hasNavigationBar);
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setViewParamsByVirtualBtn(hasNavigationBar);
        super.onConfigurationChanged(newConfig);
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
    public void onPause() {
        super.onPause();
        p.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        p.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        p.onDestroy();
    }

    @Override
    public void playVideo(String url) {
        p.playVideo(url);
    }

    @Override
    public void showNotFoundVideo() {
        getActivity().runOnUiThread(()->{
            Toast.makeText(getContext(),"video not found!",Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void setIsShowPlayIcon(boolean isShow) {
        getActivity().runOnUiThread(()->{
            imgPausePlay.setImageResource(isShow?R.drawable.media_play:R.drawable.media_pause);
        });
    }

    @Override
    public void setTimeTotal(int seconds) {
        getActivity().runOnUiThread(()->{
            tvTotal.setText(TimeUtils.toMediaTime(seconds));
            seekBarProgress.setMax(seconds);
        });
    }

    @Override
    public void setBufferingUpdatePosition(int seconds) {
        getActivity().runOnUiThread(()->{
            seekBarProgress.setSecondaryProgress(seconds);
        });
    }

    @Override
    public void setTimeCurrent(int seconds) {
        getActivity().runOnUiThread(()->{
            if (currentPosition<=seconds && !isDragSeekBarByUser) {
                currentPosition = seconds;
                tvProgress.setText(TimeUtils.toMediaTime(currentPosition));
                seekBarProgress.setProgress(currentPosition);
            }
        });
    }

    @Override
    public void playCompleteCleanFlags() {
        currentPosition = 0;
    }

    @Override
    public void setTitle(String title) {
        getActivity().runOnUiThread(()->{
            tvTitle.setText(title);
        });
    }

    @Override
    public void showControlView() {
        if (getActivity()!=null)
            getActivity().runOnUiThread(()->{
                bottomLayout.setVisibility(View.VISIBLE);
                topLayout.setVisibility(View.VISIBLE);
            });
    }

    @Override
    public void hideControlView() {
        if (getActivity()!=null)
            getActivity().runOnUiThread(()->{
                bottomLayout.setVisibility(View.INVISIBLE);
                topLayout.setVisibility(View.INVISIBLE);
            });
    }

    @Override
    public void seekToSecond(int seconds) {
        p.seekToSecond(seconds);
    }

    @Override
    public void pauseVideo() {
        p.pauseVideo();
    }

    @Override
    public void resumeVideo() {
        p.resumeVideo();
    }

    @Override
    public void stopVideo() {
        p.stopVideo();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_pause_play) {
            p.playOrPause();

        }
        else if (i == R.id.img_back) {
            p.onPause();
            getActivity().finish();

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            tvProgress.setText(TimeUtils.toMediaTime(progress));
            seekBarProgress.setProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragSeekBarByUser = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        currentPosition = seekBar.getProgress();
        p.seekToSecond(currentPosition);
        isDragSeekBarByUser = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
}
