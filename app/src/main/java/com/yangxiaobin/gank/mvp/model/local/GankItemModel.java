package com.yangxiaobin.gank.mvp.model.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by handsomeyang on 2017/7/27.
 */

public class GankItemModel extends RealmObject {

  @PrimaryKey private long id;         //   主键
  private String url;                  //   网页地址
  private String title;                //   标题
  private String date;                 //   日期
  private String imageUrl1;            //   图片1
  private String imageUrl2;            //   图片2
  private String who;                  //   谁审核
  private boolean isColleced;          //   是否收藏

  public boolean isColleced() {
    return isColleced;
  }

  public void setColleced(boolean colleced) {
    isColleced = colleced;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getImageUrl1() {
    return imageUrl1;
  }

  public void setImageUrl1(String imageUrl1) {
    this.imageUrl1 = imageUrl1;
  }

  public String getImageUrl2() {
    return imageUrl2;
  }

  public void setImageUrl2(String imageUrl2) {
    this.imageUrl2 = imageUrl2;
  }

  public String getWho() {
    return who;
  }

  public void setWho(String who) {
    this.who = who;
  }
}
