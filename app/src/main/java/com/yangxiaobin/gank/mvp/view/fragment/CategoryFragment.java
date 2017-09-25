package com.yangxiaobin.gank.mvp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BaseFragment;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.mvp.contract.CategoryContract;
import com.yangxiaobin.gank.mvp.presenter.CategoryPresenter;
import com.yangxiaobin.gank.mvp.view.adapter.CategoryAdapter;
import com.yxb.easy.EasyRecyclerView;
import com.yxb.easy.refresh.SwipeTopBottomLayout;
import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends BaseFragment implements CategoryContract.View {

  @Inject RealmHelper mRealmHelper;
  @Inject CategoryPresenter mPresenter;
  @BindView(R.id.toolbar_single_cateogory_fragment) Toolbar mToolbar;
  @BindView(R.id.recyclerview_single_category_fragment) EasyRecyclerView mRecyclerView;
  private SwipeTopBottomLayout mSwipeTopBottomLayout;

  @Override protected int getLayoutResId() {
    return R.layout.fragment_category;
  }

  @Override protected void initialize(Bundle bundle) {
    super.initialize(bundle);
    mPresenter.start();
    // toolbar
    mToolbar.setNavigationIcon(R.drawable.ic_left_arraw_128);
    mToolbar.setNavigationOnClickListener(mPresenter);
    // recyclerview
    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    mRecyclerView.setNeedRefresh(true);

    mSwipeTopBottomLayout = mRecyclerView.getSwipeTopBottomLayout();
    mSwipeTopBottomLayout.setRefreshing(true);
    mSwipeTopBottomLayout.setOnLoadMoreListener(mPresenter);
    mSwipeTopBottomLayout.setOnRefreshListener(mPresenter);
    mRecyclerView.setOnItemClickListener(mPresenter, R.id.layout_item_content_fragment,
        R.id.imgv1_item_content_content_fragment, R.id.imgv2_item_content_content_fragment);
  }

  @Override public Context getViewContext() {
    return mContext;
  }

  @Override public void showToast(String msg) {

  }

  @Override public RealmHelper getRealmHelper() {
    return mRealmHelper;
  }

  @Override public void setRecyclerViewAdapter(CategoryAdapter adapter) {
    mRecyclerView.setAdapter(adapter);
  }

  @Override public void setToolbarTitle(String title) {
    mToolbar.setTitle(title);
  }

  @Override public Bundle getFragmentBundle() {
    return getArguments();
  }

  @Override public void stopRefresh() {
    mSwipeTopBottomLayout.setLoadingMore(false);
    mSwipeTopBottomLayout.setRefreshing(false);
  }

  @Override public void scrollRecyclerViewToPos(int pos) {
    mRecyclerView.smoothScrollToPosition(pos);
  }

  @Override public void removeSelf() {
    getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
  }
}
