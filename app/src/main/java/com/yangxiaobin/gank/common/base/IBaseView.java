package com.yangxiaobin.gank.common.base;

import android.content.Context;

/**
 * Created by handsomeyang on 2017/6/30.
 */

public interface IBaseView {

  Context getViewContext();

  void showToast(String msg);
}
