package com.chestnut.Media.Video;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.chestnut.Common.utils.BarUtils;
import com.chestnut.Common.utils.LogUtils;
import com.chestnut.Media.R;

public class VideoActivity extends AppCompatActivity {

    private boolean OpenLog = true;
    private String TAG = "VideoActivity";

    private ImageView playIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BarUtils.hideStatusBar(this);
        BarUtils.hideNotificationBar(this);
        setContentView(R.layout.activity_video);
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        playIcon = (ImageView) findViewById(R.id.img_pause);

        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.w(OpenLog,TAG,"playIcon");
            }
        });

        //播放完毕显示重新播放按钮
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });

        //开始播放
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

            }
        });

        videoView.setVideoPath(Environment.getExternalStorageDirectory().getPath() + "/1.mp4");
        videoView.start();
        videoView.requestFocus();
    }
}
