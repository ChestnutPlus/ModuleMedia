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
public interface VideoContract {
    interface V extends VideoControl{

    }
    interface VideoControl {
        void seekToSecond(int seconds);
        int getDurationSecond();
        int getCurrentSecond();
        void pauseVideo();
        void resumeVideo();
        void stopVideo();
        void playVideo();
        void release();
    }
}
