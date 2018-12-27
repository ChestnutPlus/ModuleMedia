package com.chestnut.media.contract;

import android.app.Activity;
import android.content.Intent;
import android.util.LongSparseArray;

import com.chestnut.media.v.activity.VideoActivity;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2018/12/25 15:57
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class MediaManager {

    public static String Key_Builder = "Key_Builder";

    private LongSparseArray<Builder> builderLongSparseArray = new LongSparseArray<>();

    /*单例*/
    private static volatile MediaManager defaultInstance;
    public static MediaManager getInstance() {
        MediaManager xFontUtils = defaultInstance;
        if (defaultInstance == null) {
            synchronized (MediaManager.class) {
                xFontUtils = defaultInstance;
                if (defaultInstance == null) {
                    xFontUtils = new MediaManager();
                    defaultInstance = xFontUtils;
                }
            }
        }
        return xFontUtils;
    }
    private MediaManager(){}

    /**
     * 执行跳转
     * @param builder builder
     */
    void execute(Builder builder){
        long sparseKey = System.currentTimeMillis();
        Intent intent = null;
        if (builder instanceof VideoBuilder) {
            builderLongSparseArray.put(sparseKey, builder);
            intent = new Intent(builder.context, VideoActivity.class);
        }
        else if (builder instanceof MusicBuilder) {
            builderLongSparseArray.put(sparseKey, builder);
        }
        if (intent!=null) {
            if (!(builder.context instanceof Activity))
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Key_Builder, sparseKey);
            builder.context.startActivity(intent);
        }
    }

    /**
     * 得到建造者
     * @return 建造者
     */
    public static VideoBuilder videoBuilder() {
        return new VideoBuilder();
    }

    public static MusicBuilder musicBuilder() {
        return new MusicBuilder();
    }

    /**
     * pop出 builder
     * @param key key
     * @return builder
     */
    public synchronized Builder pop(long key) {
        return builderLongSparseArray.get(key,null);
    }

    public synchronized Builder popAndClean(long key) {
        Builder builder = builderLongSparseArray.get(key,null);
        builderLongSparseArray.clear();
        return builder;
    }
}
