package com.yangxiaobin.gank.mvp.view.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.logger.Logger;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.common.bean.GankDailyDataEntity;
import com.yangxiaobin.gank.common.glide.GlideApp;
import com.yangxiaobin.gank.common.net.ErrorConsumer;
import com.yangxiaobin.gank.common.utils.CacheHelper;
import com.yangxiaobin.gank.mvp.presenter.MainPresenter;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Publisher;

/**
 * Created by handsomeyang on 2017/7/20.
 */

public class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
  //用来标记是否正在向最后一个滑动
  private MainPresenter mPresenter;
  private CacheHelper mCacheHelper;
  private BitmapFactory.Options mOptions;

  public RecyclerViewOnScrollListener(MainPresenter presenter) {
    mPresenter = presenter;
    mCacheHelper = new CacheHelper();
  }

  @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
    super.onScrollStateChanged(recyclerView, newState);
    Context context = recyclerView.getContext();

    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    switch (newState) {
      case RecyclerView.SCROLL_STATE_IDLE:
        // glide 4.0 针对RecyclerView优化
        GlideApp.with(context).resumeRequests();
        // 当不滚动时
        // 渲染背景
        notifyBackgroundChange();
        //获取最后一个完全显示的ItemPosition
        int totalItemCount = linearLayoutManager.getItemCount();
        int currentPos = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        //Logger.e("当前item：" + currentPos);
        if (currentPos >= 0 && mPresenter.getTotalEntities() != null) {
          // setToolbar title
          if (currentPos != totalItemCount - 1) {
            // 防止刷新的时候crash
            GankDailyDataEntity entity = mPresenter.getTotalEntities().get(currentPos);
            // 获取日期
            String todayDate = entity.getResults().get福利().get(0).getPublishedAt().split("T")[0];
            // 获取制定日期title
            String todayTitle = entity.getTitle();
            // 设置toolbar title 第一次加载不会执行，因为没有滑动
            mPresenter.getView().setToolbarTitle(todayDate + "  " + todayTitle);
          }
        }
        break;
      case RecyclerView.SCROLL_STATE_DRAGGING:
        GlideApp.with(context).resumeRequests();
        break;
      case RecyclerView.SCROLL_STATE_SETTLING:
        GlideApp.with(context).pauseRequests();
        break;
      default:
        break;
    }
  }

  /**
   * 使背景模糊
   */
  public void notifyBackgroundChange() {
    // cardHelper 为null return
    int currentItemPos = mPresenter.getView().getCurrentItemPos();
    if (currentItemPos == Constant.RECYCLERVIEW_CARD_HELPER_NULL) {
      return;
    }
    // 获取当前展示的图片url
    List<GankDailyDataEntity> totalEntities = mPresenter.getTotalEntities();
    if (totalEntities == null) {
      Logger.e("无法渲染背景");
      return;
    }
    GankDailyDataEntity gankDailyDataEntity;
    try {
      gankDailyDataEntity = totalEntities.get(currentItemPos);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    final String url = gankDailyDataEntity.getResults().get福利().get(0).getUrl();
    Bitmap bitmap = mCacheHelper.getLruCache().get(url);
    if (bitmap != null) {
      //Logger.e("从lru中获取");
      mPresenter.getView().startSwitchBgAnim(bitmap);
    } else {
      // 没有缓存
      Flowable.just(url)
          .subscribeOn(Schedulers.newThread())
          .flatMap(new Function<String, Publisher<File>>() {
            @Override public Publisher<File> apply(String s) throws Exception {
              return Flowable.just(Glide.with(mPresenter.getView().getViewContext())
                  .load(url)
                  .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                  .get());
            }
          })
          .timeout(500, TimeUnit.SECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Consumer<File>() {
            @Override public void accept(File file) throws Exception {
              if (mOptions == null) {
                mOptions = new BitmapFactory.Options();
                mOptions.inPreferredConfig = Bitmap.Config.ARGB_4444;
              }
              Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), mOptions);
              //int byteCount = bitmap.getByteCount();
              //5.56M   argb 444为2.78 MB   rgb_565 2.78 MB
              //Logger.e(
              //    "bitmap size:" + Formatter.formatFileSize(mPresenter.getView().getViewContext(),
              //        byteCount));
              mCacheHelper.getLruCache().put(url, bitmap);
              mPresenter.getView().startSwitchBgAnim(bitmap);
            }
          }, new ErrorConsumer());
    }
  }
}
