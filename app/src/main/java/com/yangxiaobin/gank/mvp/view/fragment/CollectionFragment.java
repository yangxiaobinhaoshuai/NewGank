package com.yangxiaobin.gank.mvp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import com.handsome.library.T;
import com.yangxiaobin.EasyRecyclerView;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BaseFragment;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.common.utils.UserUtils;
import com.yangxiaobin.gank.mvp.contract.CollectionContract;
import com.yangxiaobin.gank.mvp.presenter.CollectionPresenter;
import com.yangxiaobin.gank.mvp.view.adapter.ContentAdapter;
import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends BaseFragment implements CollectionContract.View {

  @BindView(R.id.recyclerview_collection_fragment) EasyRecyclerView mRecyclerView;
  @BindView(R.id.toolbar_collection_frament) Toolbar mToolbar;
  @Inject CollectionPresenter mPresenter;
  @Inject RealmHelper mRealmHelper;

  @Override protected int getLayoutResId() {
    return R.layout.fragment_collection;
  }

  @Override protected void initialize(Bundle bundle) {
    mToolbar.setTitle("收藏");
    mToolbar.setNavigationIcon(R.drawable.ic_left_arraw_128);
    mToolbar.setNavigationOnClickListener(mPresenter);
    if (!UserUtils.hasLogined(mContext)) {
      T.info(getString(R.string.please_login_frist));
      return;
    }
    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mRecyclerView.setAntiShake(1000);
    mRecyclerView.setOnItemClickListener(mPresenter, R.id.layout_item_content_fragment,
        R.id.imgv1_item_content_content_fragment, R.id.imgv2_item_content_content_fragment,
        R.id.layout_title_content_fragment);
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    mPresenter.start();
  }

  @Override public Context getViewContext() {
    return mContext;
  }

  @Override public void showToast(String msg) {

  }

  @Override public void removeSelf() {
    getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
  }

  @Override public void setRecyclerViewAdapter(ContentAdapter adapter) {
    mRecyclerView.setAdapter(adapter);
  }

  @Override public RealmHelper getRealmHelper() {
    return mRealmHelper;
  }
}
