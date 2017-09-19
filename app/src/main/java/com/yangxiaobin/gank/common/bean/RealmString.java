package com.yangxiaobin.gank.common.bean;

import io.realm.RealmObject;

/**
 * Created by handsomeyang on 2017/8/16.
 * 当RealmList 代替String 因为要求泛型必须直接继承RealmObject
 */

public class RealmString extends RealmObject {
  private String string;

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }
}
