package com.yangxiaobin.gank.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BasePresenter;
import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.common.bean.SearchHistoryEntity;
import com.yangxiaobin.gank.common.net.ErrorConsumer;
import com.yangxiaobin.gank.mvp.contract.SearchContract;
import com.yangxiaobin.gank.mvp.view.activity.LandscapeVideoActivity;
import com.yangxiaobin.gank.mvp.view.adapter.CategoryAdapter;
import com.yangxiaobin.gank.mvp.view.adapter.SearchHistoryAdapter;
import com.yangxiaobin.gank.mvp.view.fragment.WebFragment;
import com.yxb.base.CommonKey;
import com.yxb.base.utils.ActivitySkipper;
import com.yxb.base.utils.ConvertUtils;
import com.yxb.base.utils.FragmentSkipper;
import com.yxb.base.utils.ScreenUtils;
import com.yxb.easy.EasyRecyclerView;
import com.yxb.easy.adapter.AdapterWrapper;
import com.yxb.easy.listener.OnItemClickListener;
import com.yxb.easy.refresh.SwipeTopBottomLayout;
import io.reactivex.functions.Consumer;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/8/28.
 */

public class SearchPresenter extends BasePresenter
    implements SearchContract.Presenter, View.OnClickListener, SearchView.OnQueryTextListener,
    SwipeTopBottomLayout.OnRefreshListener, SwipeTopBottomLayout.OnLoadMoreListener,
    OnItemClickListener {

  private SearchContract.View mView;
  private SearchContract.Model mModel;
  private List<CategoryEntity.ResultsBean> mTotalResults;
  private CategoryAdapter mAdapter;
  private String mQuery;
  private static final int sCount = 10;
  private int mCurrentPage = 1;
  private boolean isLoadingMore;
  private InputMethodManager mInputMethodManager;
  private PopupWindow mPopupWindow;
  private float mScreenWidth;
  // 搜索历史
  private List<SearchHistoryEntity> mHistoryEntities;
  private AdapterWrapper mAdapterWrapper;

  @Inject public SearchPresenter(SearchContract.View view, SearchContract.Model model) {
    mView = view;
    mModel = model;
  }

  @RequiresApi(api = Build.VERSION_CODES.M) @Override public void start() {
    // 隐藏或者显示软键盘
    mInputMethodManager =
        (InputMethodManager) mView.getViewContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    // 延迟100ms 后执行
    mView.getViewForInputManager().postDelayed(new Runnable() {
      @Override public void run() {
        mInputMethodManager.showSoftInput(mView.getViewForInputManager(), 0);
      }
    }, 100);
    initPopupWindow();
  }

  @RequiresApi(api = Build.VERSION_CODES.M) private void initPopupWindow() {
    mPopupWindow = new PopupWindow(mView.getViewContext());
    mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    mScreenWidth = ScreenUtils.getScreenWidth();
    mPopupWindow.setWidth(((int) (mScreenWidth * 9 / 10)));
    mPopupWindow.setBackgroundDrawable(
        new ColorDrawable(mView.getViewContext().getColor(R.color.white)));
    mPopupWindow.setElevation(ConvertUtils.dp2px(10));
    Slide enterSlide = new Slide();
    enterSlide.setSlideEdge(Gravity.TOP);
    mPopupWindow.setEnterTransition(enterSlide);
    mPopupWindow.setExitTransition(enterSlide);
    mPopupWindow.setOutsideTouchable(true);

    // 软键盘是必须的
    mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
    // 当布局变化时重新计算大小
    // mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    // 软键盘盖住popUpWindow
    mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    // recyclerView setContentView
    mPopupWindow.setContentView(getHistoryRecyclerView());
    // show PopUpWindow
    mView.getViewForInputManager().postDelayed(new Runnable() {
      @Override public void run() {
        if (mHistoryEntities.size() != 0) {
          mPopupWindow.showAsDropDown(mView.getSuggestWindowAnchor(),
              ((int) (mScreenWidth / 10 / 2)), 0);
        }
      }
    }, 100);
  }

  private EasyRecyclerView getHistoryRecyclerView() {
    EasyRecyclerView recyclerView = getPopUpWindowRecyclerView();
    // 从db中获取history
    mHistoryEntities = mView.getRealmHelper().getAllSearchHistory();
    // history adapter
    SearchHistoryAdapter searchHistoryAdapter =
        new SearchHistoryAdapter(mHistoryEntities, R.layout.item_search_history_search_fragment);
    mAdapterWrapper = new AdapterWrapper(searchHistoryAdapter);
    // 添加footer 和 header
    initPopUpWindowHeaderAndFooter();
    recyclerView.setAdapter(mAdapterWrapper);
    return recyclerView;
  }

  @NonNull private EasyRecyclerView getPopUpWindowRecyclerView() {
    EasyRecyclerView recyclerView = new EasyRecyclerView(mView.getViewContext());
    recyclerView.setLayoutManager(new LinearLayoutManager(mView.getViewContext()));
    recyclerView.addItemDecoration(
        new DividerItemDecoration(mView.getViewContext(), DividerItemDecoration.VERTICAL));
    recyclerView.setOnItemClickListener(this, R.id.layout_item_search_history,
        R.id.imgv_delete_item_search_history);
    return recyclerView;
  }

  private void initPopUpWindowHeaderAndFooter() {
    // add header
    LayoutInflater layoutInflater = LayoutInflater.from(mView.getViewContext());
    View popUpWindowHeader =
        layoutInflater.inflate(R.layout.header_search_history, mView.getHeaderAndFooterParent(),
            false);
    // close popUpWindow
    popUpWindowHeader.findViewById(R.id.imgv_close_search_history_list).setOnClickListener(this);
    View popUpWindowFooter =
        layoutInflater.inflate(R.layout.footer_search_history, mView.getHeaderAndFooterParent(),
            false);
    // delete all history
    popUpWindowFooter.findViewById(R.id.imgv_delete_all_search_history_list)
        .setOnClickListener(this);
    mAdapterWrapper.addHeaders(popUpWindowHeader);
    mAdapterWrapper.addFooters(popUpWindowFooter);
  }

  // 向db插入搜索历史
  @Override public boolean onQueryTextSubmit(String query) {
    mQuery = query;
    dismissPopUpWindow();
    //Logger.e("提交的搜索内容：" + query);
    doNetGetSearchRes(query);
    // save to db
    mView.getRealmHelper().insertSearchHistory(query, System.currentTimeMillis());
    return false;
  }

  private void doNetGetSearchRes(String query) {
    mModel.getSearchRes(query, Constant.Category.ALL, sCount, mCurrentPage)
        .subscribe(new Consumer<CategoryEntity>() {
          @Override public void accept(CategoryEntity searchResultEntity) throws Exception {
            if (isLoadingMore) {
              notifyDataWhenLoadingMore(searchResultEntity);
            } else {
              setAdapterFirstLoaded(searchResultEntity);
            }
            mView.stopRefreshAndLoadMore();
          }
        }, new ErrorConsumer());
  }

  private void notifyDataWhenLoadingMore(CategoryEntity searchResultEntity) {
    if (searchResultEntity.getResults().size() != 0) {
      mTotalResults.addAll(searchResultEntity.getResults());
      //Logger.e("加载更多后的集合" + mTotalResults.size());
      mAdapter.notifyItemRangeInserted(
          mTotalResults.size() - searchResultEntity.getResults().size(), mTotalResults.size());
      mView.recyclerViewMoveToPos(mTotalResults.size() - searchResultEntity.getResults().size());
    } else {
      mView.showToast("没有更多了哦");
    }
    isLoadingMore = false;
  }

  private void setAdapterFirstLoaded(CategoryEntity searchResultEntity) {
    // first loaded
    mTotalResults = searchResultEntity.getResults();
    if (mTotalResults.size() == 0) {
      mView.showToast("没有搜索到结果哦，换个关键词试试");
    } else {
      mAdapter = new CategoryAdapter(mTotalResults);
      mView.setRecyclerViewAdapter(mAdapter);
    }
  }

  @Override public boolean onQueryTextChange(String newText) {
    //Logger.e("搜索的内容改变为：" + newText);
    return false;
  }

  @Override public void onRefresh() {
    mCurrentPage = 1;
    doNetGetSearchRes(mQuery);
  }

  @Override public void onLoadMore() {
    isLoadingMore = true;
    mCurrentPage++;
    doNetGetSearchRes(mQuery);
  }

  @Override public void onDestroy() {
    // 强制隐藏软键盘
    mInputMethodManager.hideSoftInputFromWindow(
        ((Activity) mView.getViewContext()).getWindow().getDecorView().getWindowToken(), 0);
    dismissPopUpWindow();
    mView.getRealmHelper().closeRealm();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      // edt 收到点击就会弹窗
      case android.support.v7.appcompat.R.id.search_src_text:
        if (!mPopupWindow.isShowing()) {
          mAdapterWrapper.notifyDataSetChanged();
          if (mHistoryEntities.size() != 0) {
            if (mHistoryEntities.size() > 10) {
              mHistoryEntities = mHistoryEntities.subList(0, 10);
            }
            mPopupWindow.showAsDropDown(mView.getSuggestWindowAnchor(),
                ((int) (mScreenWidth / 10 / 2)), 0);
          }
        }
        break;
      case R.id.imgv_close_search_history_list:
        // 关闭pupUpWindow
        dismissPopUpWindow();
        break;
      case R.id.imgv_delete_all_search_history_list:
        // 删除所有历史记录
        mView.getRealmHelper().deleteAllHistory();
        dismissPopUpWindow();
        break;
      default:
        // navigator
        mView.removeSelf();
        break;
    }
  }

  // 搜索历史列表的监听   注意recyclerView如果带了header的话是会影响position的
  @Override public void onItemClick(View view, int pos, MotionEvent motionEvent) {
    switch (view.getId()) {
      case R.id.layout_item_search_history:
        mView.setHistoryQueryAndSubmit(mHistoryEntities.get(pos - 1).getContent());
        dismissPopUpWindow();
        break;
      case R.id.imgv_delete_item_search_history:
        // 集合删除  db 删除 , realm 实时映射，不需要集合删除
        //Logger.e("历史记录：" + mHistoryEntities.size() + "    pos:" + pos);
        mView.getRealmHelper().deleteSomeHistory(mHistoryEntities.get(pos - 1).getContent());
        if (mView.getRealmHelper().getAllSearchHistory().size() == 0) {
          dismissPopUpWindow();
          return;
        }
        mAdapterWrapper.notifyItemRemoved(pos);
        break;
      case R.id.layout_item_content_fragment:
        CategoryEntity.ResultsBean entity = mTotalResults.get(pos);
        String url = entity.getUrl();
        if (Constant.Category.VIDEO.equals(entity.getType())) {
          startVideoActivity(entity);
        } else {
          FragmentSkipper.getInstance()
              .init(mView.getViewContext())
              .target(new WebFragment().setUrl(url))
              .add(android.R.id.content);
        }
        App.getINSTANCE().getItemUrls().add(url);
        mAdapter.notifyItemChanged(pos);
        break;
      default:
        break;
    }
  }

  // 跳转横屏activity播放
  private void startVideoActivity(ContentItemEntity entity) {
    ActivitySkipper.getInstance()
        .init(mView.getViewContext())
        .putExtras(CommonKey.STR1, entity.getUrl())
        .putExtras(CommonKey.STR2, entity.getDesc())
        .skip(LandscapeVideoActivity.class);
  }

  private void dismissPopUpWindow() {
    if (mPopupWindow != null && mPopupWindow.isShowing()) {
      mPopupWindow.dismiss();
    }
  }
}
