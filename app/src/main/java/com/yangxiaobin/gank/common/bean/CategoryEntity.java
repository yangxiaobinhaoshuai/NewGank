package com.yangxiaobin.gank.common.bean;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by handsomeyang on 2017/7/25.
 */

public class CategoryEntity {

  /**
   * error : false
   * results : [{"_id":"5973f95d421aa90c9203d3eb","createdAt":"2017-07-23T09:18:21.828Z","desc":"Android
   * 层叠卡片控件，仿\"探探app\"","images":["http://img.gank.io/36e2eb02-be78-4c97-950b-28020d1e0356"],"publishedAt":"2017-07-24T12:13:11.280Z","source":"chrome","type":"Android","url":"https://github.com/fashare2015/StackLayout","used":true,"who":"Jason"},{"_id":"59746449421aa90ca209c4c0","createdAt":"2017-07-23T16:54:33.986Z","desc":"Java
   * 时间与日期处理","publishedAt":"2017-07-24T12:13:11.280Z","source":"chrome","type":"Android","url":"https://zhuanlan.zhihu.com/p/28055974","used":true,"who":"王下邀月熊"},{"_id":"5975480b421aa97de5c7c999","createdAt":"2017-07-24T09:06:19.690Z","desc":"RxJava2.X
   * 源码解析（一）： 探索RxJava2分发订阅流程","publishedAt":"2017-07-24T12:13:11.280Z","source":"web","type":"Android","url":"http://url.cn/4CM8ka6","used":true,"who":"陈宇明"},{"_id":"59754c7f421aa97de5c7c99b","createdAt":"2017-07-24T09:25:19.617Z","desc":"强大和智能的RefreshLayout，支持越界回弹，集成了几十种炫酷的Header和
   * Footer","publishedAt":"2017-07-24T12:13:11.280Z","source":"web","type":"Android","url":"https://segmentfault.com/a/1190000010066071","used":true,"who":"树朾"},{"_id":"5975717d421aa90ca3bb6b60","createdAt":"2017-07-24T12:03:09.539Z","desc":"Android
   * 简洁优雅的文件选择器。","images":["http://img.gank.io/b3771674-25a1-478a-b665-f0f0960f80a4"],"publishedAt":"2017-07-24T12:13:11.280Z","source":"chrome","type":"Android","url":"https://github.com/FirzenYogesh/FileListerDialog","used":true,"who":"代码家"},{"_id":"5964c8ff421aa90ca3bb6ae1","createdAt":"2017-07-11T20:47:59.353Z","desc":"理解与设计自适应图标
   * \u2014\u2014 自适应图标入门指南","publishedAt":"2017-07-21T12:39:43.370Z","source":"chrome","type":"Android","url":"https://zhuanlan.zhihu.com/p/27814686","used":true,"who":"galois"},{"_id":"5969a267421aa90ca209c46a","createdAt":"2017-07-15T13:04:39.224Z","desc":"Android源码完全解析\u2014\u2014View的Measure过程","publishedAt":"2017-07-21T12:39:43.370Z","source":"web","type":"Android","url":"http://www.jianshu.com/p/4a68f9dc8f7c","used":true,"who":null},{"_id":"5971719e421aa97de5c7c97d","createdAt":"2017-07-21T11:14:38.609Z","desc":"一款非常漂亮的
   * Material Design 风格的音乐播放器！超棒！","images":["http://img.gank.io/9f05efe7-3196-4de4-af65-24e0a919a584"],"publishedAt":"2017-07-21T12:39:43.370Z","source":"chrome","type":"Android","url":"https://github.com/aliumujib/Orin","used":true,"who":"代码家"},{"_id":"59701bf6421aa90c9203d3c9","createdAt":"2017-07-20T10:56:54.503Z","desc":"自定义View之渐变圆环进度条","publishedAt":"2017-07-20T15:11:16.10Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s?__biz=MzIwMzYwMTk1NA==&mid=2247485843&idx=1&sn=d5de05fc0240be0527de8d69b1616c6f&chksm=96cda8dea1ba21c8ba3c620acc07c2ef1e2afc619928587ac8201958af1eb4d3568f6516e4ac#rd","used":true,"who":"陈宇明"},{"_id":"5970415f421aa90ca3bb6b40","createdAt":"2017-07-20T13:36:31.736Z","desc":"又一个漂亮的
   * Android 日历组件。","images":["http://img.gank.io/20ad8aae-6740-4695-ad14-12080649690b"],"publishedAt":"2017-07-20T15:11:16.10Z","source":"chrome","type":"Android","url":"https://github.com/MagicMashRoom/SuperCalendar","used":true,"who":"代码家"}]
   */
  @SerializedName("count") private int itemCount;
  private boolean error;
  private List<ResultsBean> results;

  public int getItemCount() {
    return itemCount;
  }

  public void setItemCount(int itemCount) {
    this.itemCount = itemCount;
  }

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
    return "CategoryEntity{" + "error=" + error + ", results=" + results + '}';
  }

  public static class ResultsBean extends ContentItemEntity {

  }
}
