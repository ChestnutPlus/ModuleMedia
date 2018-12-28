package test.com.chestnut.Media;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.chestnut.media.contract.MediaManager;
import com.chestnut.media.contract.MusicBuilder;
import com.chestnut.media.p.MusicPresenter;

public class MusicTestActivity extends AppCompatActivity {

    MusicPresenter musicPresenter;
    private MusicBuilder.Callback callback = new MusicBuilder.Callback() {
        @Override
        public void onBuffering(int percent) {
            Log.i("MusicTestActivity","onBuffering:"+percent);
        }

        @Override
        public void onSeekTo(int seconds) {
            Log.i("MusicTestActivity","onSeekTo:"+seconds);
        }

        @Override
        public void onStart() {
            Log.i("MusicTestActivity","onStart");
        }

        @Override
        public void onStop() {
            Log.i("MusicTestActivity","onStop");
        }

        @Override
        public void onCompletion() {
            Log.i("MusicTestActivity","onCompletion");
        }

        @Override
        public void onPause() {
            Log.i("MusicTestActivity","onPause");
        }

        @Override
        public void onErr(int code, String msg) {
            Log.i("MusicTestActivity","code:"+code+","+msg);
        }

        @Override
        public void onProgressChange(int currentSecond, int totalSecond) {
            Log.i("MusicTestActivity","onProgressChange:"+currentSecond+","+totalSecond);
        }
    };

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_test);
        musicPresenter = new MusicPresenter(this);

        findViewById(R.id.btn_load).setOnClickListener(v -> {
            String[] s = {
                    "/sdcard/adb.mp3",
                    "/sdcard/a.mp3",
                    "/sdcard/b.mp3",
                    "http://h5.honeybot.cn/c.mp3",
            };
            musicPresenter.setBuilder(MediaManager.musicBuilder()
                    .setAutoPlay(true)
                    .setCallback(callback)
                    .setUrl(s[index]));
            index++;
            if (index>=s.length)
                index = 0;
        });

        findViewById(R.id.btn_play).setOnClickListener(v -> {
            musicPresenter.start();
        });

        findViewById(R.id.btn_pause).setOnClickListener(v -> {
            musicPresenter.pause();
        });

        findViewById(R.id.btn_stop).setOnClickListener(v -> {
            musicPresenter.stop();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPresenter.release();
    }
}
