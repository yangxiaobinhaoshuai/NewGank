package com.yangxiaobin.gank.common.bean;

import io.realm.RealmObject;

/**
 * Created by handsomeyang on 2017/8/30.
 */

public class SearchHistoryEntity extends RealmObject {
  private String content;
  private long searchTime;

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
