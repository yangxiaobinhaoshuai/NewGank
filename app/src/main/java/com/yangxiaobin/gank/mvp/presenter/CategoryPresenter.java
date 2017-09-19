package com.yangxiaobin.gank.mvp.presenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import com.handsome.library.T;
import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BasePresenter;
import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.common.net.ErrorConsumer;
import com.yangxiaobin.gank.common.utils.UserUtils;
import com.yangxiaobin.gank.mvp.contract.CategoryContract;
import com.yangxiaobin.gank.mvp.view.adapter.CategoryAdapter;
import com.yangxiaobin.gank.mvp.view.fragment.PicDialogFragment;
import com.yangxiaobin.gank.mvp.view.fragment.WebFragment;
import com.yangxiaobin.kits.base.CommonKey;
import com.yangxiaobin.kits.base.FragmentSkiper;
import com.yangxiaobin.listener.OnItemClickListener;
import com.yangxiaobin.listener.OnItemLongClickListener;
import com.yangxiaobin.refresh.SwipeTopBottomLayout;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/7/25.
 */

public class CategoryPresenter extends BasePresenter
    implements CategoryContract.Presenter, SwipeTopBottomLayout.OnLoadMoreListener,
    OnItemClickListener, SwipeTopBottomLayout.OnRefreshListener, View.OnClickListener,
    OnItemLongClickListener, DialogInterface.OnClickListener {

  private CategoryContract.View mView;
  private CategoryContract.Model mModel;
  private String mTitle;
  private static final int COUNT_EACH_PAGE = 10;
  private List<CategoryEntity.ResultsBean> mResultsBeans;
  private CategoryAdapter mAdapter;
  private int mCurrentPage = 1;
  private PicDialogFragment mPicDialogFragment;
  private int mLongClickedPos;
  private AlertDialog.Builder mBuilder;

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
    mBuilder = new AlertDialog.Builder(mView.getViewContext());
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
        // start webfragment
        String webUrl = mResultsBeans.get(pos).getUrl();
        String title = mResultsBeans.get(pos).getDesc();
        FragmentSkiper.getInstance()
            .init(((FragmentActivity) mView.getViewContext()))
            .target(new WebFragment().setUrl(webUrl).setTitle(title))
            .add(android.R.id.content, true);
        App.getINSTANCE().getItemUrls().add(webUrl);
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

  private void showPicDialog() {
    mPicDialogFragment.show(((FragmentActivity) mView.getViewContext()).getSupportFragmentManager(),
        mPicDialogFragment.getClass().getSimpleName());
  }

  // 长按添加收藏
  @Override public void onItemLongClick(View view, int pos, MotionEvent event) {
    if (!hasInsertedBefore(mResultsBeans.get(pos))) {
      mBuilder.setItems(new String[] { "添加收藏", }, this);
    } else {
      mBuilder.setItems(new String[] { "删除收藏" }, this);
    }
    mBuilder.create().show();
    mLongClickedPos = pos;
  }

  @Override public void onClick(DialogInterface dialog, int which) {
    if (!UserUtils.hasLogined(mView.getViewContext())) {
      T.info(mView.getViewContext().getString(R.string.please_login_frist));
    } else {
      ContentItemEntity entity = mResultsBeans.get(mLongClickedPos);
      if (hasInsertedBefore(entity)) {
        mView.getRealmHelper().delete(entity);
      } else {
        mView.getRealmHelper().insert(entity);
      }
      // refresh ui
      mAdapter.notifyItemChanged(mLongClickedPos);
    }
  }

  // 查询是否添加过
  private boolean hasInsertedBefore(ContentItemEntity entity) {
    return mView.getRealmHelper().findOne(entity) != null;
  }

  @Override public void onRefresh() {
    mResultsBeans.clear();
    getListData();
  }

  @Override public void onClick(View v) {
    mView.removeSelf();
  }
}
