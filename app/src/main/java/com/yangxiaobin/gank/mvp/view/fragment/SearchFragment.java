package com.yangxiaobin.gank.mvp.view.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import com.handsome.library.T;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BaseFragment;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.mvp.contract.SearchContract;
import com.yangxiaobin.gank.mvp.presenter.SearchPresenter;
import com.yangxiaobin.gank.mvp.view.adapter.CategoryAdapter;
import com.yxb.easy.EasyRecyclerView;
import com.yxb.easy.refresh.SwipeTopBottomLayout;
import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends BaseFragment implements SearchContract.View {

  @BindView(R.id.toolbar_search_framgent) Toolbar mToolbar;
  @BindView(R.id.recyclerview_search_fragment) EasyRecyclerView mRecyclerView;
  @Inject SearchPresenter mPresenter;
  @Inject RealmHelper mRealmHelper;
  private SwipeTopBottomLayout mSwipeTopBottomLayout;
  private SearchView.SearchAutoComplete mSearchTextView;
  private SearchView mSearchView;

  @Override protected int getLayoutResId() {
    return R.layout.fragment_search;
  }

  @RequiresApi(api = Build.VERSION_CODES.M) @Override protected void initialize(Bundle bundle) {
    super.initialize(bundle);
    initToolbar();
    initRecyclerView();
    mPresenter.start();
  }

  private void initRecyclerView() {
    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    mRecyclerView.setNeedRefresh(true);
    mRecyclerView.setAntiShake(1000);
    mRecyclerView.setOnItemClickListener(mPresenter);
    mSwipeTopBottomLayout = mRecyclerView.getSwipeTopBottomLayout();
    mSwipeTopBottomLayout.setOnRefreshListener(mPresenter);
    mSwipeTopBottomLayout.setOnLoadMoreListener(mPresenter);
  }

  private void initToolbar() {
    mToolbar.setNavigationIcon(R.drawable.ic_left_arraw_128);
    mToolbar.setNavigationOnClickListener(mPresenter);
    mToolbar.inflateMenu(R.menu.menu_toolbar_search_fragment);
    mToolbar.setTitle("搜索");
    mSearchView = (SearchView) mToolbar.findViewById(R.id.search_view_menu_item_search_fragment);
    // if false searchView always visible, bug looks ugly
    mSearchView.setIconifiedByDefault(false);
    // 修改search view textColor 为白色
    mSearchTextView = (SearchView.SearchAutoComplete) mSearchView.findViewById(
        android.support.v7.appcompat.R.id.search_src_text);
    mSearchView.setSubmitButtonEnabled(true);
    mSearchTextView.setTextColor(getResources().getColor(R.color.white));
    mSearchTextView.setHintTextColor(getResources().getColor(R.color.gray));
    mSearchView.setQueryHint("搜索福利试试");
    mSearchView.setOnQueryTextListener(mPresenter);
    mSearchTextView.setOnClickListener(mPresenter);
  }

  @Override public Context getViewContext() {
    return mContext;
  }

  @Override public void showToast(String msg) {
    T.info(msg);
  }

  @Override public void removeSelf() {
    getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
  }

  @Override public void setRecyclerViewAdapter(CategoryAdapter adapter) {
    mRecyclerView.setAdapter(adapter);
  }

  @Override public RealmHelper getRealmHelper() {
    return mRealmHelper;
  }

  @Override public void stopRefreshAndLoadMore() {
    mSwipeTopBottomLayout.setRefreshing(false);
    mSwipeTopBottomLayout.setLoadingMore(false);
  }

  @Override public void recyclerViewMoveToPos(int pos) {
    mRecyclerView.smoothScrollToPosition(pos);
  }

  @Override public SearchView.SearchAutoComplete getViewForInputManager() {
    return mSearchTextView;
  }

  @Override public Toolbar getSuggestWindowAnchor() {
    return mToolbar;
  }

  @Override public void setHistoryQueryAndSubmit(String content) {
    mSearchView.setQuery(content, true);
  }

  @Override public EasyRecyclerView getHeaderAndFooterParent() {
    return mRecyclerView;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mPresenter.onDestroy();
  }
}
