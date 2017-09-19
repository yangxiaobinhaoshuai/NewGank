package com.yangxiaobin.gank.common.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by handsomeyang on 2017/7/11.
 */
public class ContentItemEntity implements Serializable {
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

  private String _id;
  private String createdAt;
  private String desc;
  private String publishedAt;
  private String type;
  private String url;
  private boolean used;
  private List<String> images;
  private String who;
  private String title;    // 类别标题

  private String ganhuo_id;
  private String readablity;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getImages() {
    return images;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }

  public String get_id() {
    return _id;
  }

  public void set_id(String _id) {
    this._id = _id;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
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

  public String getGanhuo_id() {
    return ganhuo_id;
  }

  public void setGanhuo_id(String ganhuo_id) {
    this.ganhuo_id = ganhuo_id;
  }

  public String getReadablity() {
    return readablity;
  }

  public void setReadablity(String readablity) {
    this.readablity = readablity;
  }

  @Override public String toString() {
    return "AndroidBean{"
        + "_id='"
        + _id
        + '\''
        + ", createdAt='"
        + createdAt
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
