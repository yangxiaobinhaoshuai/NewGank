package com.yangxiaobin.gank.mvp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import butterknife.BindView;
import com.handsome.library.T;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BaseFragment;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.common.utils.ImageUtils;
import com.yangxiaobin.gank.mvp.contract.ContentContract;
import com.yangxiaobin.gank.mvp.presenter.ContentPresenter;
import com.yangxiaobin.gank.mvp.view.adapter.ContentAdapter;
import com.yxb.base.utils.ConvertUtils;
import com.yxb.base.utils.ScreenUtils;
import com.yxb.easy.EasyRecyclerView;
import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends BaseFragment implements ContentContract.View {

  @Inject ContentPresenter mPresenter;
  @Inject RealmHelper mRealmHelper;
  @BindView(R.id.auto_recyclerview_content_fragment) EasyRecyclerView mRecyclerView;
  @BindView(R.id.imgv_collapse_conentent_fragment) ImageView mImageView;
  @BindView(R.id.toolbar_content_fragment) Toolbar mToolbar;
  @BindView(R.id.fab_content_fragment) FloatingActionButton mFabVideo;

  @Override protected int getLayoutResId() {
    return R.layout.fragment_content;
  }

  @Override protected void initialize(Bundle bundle) {
    super.initialize(bundle);
    mPresenter.start();
  }

  @Override public Context getViewContext() {
    return mContext;
  }

  @Override public void showToast(String msg) {
    T.info(msg);
  }

  @Override public void setUpToolbar() {
    mToolbar.setNavigationIcon(R.drawable.ic_left_arraw_128);
    mToolbar.setNavigationOnClickListener(mPresenter);
    mImageView.setOnClickListener(mPresenter);
    mFabVideo.setOnClickListener(mPresenter);
  }

  @Override public void setUpToolbarTitle(String title) {
    mToolbar.setTitle(title);
  }

  @Override public Bundle getFragmentArgument() {
    return getArguments();
  }

  @Override public void setUpRecyclerView() {
    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    mRecyclerView.setAntiShake(1000);
    mRecyclerView.setOnItemClickListener(mPresenter, R.id.layout_item_content_fragment,
        R.id.imgv1_item_content_content_fragment, R.id.imgv2_item_content_content_fragment,
        R.id.layout_title_content_fragment);
  }

  @Override public void setRecyclerViewAdapter(ContentAdapter adapter) {
    mRecyclerView.setAdapter(adapter);
  }

  @Override public void setImageViewUrl(String url) {
    float screenWidth = ScreenUtils.getScreenWidth();
    int height = ConvertUtils.dp2px(156);
    ImageUtils.load(mContext, url, mImageView, ((int) screenWidth), height);
  }

  @Override public RealmHelper getRealmHelper() {
    return mRealmHelper;
  }

  @Override public void removeSelf() {
    getFragmentManager().beginTransaction().remove(this).commit();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mPresenter.onDestroy();
    mRealmHelper.closeRealm();
  }
}
