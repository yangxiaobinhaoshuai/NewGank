package com.yangxiaobin.gank.mvp.contract;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import com.yangxiaobin.gank.common.base.IBaseModel;
import com.yangxiaobin.gank.common.base.IBaseView;
import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.mvp.view.adapter.CategoryAdapter;
import com.yxb.easy.EasyRecyclerView;
import io.reactivex.Flowable;

/**
 * Created by handsomeyang on 2017/8/28.
 */

public interface SearchContract {

  interface Model extends IBaseModel {

    Flowable<CategoryEntity> getSearchRes(String query, String category, int count, int page);
  }

  interface View extends IBaseView {

    void removeSelf();

    void setRecyclerViewAdapter(CategoryAdapter adapter);

    RealmHelper getRealmHelper();

    void stopRefreshAndLoadMore();

    void recyclerViewMoveToPos(int pos);

    SearchView.SearchAutoComplete getViewForInputManager();

    Toolbar getSuggestWindowAnchor();

    void setHistoryQueryAndSubmit(String content);

    EasyRecyclerView getHeaderAndFooterParent();
  }

  interface Presenter {

    void onDestroy();
  }
}
