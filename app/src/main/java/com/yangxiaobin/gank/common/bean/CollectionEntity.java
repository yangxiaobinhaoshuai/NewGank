package com.yangxiaobin.gank.common.bean;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by handsomeyang on 2017/8/16.
 */

public class CollectionEntity extends RealmObject {
  /**
   * _id : 56cc6d23421aa95caa707a69
   * createdAt : 2015-08-06T07:15:52.65Z
   * desc : 类似Link Bubble的悬浮式操作设计
   * publishedAt : 2015-08-07T03:57:48.45Z
   * type : Android
   * url : https://github.com/recruit-lifestyle/FloatingView
   * used : true
   * who : mthli
   */

  private String desc;
  private String publishedAt;
  private String type;
  private String url;
  private boolean used;
  private RealmList<RealmString> images;
  private String who;
  private String title;    // 类别标题
  private String userId;   // 标识哪个用户

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public RealmList<RealmString> getImages() {
    return images;
  }

  public void setImages(RealmList<RealmString> images) {
    this.images = images;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(String publishedAt) {
    this.publishedAt = publishedAt;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isUsed() {
    return used;
  }

  public void setUsed(boolean used) {
    this.used = used;
  }

  public String getWho() {
    return who;
  }

  public void setWho(String who) {
    this.who = who;
  }

  @Override public String toString() {
    return "AndroidBean{"
        + '\''
        + ", desc='"
        + desc
        + '\''
        + ", publishedAt='"
        + publishedAt
        + '\''
        + ", type='"
        + type
        + '\''
        + ", url='"
        + url
        + '\''
        + ", used="
        + used
        + ", who='"
        + who
        + '\''
        + '}';
  }
}
