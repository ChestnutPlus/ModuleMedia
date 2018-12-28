package com.chestnut.media.contract;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2018/12/25 16:21
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public interface MusicContract {
    interface P {
        void setBuilder(MusicBuilder builder);
        void start();
        void stop();
        void pause();
        int getCurrentSecond();
        int getTotalSecond();
        boolean isPlaying();
        void seekToSecond(int seconds);
        void release();
    }
    interface V {

    }
}
