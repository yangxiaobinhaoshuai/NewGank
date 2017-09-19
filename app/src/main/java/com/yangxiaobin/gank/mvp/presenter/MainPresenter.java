package com.yangxiaobin.gank.mvp.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.orhanobut.logger.Logger;
import com.yangxiaobin.Constant;
import com.yangxiaobin.adapter.AdapterWrapper;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BasePresenter;
import com.yangxiaobin.gank.common.bean.GankDailyDataEntity;
import com.yangxiaobin.gank.common.bean.GankDailyTitleEntity;
import com.yangxiaobin.gank.common.bean.GankTotalHistoryEntity;
import com.yangxiaobin.gank.common.bean.GitHubUserEntity;
import com.yangxiaobin.gank.common.net.ApiExceptionHandler;
import com.yangxiaobin.gank.common.net.ErrorConsumer;
import com.yangxiaobin.gank.common.utils.CircularRevealUtils;
import com.yangxiaobin.gank.common.utils.CleanCatcheUtils;
import com.yangxiaobin.gank.common.utils.NetworkUtils;
import com.yangxiaobin.gank.common.utils.Rx2Bus;
import com.yangxiaobin.gank.common.utils.RxUtils;
import com.yangxiaobin.gank.common.utils.SPUtils;
import com.yangxiaobin.gank.common.utils.UserUtils;
import com.yangxiaobin.gank.mvp.contract.MainContract;
import com.yangxiaobin.gank.mvp.view.adapter.MainAdapter;
import com.yangxiaobin.gank.mvp.view.fragment.AboutFragment;
import com.yangxiaobin.gank.mvp.view.fragment.CategoryFragment;
import com.yangxiaobin.gank.mvp.view.fragment.CollectionFragment;
import com.yangxiaobin.gank.mvp.view.fragment.ContentFragment;
import com.yangxiaobin.gank.mvp.view.fragment.LoginDialogFragment;
import com.yangxiaobin.gank.mvp.view.fragment.SearchFragment;
import com.yangxiaobin.gank.mvp.view.fragment.SplashFragment;
import com.yangxiaobin.gank.mvp.view.fragment.WebFragment;
import com.yangxiaobin.kits.base.CommonKey;
import com.yangxiaobin.kits.base.FragmentSkiper;
import com.yangxiaobin.listener.OnAdapterLoadMoreListener;
import com.yangxiaobin.listener.OnItemClickListener;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.reactivestreams.Publisher;

/**
 * Created by handsomeyang on 2017/7/6.
 */

