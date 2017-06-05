package test.com.chestnut.Media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chestnut.Common.utils.LogUtils;
import com.chestnut.Media.Video.VideoActivity;

public class MainActivity extends AppCompatActivity {

    private boolean OpenLog = true;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //home键监听
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homePressReceiver, homeFilter);

        //电源键监听
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mBatInfoReceiver, filter);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                intent.putExtra(VideoActivity.VIDEO_TITLE,"南山南");
                intent.putExtra(VideoActivity.VIDEO_URL, Environment.getExternalStorageDirectory().getPath() + "/1.mp4");
                intent.putExtra(VideoActivity.VIDEO_TYPE,VideoActivity.TYPE_LOCAL);
//                intent.putExtra(VideoActivity.VIDEO_URL,"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//                intent.putExtra(VideoActivity.VIDEO_TYPE,VideoActivity.TYPE_ONLINE);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.w(OpenLog,TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.w(OpenLog,TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.w(OpenLog,TAG,"onDestroy");
    }

    private final BroadcastReceiver homePressReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if(reason != null&& reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    LogUtils.w(OpenLog,TAG,"home键监听");
                    System.out.println("home键监听");
                }
            }
        }
    };

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if(Intent.ACTION_SCREEN_OFF.equals(action)) {
                LogUtils.w(OpenLog,TAG,"电源键监听");
                System.out.println("电源键监听");
            }
        }
    };
}
