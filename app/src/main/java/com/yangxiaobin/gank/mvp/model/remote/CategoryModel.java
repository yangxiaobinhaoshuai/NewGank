package com.yangxiaobin.gank.mvp.model.remote;

import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.common.net.ApiService;
import com.yangxiaobin.gank.common.utils.RxUtils;
import com.yangxiaobin.gank.mvp.contract.CategoryContract;
import io.reactivex.Flowable;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/7/25.
 */

public class CategoryModel implements CategoryContract.Model {

  private ApiService mApiService;

  @Inject public CategoryModel(ApiService apiService) {
    mApiService = apiService;
  }

  @Override
  public Flowable<CategoryEntity> getSomeCategory(String category, int dataCount, int pageCount) {
    return mApiService.getSomeCategory(category, dataCount, pageCount)
        .compose(RxUtils.<CategoryEntity>switchFlowableSchedulers());
  }
}
