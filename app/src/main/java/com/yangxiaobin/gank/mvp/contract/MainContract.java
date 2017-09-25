package com.yangxiaobin.gank.mvp.contract;

import android.graphics.Bitmap;
import com.yangxiaobin.gank.common.base.IBaseModel;
import com.yangxiaobin.gank.common.base.IBaseView;
import com.yangxiaobin.gank.common.bean.GankDailyDataEntity;
import com.yangxiaobin.gank.common.bean.GankDailyTitleEntity;
import com.yangxiaobin.gank.common.bean.GankTotalHistoryEntity;
import com.yangxiaobin.gank.common.bean.GitHubUserEntity;
import com.yxb.easy.adapter.AdapterWrapper;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.reactivestreams.Publisher;

/**
 * Created by handsomeyang on 2017/7/6.
 */

public interface MainContract {

  interface Model extends IBaseModel {

    Flowable<GankTotalHistoryEntity> getTotalHistory();

    Publisher<GankDailyDataEntity> getGankDailyData(int year, int month, int date);

    Single<GankDailyTitleEntity> getTitle(int count, int page);

    Observable<GitHubUserEntity> doGithubLogin(String account);
  }

  interface View extends IBaseView {

    void setToolbarTitle(String title);

    void setUpRecyclerView();

    void setRecyclerViewAdapter(AdapterWrapper adapter);

    void initCardHelper();

    int getCurrentItemPos();

    void startSwitchBgAnim(Bitmap bitmap);

    void stopLoadingMore();

    void setUserHeadImage(String url);

    void setUserName(String name);

    void resetUser();

    android.view.View showLoadError();
  }

  interface Presenter {

    void onDestroy();

    boolean onBackPress();
  }
}