public class MainPresenter extends BasePresenter
    implements MainContract.Presenter, NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener, Toolbar.OnMenuItemClickListener, OnItemClickListener,
    OnAdapterLoadMoreListener {

  private MainContract.View mView;
  private MainContract.Model mModel;
  private List<GankDailyDataEntity> mCollectEntities;       // collectionInto 用
  private List<GankDailyDataEntity> mTotalEntities;         // 保存所有的联网数据
  private long mLastClickTime;                              // 安全退出
  private long mSkipCount;                                  // 加载更多的时候跳过加载过的事件
  private int mSkipPage = 1;                                // 加载更多的时候标题跳过页数
  private boolean isLoadingMore;
  private LoginDialogFragment mDialogFragment;
  private ProgressDialog mProgressDialog;
  private SplashFragment mSplashFragment;
  private boolean isError;                                  // 网络是否出错
  private View mImageError;
  private View mTvError;
  private View mProgressError;
  private View mErrorView;
  private AdapterWrapper mAdapterWrapper;

  @Inject public MainPresenter(MainContract.View view, MainContract.Model model) {
    mView = view;
    mModel = model;
  }

  @Override public void start() {
    // 为了实例化toolbar上的TextView  只要TextUtils.ieEmpty(title) 为false
    mView.setToolbarTitle("妹子");
    startSplashFragment();
    mView.setUpRecyclerView();
    mDialogFragment = new LoginDialogFragment();
    RegisterRxBusObserverForChangeUser();
    doNetGetData();
  }

  //  设置头像和用户名
  private void RegisterRxBusObserverForChangeUser() {
    Disposable busSubscribe = Rx2Bus.getDefault()
        .toFlowable(GitHubUserEntity.class)
        .subscribe(new Consumer<GitHubUserEntity>() {
          @Override public void accept(GitHubUserEntity entity) throws Exception {
            mView.setUserHeadImage(entity.getAvatar_url());
            mView.setUserName(entity.getName());
          }
        }, new ErrorConsumer());
    register(busSubscribe);
  }

  // 联网获取5天数据
  public void doNetGetData() {
    mCollectEntities = new ArrayList<>(Constant.MEIZI_COUNT);
    Disposable netSubscribe =
        mModel.getTotalHistory()
            .map(new Function<GankTotalHistoryEntity, List<String>>() {
              @Override public List<String> apply(GankTotalHistoryEntity historyEntity)
                  throws Exception {
                // 所有的日期
                return historyEntity.getResults();
              }
            })
            .flatMap(new Function<List<String>, Publisher<String>>() {
              @Override public Publisher<String> apply(List<String> strings) throws Exception {
                return Flowable.fromIterable(strings);
              }
            })
            .skip(mSkipCount)
            .take(Constant.MEIZI_COUNT)
            .concatMap(new Function<String, Publisher<GankDailyDataEntity>>() {
              @Override public Publisher<GankDailyDataEntity> apply(String s) throws Exception {
                // 每一天的日期 获取当天的数据
                String[] split = s.split("-");
                return mModel.getGankDailyData(Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]), Integer.parseInt(split[2]));
              }
            })
            .collectInto(mCollectEntities,
                new BiConsumer<List<GankDailyDataEntity>, GankDailyDataEntity>() {
                  @Override public void accept(List<GankDailyDataEntity> gankDailyDataEntities,
                      GankDailyDataEntity entity) throws Exception {
                    // 把制定数量的天数的数据添加到集合当中
                    mCollectEntities.add(entity);
                  }
                })
            //添加每日title
            .zipWith(mModel.getTitle(Constant.MEIZI_COUNT, mSkipPage),
                new BiFunction<List<GankDailyDataEntity>, GankDailyTitleEntity, List<GankDailyDataEntity>>() {
                  @Override public List<GankDailyDataEntity> apply(
                      List<GankDailyDataEntity> dailyDataEntities, GankDailyTitleEntity titleEntity)
                      throws Exception {
                    // 合并数据流  添加日期
                    processAddDateField(dailyDataEntities, titleEntity);
                    return dailyDataEntities;
                  }
                })
            .subscribe(new BiConsumer<List<GankDailyDataEntity>, Throwable>() {
              @Override public void accept(List<GankDailyDataEntity> entities, Throwable throwable)
                  throws Exception {

                if (isLoadingMore) {
                  // 添加集合 notify
                  processLoadMoreData(entities, throwable);
                  isLoadingMore = false;
                } else {
                  // 第一次加载时候发生异常，没有缓存情况下
                  if (handleFirstNetThrowable(throwable)) {
                    return;
                  }
                  processFirstSuccessData(entities);
                }
              }
            });

    register(netSubscribe);
  }

  @Override public void onLoadMore() {
    isLoadingMore = true;
    mSkipCount += Constant.MEIZI_COUNT;
    mSkipPage += 1;
    doNetGetData();
  }

  private void processFirstSuccessData(List<GankDailyDataEntity> entities) {
    mTotalEntities = entities;
    MainAdapter adapter = new MainAdapter(mTotalEntities);
    mAdapterWrapper = new AdapterWrapper(adapter);
    mAdapterWrapper.setLoadMoreView(R.layout.item_main_recyclerivew_load_more);
    mAdapterWrapper.setOnAdapterLoadMoreListener(this);
    removeSplashFragment();
    mView.setRecyclerViewAdapter(mAdapterWrapper);
    // 应用启动加载first item title
    setToolbarTitleAfterFirstLoaded();
    mView.initCardHelper();
    if (isError) {
      hideErrorLayout();
    }
    isError = false;
  }

  private void processLoadMoreData(List<GankDailyDataEntity> entities, Throwable throwable) {
    // 有异常就直接return掉
    if (throwable != null) {
      ApiExceptionHandler.handleError(throwable);
      isError = true;
      return;
    }
    // 加载更多
    mTotalEntities.addAll(entities);
    mAdapterWrapper.notifyItemRangeInserted(mTotalEntities.size() - entities.size(),
        entities.size());
    mView.stopLoadingMore();
  }

  // 添加日期
  private void processAddDateField(List<GankDailyDataEntity> dailyDataEntities,
      GankDailyTitleEntity titleEntity) {
    for (int i = 0; i < dailyDataEntities.size(); i++) {
      GankDailyDataEntity dataEntity = dailyDataEntities.get(i);
      GankDailyTitleEntity.ResultsBean resultsBean = titleEntity.getResults().get(i);
      dataEntity.setTitle(resultsBean.getTitle());
      dataEntity.setDate(resultsBean.getPublishedAt());
    }
  }

  private void setToolbarTitleAfterFirstLoaded() {
    // 第一次加载设置标题
    GankDailyDataEntity entity = mTotalEntities.get(0);
    // 获取制定日期title
    String todayTitle = entity.getTitle();
    // 获取日期
    String todayDate = entity.getResults().get福利().get(0).getPublishedAt().split("T")[0];
    mView.setToolbarTitle(todayDate + "  " + todayTitle);
  }

  // 当网络有问题处理异常
  private boolean handleFirstNetThrowable(Throwable throwable) {
    if (throwable != null) {
      if (throwable instanceof EOFException) {
        Logger.e(" EOFException");
        return false;
      }
      if (isError) {
        resetErrorLayout();
      }
      isError = true;
      mErrorView = mView.showLoadError();
      mErrorView.setOnClickListener(MainPresenter.this);
      ApiExceptionHandler.handleError(throwable);
      return true;
    }
    return false;
  }

  @Override public void onItemClick(View v, int pos, MotionEvent event) {
    float x = event.getX();
    float y = event.getY();
    int height = v.getHeight();
    float finalRadius = Math.max(Math.abs(y), Math.abs(height - y));
    //Logger.e("x：" + x + "y:" + y);
    startContentFragment(v, (int) x, (int) y, (int) finalRadius, pos);
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.imgv_header_drawer_navigationview_main:
        if (UserUtils.hasLogined(mView.getViewContext())) {
          //已经登录
          showLogoutDig();
        } else {
          //  未登录 获取头像，创建本地账户
          showLoginDig();
        }
        break;
      case R.id.inflate_id_load_error:
        // 加载失败
        setErrorLayoutProgress(v);
        // 先判断网络状态
        if (NetworkUtils.isAvailable(mView.getViewContext())) {
          doNetGetData();
        } else {
          mView.showToast(mView.getViewContext().getString(R.string.net_is_not_available));
          resetErrorLayout();
        }
        break;
      default:
        break;
    }
  }

  // toolbar menu item click
  @Override public boolean onMenuItemClick(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.about_menu_item_toolbar_main:
        // about
        FragmentSkiper.getInstance()
            .init(((FragmentActivity) mView.getViewContext()))
            .target(new AboutFragment())
            .add(android.R.id.content, true);
        break;
      case R.id.search_menu_item_toolbar_main:
        // 搜索
        FragmentSkiper.getInstance()
            .init(((FragmentActivity) mView.getViewContext()))
            .target(new SearchFragment())
            .add(android.R.id.content);
        break;
      default:
        break;
    }
    return false;
  }

  private void startSplashFragment() {
    mSplashFragment = new SplashFragment();
    FragmentSkiper.getInstance()
        .init(((FragmentActivity) mView.getViewContext()))
        .target(mSplashFragment)
        .add(android.R.id.content);
  }

  private void removeSplashFragment() {
    ((FragmentActivity) mView.getViewContext()).getSupportFragmentManager()
        .beginTransaction()
        .remove(mSplashFragment)
        .commitAllowingStateLoss();
  }

  private void startContentFragment(View v, int x, int y, int finalRadius, final int pos) {
    CircularRevealUtils.animateRevealHide(v, x, y, finalRadius)
        .addListener(new AnimatorListenerAdapter() {
          @Override public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            animation.removeListener(this);
            ContentFragment contentFragment = new ContentFragment();
            Fade fade = new Fade();
            contentFragment.setEnterTransition(fade);
            contentFragment.setAllowEnterTransitionOverlap(false);

            FragmentSkiper.getInstance()
                .init(((FragmentActivity) mView.getViewContext()))
                .target(contentFragment)
                // 滑动很快点击最后一个时候
                .putSerializable(CommonKey.OBJ1, mTotalEntities.get(pos))
                .add(android.R.id.content, true);
          }
        });
  }

  // navigationView item click listener
  @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.Android_category_menu_navigationview_main:
        //android
        startCategoryFragment(Constant.Category.ANDROID);
        break;
      case R.id.iOS_category_menu_navigationview_main:
        //ios
        startCategoryFragment(Constant.Category.IOS);
        break;
      case R.id.Web_category_menu_navigationview_main:
        //web
        startCategoryFragment(Constant.Category.WEB);
        break;
      case R.id.video_category_menu_navigationview_main:
        //video
        startCategoryFragment(Constant.Category.VIDEO);
        break;
      case R.id.suggest_category_menu_navigationview_main:
        //suggest
        startCategoryFragment(Constant.Category.SUGGEST);
        break;
      case R.id.extend_category_menu_navigationview_main:
        //extend
        startCategoryFragment(Constant.Category.EXTEND);
        break;
      case R.id.meizi_category_menu_navigationview_main:
        //meizi
        startCategoryFragment(Constant.Category.MEIZI);
        break;
      case R.id.app_category_menu_navigationview_main:
        //app
        startCategoryFragment(Constant.Category.APP);
        break;
      case R.id.collection_category_menu_navigationview_main:
        //collection
        startCollectionFragment();
        break;
      case R.id.clear_cache_category_menu_navigationview_main:
        //clear cache
        String cacheSize = CleanCatcheUtils.getCacheSize(mView.getViewContext());
        String message = String.format("发现 %s 缓存(主要是图片)，是否清理？", cacheSize);
        if (cacheSize.contains("0.00")) {
          message = "尚无缓存";
        }
        showCacheDig(message);
        break;
      default:
        break;
    }
    return false;
  }

  private void showCacheDig(String message) {
    new AlertDialog.Builder(mView.getViewContext()).setIcon(R.drawable.ic_clear_cache_128)
        .setTitle(R.string.clear_cache)
        .setMessage(message)
        .setNegativeButton("取消", null)
        .setPositiveButton("清理", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            showCleanCacheProgressDig();
            cleanCacheSync();
          }
        })
        .create()
        .show();
  }

  private void startCollectionFragment() {
    FragmentSkiper.getInstance()
        .init(((FragmentActivity) mView.getViewContext()))
        .target(new CollectionFragment())
        .add(android.R.id.content, true);
  }

  private void startCategoryFragment(@Constant.Category String category) {
    FragmentSkiper.getInstance()
        .init(((FragmentActivity) mView.getViewContext()))
        .target(new CategoryFragment())
        .putString(CommonKey.STR1, category)
        .add(android.R.id.content, true);
  }

  //异步清理缓存
  private void cleanCacheSync() {
    Observable.just(CleanCatcheUtils.clear(mView.getViewContext()))
        .compose(RxUtils.<Boolean>switchObservableSchedulers())
        .subscribe(new Consumer<Boolean>() {
          @Override public void accept(Boolean cleared) throws Exception {
            if (cleared) {
              mProgressDialog.dismiss();
            }
          }
        });
  }

  private void showCleanCacheProgressDig() {
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(mView.getViewContext());
    }
    mProgressDialog.setMessage(mView.getViewContext().getString(R.string.clear_cache_ing));
    mProgressDialog.show();
  }

  private void showLogoutDig() {
    new AlertDialog.Builder(mView.getViewContext()).setIcon(R.drawable.ic_github_200)
        .setTitle(R.string.change_account_or_not)
        .setNegativeButton("取消", null)
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            // 清理sp
            SPUtils.clear(mView.getViewContext());
            mView.resetUser();
            // 清理缓存
            showLoginDig();
          }
        })
        .create()
        .show();
  }

  // 点击errorLayout 变progress
  private void setErrorLayoutProgress(View v) {
    mImageError = v.findViewById(R.id.imgv_load_error_main_activity);
    mImageError.setVisibility(View.GONE);
    mTvError = v.findViewById(R.id.tv_load_error_main_activity);
    mTvError.setVisibility(View.GONE);
    mProgressError = v.findViewById(R.id.progressbar_load_error_main_activity);
    mProgressError.setVisibility(View.VISIBLE);
  }

  private void hideErrorLayout() {
    mErrorView.setVisibility(View.GONE);
    resetErrorLayout();
  }

  private void resetErrorLayout() {
    mImageError.setVisibility(View.VISIBLE);
    mTvError.setVisibility(View.VISIBLE);
    mProgressError.setVisibility(View.GONE);
  }

  private void showLoginDig() {
    mDialogFragment.show(((FragmentActivity) mView.getViewContext()).getSupportFragmentManager(),
        mDialogFragment.getClass().getSimpleName());
  }

  // 处理MainActivity 的back 逻辑
  @Override public boolean onBackPress() {
    Boolean interceptBackEvent = handleFragmentBackPress();
    if (interceptBackEvent != null) return interceptBackEvent;
    // 安全退出
    return safeQuitApp();
  }

  @Nullable private Boolean handleFragmentBackPress() {
    FragmentManager supportFragmentManager =
        ((FragmentActivity) mView.getViewContext()).getSupportFragmentManager();
    Fragment contentFragment = supportFragmentManager.findFragmentById(android.R.id.content);
    if (contentFragment != null && !contentFragment.isRemoving()) {
      if (contentFragment instanceof WebFragment) {
        WebFragment webFragment = (WebFragment) contentFragment;
        if (webFragment.canWebViewGoBack()) {
          return true;
        }
      }
      supportFragmentManager.beginTransaction().remove(contentFragment).commit();
      return true;
    }
    return null;
  }

  // 双击推出应用
  private boolean safeQuitApp() {
    long currentClickTime = System.currentTimeMillis();
    if (Math.abs(currentClickTime - mLastClickTime) >= 2000) {
      mLastClickTime = currentClickTime;
      mView.showToast(mView.getViewContext().getString(R.string.double_click_quit_app));
      return true;
    }
    return false;
  }

  @Override public void onDestroy() {
    // unRegister sth
    unSubscribe();
  }

  @Override public MainContract.View getView() {
    return mView;
  }

  public List<GankDailyDataEntity> getTotalEntities() {
    return mTotalEntities;
  }

  public long getSkipCount() {
    return mSkipCount;
  }
}
