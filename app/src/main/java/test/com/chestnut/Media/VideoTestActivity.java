package test.com.chestnut.Media;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chestnut.media.contract.MediaManager;
import com.chestnut.media.contract.VideoBuilder;
import com.chestnut.media.v.fragment.VideoFragment;

public class VideoTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vide_test);

        VideoBuilder videoBuilder = MediaManager.videoBuilder()
                .setAutoPlay(false)
                .setTitle("Yellow")
                .setLoop(true)
                .setUrl("http://1258339144.vod2.myqcloud.com/1b77e3cavodcq1258339144/f3ae580c5285890783708931103/4ayzIpIJ77gA.mp4")
                .setCacheAble(true)
                .buildNoView(this);

        VideoFragment fragment = (VideoFragment) getSupportFragmentManager().findFragmentById(com.chestnut.media.R.id.frame_layout);
        fragment.setVideoBuilder(videoBuilder);

        findViewById(R.id.btn_stop).setOnClickListener(v -> {
            fragment.stopVideo();
        });

        findViewById(R.id.btn_play).setOnClickListener(v -> {
            fragment.playVideo();
        });

        findViewById(R.id.btn_pause).setOnClickListener(v -> {
            fragment.pauseVideo();
        });
    }
}
