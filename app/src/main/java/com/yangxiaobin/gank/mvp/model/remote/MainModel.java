package com.yangxiaobin.gank.mvp.model.remote;

import com.yangxiaobin.gank.common.bean.GankDailyDataEntity;
import com.yangxiaobin.gank.common.bean.GankDailyTitleEntity;
import com.yangxiaobin.gank.common.bean.GankTotalHistoryEntity;
import com.yangxiaobin.gank.common.bean.GitHubUserEntity;
import com.yangxiaobin.gank.common.net.ApiService;
import com.yangxiaobin.gank.common.utils.RxUtils;
import com.yangxiaobin.gank.di.scope.LoginUsed;
import com.yangxiaobin.gank.mvp.contract.MainContract;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import javax.inject.Inject;
import retrofit2.Retrofit;

/**
 * Created by handsomeyang on 2017/7/6.
 */

public class MainModel implements MainContract.Model {

  private ApiService mApiService;
  private @LoginUsed Retrofit mRetrofit;

  @Inject public MainModel(ApiService apiService, @LoginUsed Retrofit retrofit) {
    mApiService = apiService;
    mRetrofit = retrofit;
  }

  /**
   * 获取 某天gank内容
   *
   * @return 每日数据
   */
  @Override public Flowable<GankDailyDataEntity> getGankDailyData(int year, int month, int date) {
    return mApiService.getDailyData(year, month, date)
        .compose(RxUtils.<GankDailyDataEntity>switchFlowableSchedulers());
  }

  /**
   * 获取gank 历史
   *
   * @return 历史日期
   */
  @Override public Flowable<GankTotalHistoryEntity> getTotalHistory() {
    return mApiService.getTotalHistory()
        .compose(RxUtils.<GankTotalHistoryEntity>switchFlowableSchedulers());
  }

  /**
   * 获取title
   *
   * @param count 标题数量
   * @param page 第几页
   * @return 标题
   */
  @Override public Single<GankDailyTitleEntity> getTitle(int count, int page) {
    return mApiService.getDailyTitle(count, page)
        .compose(RxUtils.<GankDailyTitleEntity>switchSingleSchedulers());
  }

  @Override public Observable<GitHubUserEntity> doGithubLogin(String account) {
    return mRetrofit.create(ApiService.class)
        .getUserInfo(account)
        .compose(RxUtils.<GitHubUserEntity>switchObservableSchedulers());
  }
}
