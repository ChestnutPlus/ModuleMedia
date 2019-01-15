package com.chestnut.media.contract;

import android.content.Context;
import android.util.LongSparseArray;

import com.danikula.videocache.HttpProxyCacheServer;

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
    private LongSparseArray<Object> builderLongSparseArray = new LongSparseArray<>();
    private HttpProxyCacheServer proxy;

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

    public HttpProxyCacheServer getProxy(Context context) {
        return proxy == null ? (proxy = newProxy(context)) : proxy;
    }

    private HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(100*1024*1024)
                .maxCacheFilesCount(10)
                .cacheDirectory(context.getCacheDir())
                .build();
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
    public synchronized Object pop(long key) {
        return builderLongSparseArray.get(key,null);
    }

    public synchronized Object popAndClean(long key) {
        Object builder = builderLongSparseArray.get(key,null);
        builderLongSparseArray.clear();
        return builder;
    }

    /**
     * push入新的 builder
     * @param builder builder
     * @return key
     */
    synchronized long push(Object builder) {
        long sparseKey = System.currentTimeMillis();
        builderLongSparseArray.put(sparseKey, builder);
        return sparseKey;
    }
}
