package com.yangxiaobin.gank.mvp.presenter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BasePresenter;
import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.common.net.ErrorConsumer;
import com.yangxiaobin.gank.mvp.contract.CategoryContract;
import com.yangxiaobin.gank.mvp.view.activity.LandscapeVideoActivity;
import com.yangxiaobin.gank.mvp.view.adapter.CategoryAdapter;
import com.yangxiaobin.gank.mvp.view.fragment.PicDialogFragment;
import com.yangxiaobin.gank.mvp.view.fragment.WebFragment;
import com.yxb.base.CommonKey;
import com.yxb.base.utils.ActivitySkipper;
import com.yxb.base.utils.FragmentSkipper;
import com.yxb.easy.listener.OnItemClickListener;
import com.yxb.easy.refresh.SwipeTopBottomLayout;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/7/25.
 */

public class CategoryPresenter extends BasePresenter
    implements CategoryContract.Presenter, SwipeTopBottomLayout.OnLoadMoreListener,
    OnItemClickListener, SwipeTopBottomLayout.OnRefreshListener, View.OnClickListener {

  private CategoryContract.View mView;
  private CategoryContract.Model mModel;
  private String mTitle;
  private static final int COUNT_EACH_PAGE = 10;
  private List<CategoryEntity.ResultsBean> mResultsBeans;
  private CategoryAdapter mAdapter;
  private int mCurrentPage = 1;
  private PicDialogFragment mPicDialogFragment;

  @Inject public CategoryPresenter(CategoryContract.View view, CategoryContract.Model model) {
    mView = view;
    mModel = model;
  }

  @Override public void onDestroy() {
    unSubscribe();
    mView.getRealmHelper().closeRealm();
  }

  @Override public void start() {
    Bundle argument = mView.getFragmentBundle();
    if (argument != null) {
      mTitle = argument.getString(CommonKey.STR1);
      if (!TextUtils.isEmpty(mTitle)) {
        mView.setToolbarTitle(mTitle);
        getListData();
      }
    }
    mPicDialogFragment = new PicDialogFragment();
  }

  private void getListData() {
    Disposable getSomeCategory = mModel.getSomeCategory(mTitle, COUNT_EACH_PAGE, 1)
        .subscribe(new Consumer<CategoryEntity>() {
          @Override public void accept(CategoryEntity entity) throws Exception {
            mResultsBeans = entity.getResults();
            mAdapter = new CategoryAdapter(mResultsBeans);
            mView.setRecyclerViewAdapter(mAdapter);
            mView.stopRefresh();
          }
        }, new ErrorConsumer());
    register(getSomeCategory);
  }

  @Override public void onLoadMore() {
    mCurrentPage++;
    if (!TextUtils.isEmpty(mTitle)) {
      Disposable getSomeCategory = mModel.getSomeCategory(mTitle, COUNT_EACH_PAGE, mCurrentPage)
          .subscribe(new Consumer<CategoryEntity>() {
            @Override public void accept(CategoryEntity entity) throws Exception {
              List<CategoryEntity.ResultsBean> results = entity.getResults();
              mResultsBeans.addAll(results);
              mAdapter.notifyItemRangeInserted(mResultsBeans.size() - results.size(),
                  mResultsBeans.size());
              mView.scrollRecyclerViewToPos(mResultsBeans.size() - results.size());
              mView.stopRefresh();
            }
          }, new ErrorConsumer());
      register(getSomeCategory);
    }
  }

  @Override public void onItemClick(View view, int pos, MotionEvent motionEvent) {
    CategoryEntity.ResultsBean entity = mResultsBeans.get(pos);
    switch (view.getId()) {
      case R.id.layout_item_content_fragment:
        if (entity.getType().equals(Constant.Category.VIDEO)) {
          // start Video Activity
          ActivitySkipper.getInstance()
              .init(mView.getViewContext())
              .putExtras(CommonKey.STR1, entity.getUrl())
              .putExtras(CommonKey.STR2, entity.getDesc())
              .skip(LandscapeVideoActivity.class);
        } else {
          // start webFragment
          startWebFragment(entity);
        }
        App.getINSTANCE().getItemUrls().add(entity.getUrl());
        mAdapter.notifyItemChanged(pos);
        break;
      case R.id.imgv1_item_content_content_fragment:
        String url = entity.getImages().get(1);
        if (!TextUtils.isEmpty(url)) {
          mPicDialogFragment.setUrl(url);
          showPicDialog();
        }
        break;
      case R.id.imgv2_item_content_content_fragment:
        //如果只有一张图片就显示这张
        mPicDialogFragment.setUrl(entity.getImages().get(0));
        showPicDialog();
        break;
      default:
        break;
    }
  }

  private void startWebFragment(CategoryEntity.ResultsBean entity) {
    String webUrl = entity.getUrl();
    String title = entity.getDesc();
    FragmentSkipper.getInstance()
        .init(mView.getViewContext())
        .target(new WebFragment().setUrl(webUrl).setTitle(title))
        .add(android.R.id.content, true);
  }

  private void showPicDialog() {
    mPicDialogFragment.show(((FragmentActivity) mView.getViewContext()).getSupportFragmentManager(),
        mPicDialogFragment.getClass().getSimpleName());
  }

  @Override public void onRefresh() {
    mResultsBeans.clear();
    getListData();
  }

  @Override public void onClick(View v) {
    mView.removeSelf();
  }
}
