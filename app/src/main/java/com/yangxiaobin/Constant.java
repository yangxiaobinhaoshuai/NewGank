package com.yangxiaobin;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by handsomeyang on 2017/6/27.
 * 全局常量
 */

public final class Constant {
  /**
   * gank io api
   */
  public static final String BASE_URL = "http://gank.io/api/";
  public static final long CONNECT_TIMEOUT = 15000;
  public static final long READ_TIMEOUT = 15000;
  public static final long WRITE_TIMEOUT = 15000;
  public static final int SAFT_QUIT_INTERVAL = 2000;
  public static final String CACHE_FILE_NAME = "gank.cache";
  public static final int MEIZI_COUNT = 5;        //默认取5天数据
  public static final int RECYCLERVIEW_CARD_HELPER_NULL = -1;
  public static final String FINISH_SPLASH = "finish_splash";
  /**
   * SP key
   */
  public static final String KEY_HAS_GOT_MY_HEAD_IMAGE = "key_has_got_my_head_image";

  /**
   * gank item category
   */
  @StringDef @Retention(RetentionPolicy.SOURCE) public @interface Category {
    String ANDROID = "Android";
    String IOS = "iOS";
    String WEB = "前端";
    String SUGGEST = "瞎推荐";
    String MEIZI = "福利";
    String VIDEO = "休息视频";
    String EXTEND = "拓展资源";
    String APP = "App";
    String ALL = "all";
  }

  /**
   * github config
   */
  public static final String GITHUB_SERVER = "https://api.github.com/";
  /**
   * my github account for my head image
   */
  public static final String MY_GITHUB_ACCOUNT = "yangxiaobinhaoshuai";

  // SharePreference key
  public static final String KEY_USER_NAME = "key_user_name";
  public static final String KEY_USER_ID_LOGIN = "key_user_id_login";
}
