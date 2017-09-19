package com.yangxiaobin.gank.mvp.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import com.handsome.library.T;
import com.yangxiaobin.Constant;
import com.yangxiaobin.EasyRecyclerView;
import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BasePresenter;
import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.common.bean.SearchHistoryEntity;
import com.yangxiaobin.gank.common.net.ErrorConsumer;
import com.yangxiaobin.gank.common.utils.CommonUtils;
import com.yangxiaobin.gank.common.utils.UserUtils;
import com.yangxiaobin.gank.mvp.contract.SearchContract;
import com.yangxiaobin.gank.mvp.view.adapter.CategoryAdapter;
import com.yangxiaobin.gank.mvp.view.adapter.SearchHistoryAdapter;
import com.yangxiaobin.gank.mvp.view.fragment.WebFragment;
import com.yangxiaobin.kits.base.FragmentSkiper;
import com.yangxiaobin.listener.OnItemClickListener;
import com.yangxiaobin.listener.OnItemLongClickListener;
import com.yangxiaobin.refresh.SwipeTopBottomLayout;
import io.reactivex.functions.Consumer;
import io.realm.RealmResults;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/8/28.
 */

public class SearchPresenter extends BasePresenter
    implements SearchContract.Presenter, View.OnClickListener, SearchView.OnQueryTextListener,
    SwipeTopBottomLayout.OnRefreshListener, SwipeTopBottomLayout.OnLoadMoreListener,
    OnItemLongClickListener, DialogInterface.OnClickListener, OnItemClickListener {

  private SearchContract.View mView;
  private SearchContract.Model mModel;
  private int mLongClickedPos;
  private AlertDialog.Builder mBuilder;                       // 长按收藏
  private List<CategoryEntity.ResultsBean> mTotalResults;
  private CategoryAdapter mAdapter;
  private String mQuery;
  private static final int sCount = 10;
  private int mCurrentPage = 1;
  private boolean isLoadingMore;
  private InputMethodManager mInputMethodManager;
  private PopupWindow mPopupWindow;
  private float mScreenWidth;
  private EasyRecyclerView mRecyclerView;

  @Inject public SearchPresenter(SearchContract.View view, SearchContract.Model model) {
    mView = view;
    mModel = model;
  }

  @RequiresApi(api = Build.VERSION_CODES.M) @Override public void start() {
    mBuilder = new AlertDialog.Builder(mView.getViewContext());
    // 隐藏或者显示软键盘
    mInputMethodManager =
        (InputMethodManager) mView.getViewContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    // 延迟100ms 后执行
    mView.getViewForInputMananger().postDelayed(new Runnable() {
      @Override public void run() {
        mInputMethodManager.showSoftInput(mView.getViewForInputMananger(), 0);
      }
    }, 100);
    initPopupWindow();
  }

  @RequiresApi(api = Build.VERSION_CODES.M) private void initPopupWindow() {
    mPopupWindow = new PopupWindow(mView.getViewContext());
    mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    mScreenWidth = CommonUtils.getScreenWidth();
    mPopupWindow.setWidth(((int) (mScreenWidth * 9 / 10)));
    mPopupWindow.setBackgroundDrawable(
        new ColorDrawable(mView.getViewContext().getColor(R.color.white)));
    mPopupWindow.setElevation(CommonUtils.dp2px(10));
    Slide enterSlide = new Slide();
    enterSlide.setSlideEdge(Gravity.TOP);
    mPopupWindow.setEnterTransition(enterSlide);
    mPopupWindow.setExitTransition(enterSlide);
    mPopupWindow.setOutsideTouchable(true);

    // 软键盘是必须的
    mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
    // 当布局变化时重新计算大小
    //mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    // 软键盘盖住popupwindow
    mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    mPopupWindow.setContentView(getHistoryRecyclerView());
    mView.getViewForInputMananger().postDelayed(new Runnable() {
      @Override public void run() {
        mPopupWindow.showAsDropDown(mView.getSuggestWindwoAchor(), ((int) (mScreenWidth / 10 / 2)),
            0);
      }
    }, 100);
  }

  @NonNull private EasyRecyclerView getHistoryRecyclerView() {
    mRecyclerView = new EasyRecyclerView(mView.getViewContext());
    mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getViewContext()));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(mView.getViewContext(), DividerItemDecoration.VERTICAL));
    mRecyclerView.setOnItemClickListener(this, R.id.layout_item_search_history,
        R.id.imgv_delete_item_search_history);
    // 从db中获取history
    RealmResults<SearchHistoryEntity> historyEntities =
        mView.getRealmHelper().getAllSearchHistory();
    if (historyEntities.size() == 0) {
      return null;
    }
    SearchHistoryAdapter adapter =
        new SearchHistoryAdapter(historyEntities, R.layout.item_search_history_search_fragment);
    mRecyclerView.setAdapter(adapter);
    return mRecyclerView;
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case android.support.v7.appcompat.R.id.search_src_text:
        if (!mPopupWindow.isShowing()) {
          mPopupWindow.showAsDropDown(mView.getSuggestWindwoAchor(),
              ((int) (mScreenWidth / 10 / 2)), 0);
        }
        break;
      default:
        // navigator
        mView.removeSelf();
        break;
    }
  }

  @Override public boolean onQueryTextSubmit(String query) {
    mQuery = query;
    dismissPopupwindow();
    //Logger.e("提交的搜索内容：" + query);
    doNetGetSearchRes(query);
    // save to db
    //mView.getRealmHelper().insertSearchHistory(query, System.currentTimeMillis());
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

  @Override public void onItemLongClick(View view, int pos, MotionEvent motionEvent) {
    // save to db
    mLongClickedPos = pos;
    if (!hasInsertedBefore(mTotalResults.get(pos))) {
      mBuilder.setItems(new String[] { "添加收藏", }, this);
    } else {
      mBuilder.setItems(new String[] { "删除收藏" }, this);
    }
    mBuilder.create().show();
  }

  // 查询是否添加过
  private boolean hasInsertedBefore(ContentItemEntity entity) {
    return mView.getRealmHelper().findOne(entity) != null;
  }

  @Override public void onClick(DialogInterface dialog, int which) {
    if (!UserUtils.hasLogined(mView.getViewContext())) {
      T.info(mView.getViewContext().getString(R.string.please_login_frist));
    } else {
      ContentItemEntity entity = mTotalResults.get(mLongClickedPos);
      if (hasInsertedBefore(entity)) {
        mView.getRealmHelper().delete(entity);
      } else {
        mView.getRealmHelper().insert(entity);
      }
      mAdapter.notifyItemChanged(mLongClickedPos);
    }
  }

  @Override public void onDestroy() {
    // 强制隐藏软键盘
    mInputMethodManager.hideSoftInputFromWindow(
        ((Activity) mView.getViewContext()).getWindow().getDecorView().getWindowToken(), 0);
    dismissPopupwindow();
    mView.getRealmHelper().closeRealm();
  }

  @Override public void onItemClick(View view, int pos, MotionEvent motionEvent) {
    String url = mTotalResults.get(pos).getUrl();
    FragmentSkiper.getInstance()
        .init(((FragmentActivity) mView.getViewContext()))
        .target(new WebFragment().setUrl(url))
        .add(android.R.id.content);
    App.getINSTANCE().getItemUrls().add(url);
    mAdapter.notifyItemChanged(pos);
  }

  // 搜索历史item click 监听
  //@Override public void onSectionClick(View view, int pos, MotionEvent motionEvent) {
  //  Logger.e("onSectinClick");
  //  TextView content = (TextView) view.findViewById(R.id.tv_search_key_word_item_serarch_history);
  //  String historyText = content.getText().toString().trim();
  //  switch (view.getId()) {
  //    case R.id.layout_item_search_history:
  //      // setText as query
  //      mView.setHistoryQeryAndSubmit(historyText);
  //      dismissPopupwindow();
  //      break;
  //    case R.id.imgv_delete_item_search_history:
  //      // 删除历史记录
  //      mView.getRealmHelper().deleteSomeHistory(historyText);
  //      dismissPopupwindow();
  //      break;
  //    default:
  //      break;
  //  }
  //}

  private void dismissPopupwindow() {
    if (mPopupWindow != null && mPopupWindow.isShowing()) {
      mPopupWindow.dismiss();
    }
  }
}
