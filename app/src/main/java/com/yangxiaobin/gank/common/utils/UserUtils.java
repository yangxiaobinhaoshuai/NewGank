package com.yangxiaobin.gank.common.utils;

import android.content.Context;
import android.text.TextUtils;
import com.yangxiaobin.Constant;

/**
 * Created by handsomeyang on 2017/8/19.
 */

public class UserUtils {
  private UserUtils() {

  }

  /**
   * @return true logined ,false not logined
   */
  public static boolean hasLogined(Context context) {
    return !TextUtils.isEmpty(((String) SPUtils.get(context, Constant.KEY_USER_ID_LOGIN, "")));
  }
}
