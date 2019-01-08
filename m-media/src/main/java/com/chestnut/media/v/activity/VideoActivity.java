package com.chestnut.media.v.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.chestnut.media.R;
import com.chestnut.media.contract.MediaManager;
import com.chestnut.media.contract.VideoBuilder;
import com.chestnut.media.v.fragment.VideoFragment;

public class VideoActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐去标题栏（应用程序的名字）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏方法二：隐去状态栏部分 (电池等图标和一切修饰部分)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.media_activity_video);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //设置builder
        VideoFragment fragment = (VideoFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment);
        Intent intent = getIntent();
        if (intent!=null && intent.getExtras()!=null) {
            long longKey = intent.getLongExtra(MediaManager.Key_Builder, -1);
            Object builder = MediaManager.getInstance().popAndClean(longKey);
            if (builder instanceof VideoBuilder) {
                fragment.setVideoBuilder((VideoBuilder) builder);
            }
        }
    }
}
