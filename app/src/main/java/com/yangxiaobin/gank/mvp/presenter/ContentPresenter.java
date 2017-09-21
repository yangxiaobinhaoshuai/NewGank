package com.yangxiaobin.gank.mvp.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BasePresenter;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.common.bean.GankDailyDataEntity;
import com.yangxiaobin.gank.common.utils.CircularRevealUtils;
import com.yangxiaobin.gank.common.utils.SPUtils;
import com.yangxiaobin.gank.mvp.contract.ContentContract;
import com.yangxiaobin.gank.mvp.view.activity.LandscapeVideoActivity;
import com.yangxiaobin.gank.mvp.view.adapter.ContentAdapter;
import com.yangxiaobin.gank.mvp.view.fragment.CategoryFragment;
import com.yangxiaobin.gank.mvp.view.fragment.MeiziFragment;
import com.yangxiaobin.gank.mvp.view.fragment.PicDialogFragment;
import com.yangxiaobin.gank.mvp.view.fragment.WebFragment;
import com.yangxiaobin.kits.base.ActivitySkiper;
import com.yangxiaobin.kits.base.CommonKey;
import com.yangxiaobin.kits.base.FragmentSkiper;
import com.yangxiaobin.listener.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/7/11.
 */

public class ContentPresenter extends BasePresenter
    implements ContentContract.Presenter, View.OnClickListener, OnItemClickListener {

  private ContentContract.Model mModel;
  private ContentContract.View mView;

  private GankDailyDataEntity mDailyEntity;
  private List<ContentItemEntity> mContentItemEntities;
  private ContentAdapter mAdapter;
  private String mMeiZiUrl;
  private GankDailyDataEntity.ResultsBean.休息视频Bean mVideoEntity;
  private PicDialogFragment mPicDialogFragment;     // 图片放大dialog

  @Inject public ContentPresenter(ContentContract.Model model, ContentContract.View view) {
    mModel = model;
    mView = view;
  }

  @Override public void start() {
    mView.setUpToolbar();
    Bundle bundle = mView.getFragmentArgument();
    if (bundle != null) {
      mDailyEntity = (GankDailyDataEntity) bundle.getSerializable(CommonKey.OBJ1);
    } else {
      mView.showToast(mView.getViewContext().getString((R.string.error_parse_server)));
      return;
    }
    // 设置toolbar title
    assert mDailyEntity != null;
    mView.setUpToolbarTitle(mDailyEntity.getDate().split("T")[0]);
    initializeImageView();
    processData();
    mView.setUpRecyclerView();
    mAdapter = new ContentAdapter(mContentItemEntities);
    mView.setRecyclerViewAdapter(mAdapter);

    mPicDialogFragment = new PicDialogFragment();
  }

  private void processData() {
    // set data
    mContentItemEntities = new ArrayList<>();
    for (String title : mDailyEntity.getCategory()) {
      ContentItemEntity titleEntity = new ContentItemEntity();
      if (title.equals(Constant.Category.MEIZI)) {
        continue;
      }
      titleEntity.setTitle(title);
      mContentItemEntities.add(titleEntity);
      addAllEntities(title);
    }
  }

  private void initializeImageView() {
    mMeiZiUrl = mDailyEntity.getResults().get福利().get(0).getUrl();
    List<GankDailyDataEntity.ResultsBean.休息视频Bean> 休息视频 = mDailyEntity.getResults().get休息视频();
    if (休息视频 != null) {
      mVideoEntity = 休息视频.get(0);
    }
    SPUtils.put(mView.getViewContext(), Constant.KEY_SPLASH_IMAGE_PATH, mMeiZiUrl);
    mView.setImageViewUrl(mMeiZiUrl);
  }

  private void addAllEntities(String title) {
    GankDailyDataEntity.ResultsBean results = mDailyEntity.getResults();
    switch (title) {
      case Constant.Category.ANDROID:
        mContentItemEntities.addAll(results.getAndroid());
        break;
      case Constant.Category.IOS:
        mContentItemEntities.addAll(results.getIOS());
        break;
      case Constant.Category.WEB:
        mContentItemEntities.addAll(results.get前端());
        break;
      case Constant.Category.EXTEND:
        mContentItemEntities.addAll(results.get拓展资源());
        break;
      case Constant.Category.VIDEO:
        mContentItemEntities.addAll(results.get休息视频());
        break;
      case Constant.Category.SUGGEST:
        mContentItemEntities.addAll(results.get瞎推荐());
        break;
      case Constant.Category.APP:
        mContentItemEntities.addAll(results.getApp());
      default:
        break;
    }
  }

  @Override public void onDestroy() {
    unSubscribe();
    mView.getRealmHelper().closeRealm();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.imgv_collapse_conentent_fragment:
        startMeiziFragmentWithAnim(v);
        break;
      case R.id.fab_content_fragment:
        // fab
        // 判断是否有视频
        if (mVideoEntity != null) {
          startVideoActivity(mVideoEntity.getUrl(), mVideoEntity.getDesc());
        } else {
          mView.showToast(mView.getViewContext().getString(R.string.no_video_today));
        }
        break;
      default:
        // fragment toolbar navigation click listener
        mView.removeSelf();
        break;
    }
  }

  private void startMeiziFragmentWithAnim(final View v) {
    // imageview click listener
    CircularRevealUtils.animateRevealHide(v).addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        MeiziFragment meiziFragment = new MeiziFragment();
        Fade fade = new Fade();
        fade.setDuration(300);
        meiziFragment.setEnterTransition(fade);
        meiziFragment.setExitTransition(fade);
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(300);
        meiziFragment.setSharedElementEnterTransition(changeBounds);

        FragmentSkiper.getInstance()
            .init(((FragmentActivity) mView.getViewContext()))
            .target(meiziFragment)
            .addSharedElement(v,
                mView.getViewContext().getString(R.string.share_element_main_item_to_content))
            .putString(CommonKey.STR1, mMeiZiUrl)
            .replace(android.R.id.content, true);
      }
    });
  }

  // 跳转横屏activity播放
  private void startVideoActivity(String url, String title) {
    ActivitySkiper.getInstance()
        .init(mView.getViewContext())
        .putExtras(CommonKey.STR1, url)
        .putExtras(CommonKey.STR2, title)
        .skip(LandscapeVideoActivity.class);
  }

  @Override public void onItemClick(View view, int pos, MotionEvent motionEvent) {
    // content
    ContentItemEntity entity = mContentItemEntities.get(pos);
    switch (view.getId()) {
      case R.id.layout_title_content_fragment:
        // title  start category fragment
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
  }

  private void startWebFragment(ContentItemEntity entity, int pos) {
    // 跳转webFragment
    if (Constant.Category.VIDEO.equals(entity.getType())) {
      startVideoActivity(mVideoEntity.getUrl(), mVideoEntity.getDesc());
    } else {
      FragmentSkiper.getInstance()
          .init(((FragmentActivity) mView.getViewContext()))
          .target(new WebFragment().setUrl(entity.getUrl()).setTitle(entity.getDesc()))
          .add(android.R.id.content, true);
    }
    // 存储点击过的item
    App.getINSTANCE().getItemUrls().add(entity.getUrl());
    mAdapter.notifyItemChanged(pos);
  }

  private void showPicDialog() {
    mPicDialogFragment.show(((FragmentActivity) mView.getViewContext()).getSupportFragmentManager(),
        mPicDialogFragment.getClass().getSimpleName());
  }
}
