package test.com.chestnut.Media;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
                intent.putExtra(VideoActivity.VIDEO_TITLE,"南山南");
                intent.putExtra(VideoActivity.VIDEO_URL, Environment.getExternalStorageDirectory().getPath() + "/1.mp4");
                intent.putExtra(VideoActivity.VIDEO_TYPE,VideoActivity.TYPE_LOCAL);
                startActivity(intent);
            }
        });

        //视频-网络
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                intent.putExtra(VideoActivity.VIDEO_TITLE,"南山南");
                intent.putExtra(VideoActivity.VIDEO_URL,"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
                intent.putExtra(VideoActivity.VIDEO_TYPE,VideoActivity.TYPE_ONLINE);
                startActivity(intent);
            }
        });


    }
}
