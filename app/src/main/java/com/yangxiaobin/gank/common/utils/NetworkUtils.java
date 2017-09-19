package com.yangxiaobin.gank.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by handsomeyang on 2017/8/19.
 */

public class NetworkUtils {

  private NetworkUtils() {
  }

  /**
   * 判断网络是否可用
   * <p>需添加权限 android.permission.ACCESS_NETWORK_STATE</p>
   */
  public static boolean isAvailable(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = cm.getActiveNetworkInfo();
    return info != null && info.isAvailable();
  }
}

