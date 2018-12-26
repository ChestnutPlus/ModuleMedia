package com.chestnut.media.v.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chestnut.media.R;

public class VideoActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.media_activity_video);
    }
}
