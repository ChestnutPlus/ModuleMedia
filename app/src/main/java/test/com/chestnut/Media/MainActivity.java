package test.com.chestnut.Media;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chestnut.media.contract.MediaManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //视频-本地
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaManager.videoBuilder()
                        .setAutoPlay(true)
                        .setTitle("小幸运日文版")
                        .setUrl("/sdcard/_23.mp4")
                        .build(MainActivity.this);
            }
        });
        //视频-网络
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaManager.videoBuilder()
                        .setAutoPlay(true)
                        .setTitle("Yellow")
                        .setUrl("http://1258339144.vod2.myqcloud.com/1b77e3cavodcq1258339144/f3ae580c5285890783708931103/4ayzIpIJ77gA.mp4")
                        .build(MainActivity.this);
            }
        });
        //音频
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MusicTestActivity.class));
            }
        });
        //音频-本地
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaManager.musicBuilder()
                        .setAutoPlay(true)
                        .setTitle("陈奕迅-圣诞结")
                        .setUrl("/sdcard/adb.mp3")
                        .build(MainActivity.this);
            }
        });
        //音频-网络
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaManager.musicBuilder()
                        .setAutoPlay(true)
                        .setTitle("心酸")
                        .setUrl("http://h5.honeybot.cn/c.mp3")
                        .build(MainActivity.this);
            }
        });
    }
}
