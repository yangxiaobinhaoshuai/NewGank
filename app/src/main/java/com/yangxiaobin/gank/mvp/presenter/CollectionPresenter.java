package com.yangxiaobin.gank.mvp.presenter;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BasePresenter;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.common.bean.GankDailyDataEntity;
import com.yangxiaobin.gank.mvp.contract.CollectionContract;
import com.yangxiaobin.gank.mvp.view.activity.LandscapeVideoActivity;
import com.yangxiaobin.gank.mvp.view.adapter.ContentAdapter;
import com.yangxiaobin.gank.mvp.view.adapter.FlagForContentAdapter;
import com.yangxiaobin.gank.mvp.view.fragment.CategoryFragment;
import com.yangxiaobin.gank.mvp.view.fragment.PicDialogFragment;
import com.yangxiaobin.gank.mvp.view.fragment.WebFragment;
import com.yangxiaobin.kits.base.ActivitySkiper;
import com.yangxiaobin.kits.base.CommonKey;
import com.yangxiaobin.kits.base.FragmentSkiper;
import com.yangxiaobin.listener.OnItemClickListener;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/8/15.
 */

public class CollectionPresenter extends BasePresenter
    implements CollectionContract.Presenter, View.OnClickListener, OnItemClickListener {

  private CollectionContract.Model mModel;
  private CollectionContract.View mView;

  private PicDialogFragment mPicDialogFragment;     // 图片放大dialog
  private ContentAdapter mAdapter;
  private List<ContentItemEntity> mContentItemEntities;
  private GankDailyDataEntity.ResultsBean.休息视频Bean mVideoEntity;

  @Inject public CollectionPresenter(CollectionContract.Model model, CollectionContract.View view) {
    mModel = model;
    mView = view;
  }

  @Override public void onDestroy() {
    mView.getRealmHelper().closeRealm();
  }

  @Override public void start() {
    mContentItemEntities = getDataFromDB();
    mAdapter = new ContentAdapter(mContentItemEntities, FlagForContentAdapter.COLLECTION);
    mView.setRecyclerViewAdapter(mAdapter);
    mPicDialogFragment = new PicDialogFragment();
  }

  // navigation click listener
  @Override public void onClick(View v) {
    mView.removeSelf();
  }

  private List<ContentItemEntity> getDataFromDB() {
    return mView.getRealmHelper().getAllSortedConentItemEntities();
  }

  @Override public void onItemClick(View view, int pos, MotionEvent motionEvent) {
    switch (view.getId()) {
      case R.id.layout_title_content_fragment:
        // title
        // start category fragment
        if (view.getId() == R.id.layout_title_content_fragment) {
          TextView textView = (TextView) view.findViewById(R.id.tv_item_title_content_fragment);
          FragmentSkiper.getInstance()
              .init(((FragmentActivity) mView.getViewContext()))
              .target(new CategoryFragment())
              .putString(CommonKey.STR1, textView.getText().toString().trim())
              .add(android.R.id.content, true);
        }
        break;
      case R.id.layout_item_content_fragment:
        // content
        ContentItemEntity entity = mContentItemEntities.get(pos);
        switch (view.getId()) {
          case R.id.layout_item_content_fragment:
            startWebFragment(entity, pos);
            break;
          case R.id.imgv1_item_content_content_fragment:
            String url = entity.getImages().get(1);
            if (!TextUtils.isEmpty(url)) {
              mPicDialogFragment.setUrl(url);
              showPicDialog();
            }
            break;
          case R.id.imgv2_item_content_content_fragment:
            mPicDialogFragment.setUrl(entity.getImages().get(0));
            showPicDialog();
            break;
          default:
            break;
        }
        break;
      default:
        break;
    }
  }

  private void startWebFragment(ContentItemEntity entity, int pos) {
    // 跳转webFragment
    if (Constant.Category.VIDEO.equals(entity.getType())) {
      startVideoActivity(entity);
    } else {
      FragmentSkiper.getInstance()
          .init(((FragmentActivity) mView.getViewContext()))
          .target(new WebFragment().setUrl(entity.getUrl()).setTitle(entity.getDesc()))
          .add(android.R.id.content, true);
    }
    App.getINSTANCE().getItemUrls().add(entity.getUrl());
    mAdapter.notifyItemChanged(pos);
  }

  // 跳转横屏activity播放
  private void startVideoActivity(ContentItemEntity entity) {
    ActivitySkiper.getInstance()
        .init(mView.getViewContext())
        .putExtras(CommonKey.STR1, entity.getUrl())
        .putExtras(CommonKey.STR2, entity.getDesc())
        .skip(LandscapeVideoActivity.class);
  }

  private void showPicDialog() {
    mPicDialogFragment.show(((FragmentActivity) mView.getViewContext()).getSupportFragmentManager(),
        mPicDialogFragment.getClass().getSimpleName());
  }
}
