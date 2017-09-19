package com.yangxiaobin.gank.common.bean;

import java.util.List;

/**
 * Created by handsomeyang on 2017/7/12.
 */

public class GankDailyTitleEntity {

  private boolean error;
  private List<ResultsBean> results;

  public boolean isError() {
    return error;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public List<ResultsBean> getResults() {
    return results;
  }

  public void setResults(List<ResultsBean> results) {
    this.results = results;
  }

  @Override public String toString() {
    return "GankDailyTitleEntity{" + "error=" + error + ", results=" + results + '}';
  }

  public static class ResultsBean {

    private String _id;
    private String content;
    private String created_at;
    private String publishedAt;
    private String rand_id;
    private String title;
    private String updated_at;

    public String get_id() {
      return _id;
    }

    public void set_id(String _id) {
      this._id = _id;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public String getCreated_at() {
      return created_at;
    }

    public void setCreated_at(String created_at) {
      this.created_at = created_at;
    }

    public String getPublishedAt() {
      return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
      this.publishedAt = publishedAt;
    }

    public String getRand_id() {
      return rand_id;
    }

    public void setRand_id(String rand_id) {
      this.rand_id = rand_id;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getUpdated_at() {
      return updated_at;
    }

    public void setUpdated_at(String updated_at) {
      this.updated_at = updated_at;
    }

    @Override public String toString() {
      return "ResultsBean{"
          + "_id='"
          + _id
          + '\''
          + ", content='"
          + content
          + '\''
          + ", created_at='"
          + created_at
          + '\''
          + ", publishedAt='"
          + publishedAt
          + '\''
          + ", rand_id='"
          + rand_id
          + '\''
          + ", title='"
          + title
          + '\''
          + ", updated_at='"
          + updated_at
          + '\''
          + '}';
    }
  }
}
