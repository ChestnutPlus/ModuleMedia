package test.com.chestnut.Media;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chestnut.Media.Music.MusicActivity;
import com.chestnut.Media.Video.VideoActivity;

public class MainActivity extends AppCompatActivity {

    private boolean OpenLog = true;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //视频-本地
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                intent.putExtra(VideoActivity.VIDEO_TITLE,"Joe_Grace");
//                intent.putExtra(VideoActivity.VIDEO_URL, Environment.getExternalStorageDirectory().getPath() + "/44.flv");
//                intent.putExtra(VideoActivity.VIDEO_URL, Environment.getExternalStorageDirectory().getPath() + "/guoge.mkv");
                intent.putExtra(VideoActivity.VIDEO_URL, Environment.getExternalStorageDirectory().getPath() + "/413082254412089708.mp4");
//                intent.putExtra(VideoActivity.VIDEO_URL, "/storage/emulated/0/Android/data/com.huiyu.honeybot.honeybotapplication/974920378#test/files/df8c60ad031245709f7161a843f3ecd2/285750406804406812/a4212a30-e52f-11e7-97f3-a5cf69b1c7f2.mp4");
                intent.putExtra(VideoActivity.VIDEO_TYPE,VideoActivity.TYPE_LOCAL);
                startActivity(intent);
            }
        });

        //视频-网络
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                intent.putExtra(VideoActivity.VIDEO_TITLE,"Yellow");
                intent.putExtra(VideoActivity.VIDEO_URL,"http://1258339144.vod2.myqcloud.com/1b77e3cavodcq1258339144/f3ae580c5285890783708931103/4ayzIpIJ77gA.mp4");
                intent.putExtra(VideoActivity.VIDEO_TYPE,VideoActivity.TYPE_ONLINE);
                startActivity(intent);
            }
        });
        //音频
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MusicActivity.class);


                startActivity(intent);
            }
        });
    }
}
