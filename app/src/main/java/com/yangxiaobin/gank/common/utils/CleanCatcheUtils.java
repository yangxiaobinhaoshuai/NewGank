package com.yangxiaobin.gank.common.utils;

/*
 *  缓存清理工具类
 *  /storage/emulated/0   sd卡目录
 *
 *   App缓存主要保存在两个地方：
 *  1> /data/data/应用包名/com.cytmxk.test/cache
 *  2> /storage/emulated/0/Android/data/应用包名/cache
 *

 */

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;
import com.orhanobut.logger.Logger;
import java.io.File;

public class CleanCatcheUtils {

  private CleanCatcheUtils() {

  }

  // 清理缓存
  public static boolean clear(Context context) {
    return delFile(context.getCacheDir()) && delFile(context.getExternalCacheDir());
  }

  //递归删除文件
  private static boolean delFile(File file) {
    if (file.isDirectory() && file.exists()) {
      File[] files = file.listFiles();
      for (File f : files) {
        if (f.isDirectory()) {
          delFile(f);
        }
        f.delete();
      }
    }
    return file.delete(); // 删除最外层
  }

  /**
   * 获取App的缓存大小(单位 byte)
   */
  public static String getCacheSize(Context context) {
    long result = 0;
    File dataDataCache = context.getCacheDir();
    if (dataDataCache.exists()) {
      result += getFolderSize(dataDataCache);
    }

    // sd 卡挂载
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
      File SDCache = context.getExternalCacheDir();
      if (null != SDCache && SDCache.exists()) {
        result += getFolderSize(new File(SDCache.getAbsolutePath()));
      }
    }

    return formatSize(context, result);
  }

  // 获取某个文件夹下面所有文件大小
  private static long getFolderSize(File file) {
    long size = 0;
    try {
      File[] fileList = file.listFiles();
      for (File f : fileList) {
        if (f.isDirectory()) {
          size += getFolderSize(f);
        } else {
          size += f.length();
        }
      }
    } catch (Exception e) {
      Logger.e("获取目录大小错误：" + e);
      e.printStackTrace();
    }
    return size;
  }

  //格式化
  private static String formatSize(Context context, long size) {
    return Formatter.formatFileSize(context, size);
  }
}