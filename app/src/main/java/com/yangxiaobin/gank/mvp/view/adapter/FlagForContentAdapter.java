package com.yangxiaobin.gank.mvp.view.adapter;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by handsomeyang on 2017/9/19.
 */

@StringDef @Retention(RetentionPolicy.SOURCE) public @interface FlagForContentAdapter {
  String COLLECTION = "collectionFragment";
  String CONTENT = "content";
}
