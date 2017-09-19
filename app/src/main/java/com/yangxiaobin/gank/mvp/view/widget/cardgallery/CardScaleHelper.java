package com.yangxiaobin.gank.mvp.view.widget.cardgallery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.orhanobut.logger.Logger;
import com.yangxiaobin.gank.common.utils.CommonUtils;

/**
 * Created by jameson on 8/30/16.
 */
public class CardScaleHelper {
  private RecyclerView mRecyclerView;

  private float mScale = 0.9f;      // 两边视图scale
  private int mPagePadding = 15;    // 卡片的padding, 卡片间的距离等于2倍的mPagePadding
  private int mShowLeftCardWidth = 15;   // 左边卡片显示大小

  private int mCardWidth;           // 卡片宽度
  private int mOnePageWidth;        // 滑动一页的距离
  private int mCardGalleryWidth;

  private int mCurrentItemPos;
  private int mCurrentItemOffset;

  private CardLinearSnapHelper mLinearSnapHelper = new CardLinearSnapHelper();
  private boolean mLogEnable;

  public void attachToRecyclerView(final RecyclerView mRecyclerView) {
    // 开启log会影响滑动体验, 调试时才开启
    //mLogEnable = true;
    this.mRecyclerView = mRecyclerView;
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          mLinearSnapHelper.mNoNeedToScroll =
              mCurrentItemOffset == 0 || mCurrentItemOffset == getDestItemOffset(
                  mRecyclerView.getAdapter().getItemCount() - 1);
        } else {
          mLinearSnapHelper.mNoNeedToScroll = false;
        }
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // dx>0则表示右滑, dx<0表示左滑, dy<0表示上滑, dy>0表示下滑
        mCurrentItemOffset += dx;
        computeCurrentItemPos();
        LogUtils(String.format("dx=%s, dy=%s, mScrolledX=%s", dx, dy, mCurrentItemOffset));
        onScrolledChangedCallback();
      }
    });

    initWidth();
    mLinearSnapHelper.attachToRecyclerView(mRecyclerView);
  }

  /**
   * 初始化卡片宽度
   */
  private void initWidth() {
    mRecyclerView.post(new Runnable() {
      @Override public void run() {
        mCardGalleryWidth = mRecyclerView.getWidth();
        mCardWidth = mCardGalleryWidth - CommonUtils.dp2px(2 * (mPagePadding + mShowLeftCardWidth));
        mOnePageWidth = mCardWidth;
        mRecyclerView.smoothScrollToPosition(mCurrentItemPos);
        onScrolledChangedCallback();
      }
    });
  }

  public void setCurrentItemPos(int currentItemPos) {
    this.mCurrentItemPos = currentItemPos;
  }

  public int getCurrentItemPos() {
    return mCurrentItemPos;
  }

  private int getDestItemOffset(int destPos) {
    return mOnePageWidth * destPos;
  }

  /**
   * 计算mCurrentItemOffset
   */
  private void computeCurrentItemPos() {
    if (mOnePageWidth <= 0) return;
    boolean pageChanged = false;
    // 滑动超过一页说明已翻页
    if (Math.abs(mCurrentItemOffset - mCurrentItemPos * mOnePageWidth) >= mOnePageWidth) {
      pageChanged = true;
    }
    if (pageChanged) {
      int tempPos = mCurrentItemPos;

      mCurrentItemPos = mCurrentItemOffset / mOnePageWidth;
      LogUtils(
          String.format("=======onCurrentItemPos Changed======= tempPos=%s, mCurrentItemPos=%s",
              tempPos, mCurrentItemPos));
    }
  }

  /**
   * RecyclerView位移事件监听, view大小随位移事件变化
   */
  private void onScrolledChangedCallback() {
    int offset = mCurrentItemOffset - mCurrentItemPos * mOnePageWidth;
    float percent = (float) Math.max(Math.abs(offset) * 1.0 / mOnePageWidth, 0.0001);

    LogUtils(String.format("offset=%s, percent=%s", offset, percent));
    View leftView = null;
    View currentView;
    View rightView = null;
    if (mCurrentItemPos > 0) {
      leftView = mRecyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos - 1);
    }
    currentView = mRecyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos);
    if (mCurrentItemPos < mRecyclerView.getAdapter().getItemCount() - 1) {
      rightView = mRecyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos + 1);
    }

    if (leftView != null) {
      // y = (1 - mScale)x + mScale
      leftView.setScaleY((1 - mScale) * percent + mScale);
    }
    if (currentView != null) {
      // y = (mScale - 1)x + 1
      currentView.setScaleY((mScale - 1) * percent + 1);
    }
    if (rightView != null) {
      // y = (1 - mScale)x + mScale
      rightView.setScaleY((1 - mScale) * percent + mScale);
    }
  }

  public void setScale(float scale) {
    mScale = scale;
  }

  public void setPagePadding(int pagePadding) {
    mPagePadding = pagePadding;
  }

  public void setShowLeftCardWidth(int showLeftCardWidth) {
    mShowLeftCardWidth = showLeftCardWidth;
  }

  /**
   * 如果调试  需要把  mLogEnable 改为true；
   *
   * @param object 日志
   */
  private void LogUtils(String object) {
    if (mLogEnable) {
      Logger.e(object);
    }
  }
}
