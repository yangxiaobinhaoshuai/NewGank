package com.yangxiaobin.gank.common.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by handsomeyang on 2017/8/30.
 */

public class SearchHistoryEntity extends RealmObject {
  @PrimaryKey private long id;
  private String content;
  private long searchTime;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public long getSearchTime() {
    return searchTime;
  }

  public void setSearchTime(long searchTime) {
    this.searchTime = searchTime;
  }

  @Override public String toString() {
    return "SearchHistoryEntity{"
        + "content='"
        + content
        + '\''
        + ", searchTime="
        + searchTime
        + '}';
  }
}
