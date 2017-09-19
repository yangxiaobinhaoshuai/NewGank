package com.yangxiaobin.gank.mvp.model.remote;

import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.common.net.ApiService;
import com.yangxiaobin.gank.common.utils.RxUtils;
import com.yangxiaobin.gank.mvp.contract.SearchContract;
import io.reactivex.Flowable;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/8/28.
 */

public class SearchModel implements SearchContract.Model {
  @Inject ApiService mApiService;

  @Inject public SearchModel(ApiService apiService) {
    mApiService = apiService;
  }

  @Override
  public Flowable<CategoryEntity> getSearchRes(String query, String category, int count,
      int page) {
    return mApiService.getSearchResult(query, category, count, page)
        .compose(RxUtils.<CategoryEntity>switchFlowableSchedulers());
  }
}
