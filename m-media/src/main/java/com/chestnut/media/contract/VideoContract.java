package com.chestnut.media.contract;

import android.content.Context;

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
    interface P extends VideoControl{
        void onCreate(Context context);
        void setBuilder(VideoBuilder builder);
        void onPause();
        void onResume();
        void onDestroy();
        void playOrPause();
        void onViewDownTouch();
    }
    interface V extends VideoControl{
        void showNotFoundVideo();
        //是否显示播放的按钮，则是在暂停的时候显示
        void setIsShowPlayIcon(boolean isShow);
        void setTimeTotal(int seconds);
        void setBufferingUpdatePosition(int seconds);
        void setTimeCurrent(int seconds);
        void playCompleteCleanFlags();
        void setTitle(String title);
        void showControlView();
        void hideControlView();
    }
    interface VideoControl {
        void seekToSecond(int seconds);
        void pauseVideo();
        void resumeVideo();
        void stopVideo();
        void playVideo(String url);
    }
}
