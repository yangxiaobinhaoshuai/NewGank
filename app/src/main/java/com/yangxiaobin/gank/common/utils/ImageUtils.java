package com.yangxiaobin.gank.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pgyersdk.crash.PgyCrashManager;
import com.yangxiaobin.gank.R;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by handsomeyang on 2017/7/26.
 */

public class ImageUtils {

  private ImageUtils() {

  }

  public static void load(Context context, String url, ImageView imageView) {
    try {
      RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_placeholer_128)
          .priority(Priority.HIGH)
          .dontAnimate()
          .error(R.drawable.ic_error_128)
          .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
      // 自动开启内存缓存   默认混存在磁盘上是转换过大小的图片
      Glide.with(context).load(url).apply(options).into(imageView);
    } catch (IllegalArgumentException ex) {
      Log.wtf("Glide-tag", String.valueOf(imageView.getTag()));
      // pgy 上报 catch 异常
      PgyCrashManager.reportCaughtException(context, ex);
    }
  }

  public static void load(Context context, String url, ImageView imageView, int width, int height) {
    try {
      RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_placeholer_128)
          .dontAnimate()
          .priority(Priority.HIGH)
          .override(width, height)
          .dontAnimate()
          .error(R.drawable.ic_error_128)
          .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
      // 自动开启内存缓存   默认混存在磁盘上是转换过大小的图片
      Glide.with(context).load(url).apply(options).into(imageView);
    } catch (IllegalArgumentException ex) {
      Log.wtf("Glide-tag", String.valueOf(imageView.getTag()));
      // pgy 上报 catch 异常
      PgyCrashManager.reportCaughtException(context, ex);
    }
  }

  public static void loadRound(Context context, String url, ImageView imageView) {
    try {
      RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_placeholer_128)
          .dontAnimate()
          .priority(Priority.HIGH)
          .dontAnimate()
          .error(R.drawable.ic_error_128)
          .centerCrop()
          .transform(new RoundImageTransformation())
          .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
      // 自动开启内存缓存   默认混存在磁盘上是转换过大小的图片
      Glide.with(context).load(url).apply(options).into(imageView);
    } catch (IllegalArgumentException ex) {
      Log.wtf("Glide-tag", String.valueOf(imageView.getTag()));
      // pgy 上报 catch 异常
      PgyCrashManager.reportCaughtException(context, ex);
    }
  }

  /**
   * 保存图片到相册
   * http://blog.csdn.net/xu_fu/article/details/39158747
   * 注意写文件权限
   *
   * @param context context
   * @param targetFile target file
   */
  public static boolean saveImageToGallery(Context context, File targetFile) {
    // 读取target 的file
    try {
      // 输出流  保存图片
      File fileDir = new File(Environment.getExternalStorageDirectory(), "GankIO");
      if (!fileDir.exists()) {
        fileDir.mkdir();
      }
      String fileName = System.currentTimeMillis() + ".jpg";
      File file = new File(fileDir, fileName);
      FileOutputStream fos = new FileOutputStream(file);
      BufferedOutputStream bos = new BufferedOutputStream(fos);

      //输入流  读取文件
      FileInputStream fis = new FileInputStream(targetFile.getAbsoluteFile());
      BufferedInputStream bis = new BufferedInputStream(fis);
      byte[] bs = new byte[512];
      int total;
      while ((total = bis.read(bs)) != -1) {
        fos.write(bs, 0, total);
      }
      bis.close();
      bos.close();
      // 其次把文件插入到系统图库
      MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
          fileName, null);
      // 最后通知图库更新
      context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
          Uri.parse("file://" + Environment.getExternalStorageDirectory())));
      return true;
    } catch (IOException e) {
      // pgy 上报 catch 异常
      PgyCrashManager.reportCaughtException(context, e);
      e.printStackTrace();
      Log.wtf("saveImageToGallery-tag", e);
      return false;
    }
  }
}
