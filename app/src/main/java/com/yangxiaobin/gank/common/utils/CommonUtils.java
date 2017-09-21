package com.yangxiaobin.gank.common.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by handsomeyang on 2017/7/5.
 */

public class CommonUtils {

  private static Context sContext;
  private static DisplayMetrics mMetrics;
  private static SimpleDateFormat sSdf;

  private CommonUtils() {
  }

  /**
   * call this in your appliction oncreate
   *
   * @param context application context
   */
  public static void init(Context context) {
    sContext = context;
  }

  private static Context getContext() {
    if (sContext == null) {
      throw new RuntimeException("u should call CommonUtils.init first");
    }
    return sContext;
  }

  public static int dp2px(float dp) {
    float density = getContext().getResources().getDisplayMetrics().density;
    return (int) (dp * density + 0.5F);
  }

  public static int px2dp(float px) {
    float density = getContext().getResources().getDisplayMetrics().density;
    return (int) (px / density + 0.5F);
  }

  public static int sp2px(float spValue) {
    final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }

  public static int px2sp(float pxValue) {
    final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
    return (int) (pxValue / fontScale + 0.5f);
  }

  /**
   * 获取屏幕宽度
   *
   * @return screen width
   */
  public static float getScreenWidth() {
    if (mMetrics == null) {
      mMetrics = new DisplayMetrics();
    }
    WindowManager windowManager =
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    windowManager.getDefaultDisplay().getMetrics(mMetrics);
    return mMetrics.widthPixels;
  }

  /**
   * 获取屏幕高度
   *
   * @return screen height
   */
  public static float getScreenHeight() {
    if (mMetrics == null) {
      mMetrics = new DisplayMetrics();
    }
    WindowManager windowManager =
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    windowManager.getDefaultDisplay().getMetrics(mMetrics);
    return mMetrics.heightPixels;
  }

  /**
   * set textview left drawable
   *
   * @param tv target
   * @param drawableRes drawable resId
   */
  public static void setTextViewDrawableLeft(TextView tv, @DrawableRes int drawableRes) {
    Drawable drawable = getContext().getDrawable(drawableRes);
    // 这一步必须要做,否则不会显示.
    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    tv.setCompoundDrawables(drawable, null, null, null);
  }

  /**
   * 把毫秒格式化制定日期字符串
   *
   * @param currentMills 毫秒
   * @param pattern 格式
   * @return 日期字符串
   */
  public static String formatTime(long currentMills, String pattern) {
    if (sSdf == null) {
      sSdf = new SimpleDateFormat(pattern, Locale.getDefault());
    }
    return sSdf.format(currentMills);
  }
}
