package com.yangxiaobin.gank.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.format.Formatter;
import com.orhanobut.logger.Logger;

/**
 * Created by handsomeyang on 2017/8/28.
 */

public class CacheHelper {

  private final LruCache<String, Bitmap> mLruCache;

  public CacheHelper() {
    int maxMemory = (int) (Runtime.getRuntime().maxMemory());
    int cacheSize = maxMemory / 8;
    // 12M 作为缓存
    //Logger.e("作为图片缓存大小：" + cacheSize+"  format:"+Formatter.formatFileSize(context,cacheSize));
    mLruCache = new LruCache<String, Bitmap>(cacheSize) {
      @Override protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
      }
    };
  }

  public LruCache<String, Bitmap> getLruCache() {
    return mLruCache;
  }
}
