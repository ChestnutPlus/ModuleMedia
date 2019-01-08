package com.chestnut.media.v.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.chestnut.media.R;

import java.io.IOException;

public class XVideoActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐去标题栏（应用程序的名字）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏方法二：隐去状态栏部分 (电池等图标和一切修饰部分)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_xvideo);

        SurfaceView surfaceView = findViewById(R.id.surface_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        MediaPlayer mediaPlayer = new MediaPlayer();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mediaPlayer.setDisplay(null);
            }
        });
        try {
            mediaPlayer.setDataSource(this, Uri.parse("/sdcard/_23.mp4"));
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
