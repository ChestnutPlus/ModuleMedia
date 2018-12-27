package com.chestnut.media.p;

import com.chestnut.media.contract.MusicBuilder;
import com.chestnut.media.contract.MusicContract;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLOnPreparedListener;

import java.io.IOException;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2018/12/27 22:53
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */

public class MusicPresenter implements MusicContract.P{

    private PLMediaPlayer plMediaPlayer;

    public MusicPresenter() {

    }

    @Override
    public void setBuilder(MusicBuilder builder) {
        if (plMediaPlayer==null)
            plMediaPlayer = new PLMediaPlayer(builder.context.getApplicationContext());
        try {
            plMediaPlayer.setDataSource(builder.url);
            plMediaPlayer.setOnPreparedListener(new PLOnPreparedListener() {
                @Override
                public void onPrepared(int i) {
                    plMediaPlayer.start();
                }
            });
            plMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
