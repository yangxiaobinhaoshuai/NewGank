package com.yangxiaobin.gank.mvp.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.orhanobut.logger.Logger;
import com.yangxiaobin.Constant;
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
import com.yxb.base.CommonKey;
import com.yxb.base.utils.FragmentSkipper;
import com.yxb.base.utils.NetworkUtils;
import com.yxb.easy.adapter.AdapterWrapper;
import com.yxb.easy.listener.OnAdapterLoadMoreListener;
import com.yxb.easy.listener.OnItemClickListener;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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
    OnAdapterLoadMoreListener, DialogInterface.OnClickListener {

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
  private boolean isError;                                  // 网络是否出错
  private View mImageError;
  private View mProgressError;
  private View mErrorView;
  private AdapterWrapper mAdapterWrapper;
  private boolean isTerribleNetEnvironment;                 // 标识没有网或者移动数据的网络状态
  private AlertDialog mReadCacheDig;

  @Inject MainPresenter(MainContract.View view, MainContract.Model model) {
    mView = view;
    mModel = model;
  }

  @Override public void start() {
    startSplashFragment();
    // 为了实例化toolbar上的TextView  只要TextUtils.ieEmpty(title)返回false,所以随便set一个字符串"妹子"。
    mView.setToolbarTitle("畅小朋友");
    mDialogFragment = new LoginDialogFragment();
    registerRx2BusObserverForChangingUser();
    // 检测网络状态 wifi 就do nothing，没网或者移动数据提示用户
    if (detectNetworkStateIsWIFIOrNot()) {
      doNetGetData();
    }
  }

  private void startSplashFragment() {
    SplashFragment splashFragment = new SplashFragment();
    FragmentSkipper.getInstance()
        .init(mView.getViewContext())
        .target(splashFragment)
        .add(android.R.id.content);
  }

  private void removeSplashFragment() {
    Rx2Bus.getDefault().post(Constant.FINISH_SPLASH);
  }

  /**
   * 注册头像和用户名修改监听
   */
  private void registerRx2BusObserverForChangingUser() {
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

  /**
   * 检测网络状态
   *
   * @return true 当前是WIFI状态
   */
  private boolean detectNetworkStateIsWIFIOrNot() {
    // 判断网络状态  wifi 无作为，没有网络和移动数据提示用户
    if (!NetworkUtils.isWIFINetwork()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(mView.getViewContext());
      builder.setNegativeButton("确定", this).setPositiveButton("开启Wi-Fi", this);
      if (!NetworkUtils.isNetworkAvailable()) {
        // 没有网络
        builder.setMessage("当前没有网络连接哦").setCancelable(false);
      } else if (NetworkUtils.isMobileNetwork()) {
        // 移动网络
        builder.setMessage("当前处于移动数据网络环境下，应用内图片较多，会稍显耗费流量哦");
      }
      AlertDialog alertDialog = builder.create();
      alertDialog.show();
      isTerribleNetEnvironment = true;
      return false;
    }
    return true;
  }

  /**
   * 初始化网络状态不是wifi的提示dialog
   *
   * @param dialog dialog
   * @param which positive or negative bt
   */
  @Override public void onClick(DialogInterface dialog, int which) {
    switch (which) {
      case DialogInterface.BUTTON_POSITIVE:
        // 开启wifi 跳转系统界面
        mView.getViewContext()
            .startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        break;
      case DialogInterface.BUTTON_NEGATIVE:
        // 判断是否有网（移动数据）,有就加载，没有移除splash
        if (NetworkUtils.isMobileNetwork()) {
          doNetGetData();
        } else if (!NetworkUtils.isNetworkAvailable()) {
          doNetIfCachedOrShowError();
        }
        break;
      default:
        break;
    }
  }

  /**
   * 没有网络尝试从缓存中加载网络，如果没有缓存就 show error page
   */
  private void doNetIfCachedOrShowError() {
    // 移除splash
    removeSplashFragment();
    try {
      // 有网络缓存的话，会自动用缓存
      doNetGetData();
    } catch (Exception e) {
      e.printStackTrace();
      Logger.e("加载缓存失败：" + e);
      mView.showLoadError();
    }
  }

  /**
   * 当移动数据或者无网络状态的时候，从系统wifi界面返回应用的时候
   */
  @Override public void restart() {
    if (isTerribleNetEnvironment) {
      if (NetworkUtils.isWIFINetwork()) {
        // wifi
        showRetriedNetworkState("WiFi");
        doNetGetData();
      } else if (NetworkUtils.isMobileNetwork()) {
        // mobile
        showRetriedNetworkState("移动数据");
        doNetGetData();
      } else if (!NetworkUtils.isNetworkAvailable()) {
        // no net 移除splash
        doNetIfCachedOrShowError();
      }
    }
  }

  /**
   * 联网获取当天数据
   */
  private void doNetGetData() {
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

                if (!isLoadingMore) {
                  // 第一次加载时候发生异常，没有缓存情况下
                  if (!handleFirstNetThrowable(throwable)) {
                    processFirstSuccessData(entities);
                  }
                } else {
                  // loadMore 添加数据到已有集合 notify
                  processLoadMoreData(entities, throwable);
                  isLoadingMore = false;
                }
              }
            });

    register(netSubscribe);
  }

  /**
   * 添加日期
   */
  private void processAddDateField(List<GankDailyDataEntity> dailyDataEntities,
      GankDailyTitleEntity titleEntity) {
    for (int i = 0; i < dailyDataEntities.size(); i++) {
      GankDailyDataEntity dataEntity = dailyDataEntities.get(i);
      GankDailyTitleEntity.ResultsBean resultsBean = titleEntity.getResults().get(i);
      dataEntity.setTitle(resultsBean.getTitle());
      dataEntity.setDate(resultsBean.getPublishedAt());
    }
  }

  /**
   * 当第一次加载网络时有问题，处理异常
   */
  private boolean handleFirstNetThrowable(Throwable throwable) {
    if (throwable != null) {
      // 这个EOFException 只是流读完了继续读才触发的，把它当成一个结束的标识就行。
      // 这里调用联网触发使用缓存的时候会发生多种异常
      if (!NetworkUtils.isNetworkAvailable()) {
        // 没有网，尝试用缓存,只要是没有网路造成的异常就不予理会，不断尝试读取缓存，成功为止。
        if (mReadCacheDig == null) {
          mReadCacheDig = new AlertDialog.Builder(mView.getViewContext()).setCancelable(false)
              .setMessage("当前没有网络连接，正在读取网络缓存...")
              .create();
        }
        mReadCacheDig.show();
        doNetGetData();
        return true;
      }
      isError = true;
      // find viewStub
      mErrorView = mView.showLoadError();
      mErrorView.setOnClickListener(MainPresenter.this);
      ApiExceptionHandler.handleError(throwable);
      return true;
    }
    //解析数据成功后
    if (mReadCacheDig != null) {
      mReadCacheDig.cancel();
    }
    return false;
  }

  /**
   * 应用启动首次解析数据成功加载cards
   */
  private void processFirstSuccessData(List<GankDailyDataEntity> entities) {
    mTotalEntities = entities;
    MainAdapter adapter = new MainAdapter(mTotalEntities);
    mAdapterWrapper = new AdapterWrapper(adapter);
    mAdapterWrapper.setLoadMoreView(R.layout.item_main_recyclerivew_load_more);
    mAdapterWrapper.setOnAdapterLoadMoreListener(this);
    mView.setUpRecyclerView(mAdapterWrapper);
    // 应用启动加载first item title
    setToolbarTitleAfterFirstLoaded();
    mView.initCardHelper();
    if (isError) {
      hideErrorLayout();
    }
    isError = false;
    // 联网成功无论成功失败与否先取消splash页
    removeSplashFragment();
  }

  /**
   * 加载第一个card 对应的title
   */
  private void setToolbarTitleAfterFirstLoaded() {
    // 第一次加载设置标题
    GankDailyDataEntity entity = mTotalEntities.get(0);
    // 获取制定日期title
    String todayTitle = entity.getTitle();
    // 获取日期
    String todayDate = entity.getResults().get福利().get(0).getPublishedAt().split("T")[0];
    mView.setToolbarTitle(todayDate + "  " + todayTitle);
  }

  /**
   * 处理加载更多操作请求回来的数据
   */
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

  /**
   * cards click listener
   */
  @Override public void onItemClick(View v, int pos, MotionEvent event) {
    float x = event.getX();
    float y = event.getY();
    int height = v.getHeight();
    float finalRadius = Math.max(Math.abs(y), Math.abs(height - y));
    startContentFragment(v, (int) x, (int) y, (int) finalRadius, pos);
  }

  /**
   * navigationView header image 和 error page click listener
   */
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
        if (NetworkUtils.isNetworkAvailable()) {
          doNetGetData();
        } else {
          // 没有网络尝试用缓存
          try {
            doNetGetData();
          } catch (Exception e) {
            e.printStackTrace();
          }
          mView.showToast(mView.getViewContext().getString(R.string.net_is_not_available));
          resetErrorLayout();
        }
        break;
      default:
        break;
    }
  }

  /**
   * toolbar menu item click
   */
  @Override public boolean onMenuItemClick(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.about_menu_item_toolbar_main:
        // about
        FragmentSkipper.getInstance()
            .init(mView.getViewContext())
            .target(new AboutFragment())
            .add(android.R.id.content, true);
        break;
      case R.id.search_menu_item_toolbar_main:
        // 搜索
        FragmentSkipper.getInstance()
            .init(mView.getViewContext())
            .target(new SearchFragment())
            .add(android.R.id.content);
        break;
      default:
        break;
    }
    return false;
  }

  private void startContentFragment(View v, int x, int y, int finalRadius, final int pos) {
    CircularRevealUtils.animateRevealHide(v, x, y, finalRadius)
        .addListener(new AnimatorListenerAdapter() {
          @RequiresApi(api = Build.VERSION_CODES.KITKAT) @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            animation.removeListener(this);
            ContentFragment contentFragment = new ContentFragment();
            Fade fade = new Fade();
            contentFragment.setEnterTransition(fade);
            contentFragment.setAllowEnterTransitionOverlap(false);

            FragmentSkipper.getInstance()
                .init(mView.getViewContext())
                .target(contentFragment)
                // 滑动很快点击最后一个时候
                .putSerializable(CommonKey.OBJ1, mTotalEntities.get(pos))
                .add(android.R.id.content, true);
          }
        });
  }

  /**
   * navigationView item click listener
   *
   * @param item navigationView item
   */
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
        if (cacheSize.contains(mView.getViewContext().getString(R.string.no_cache))) {
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
    FragmentSkipper.getInstance()
        .init(mView.getViewContext())
        .target(new CollectionFragment())
        .add(android.R.id.content, true);
  }

  private void startCategoryFragment(@Constant.Category String category) {
    FragmentSkipper.getInstance()
        .init(mView.getViewContext())
        .target(new CategoryFragment())
        .putString(CommonKey.STR1, category)
        .add(android.R.id.content, true);
  }

  /**
   * 异步清理缓存
   */
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

  /**
   * 点击errorLayout 变progress
   *
   * @param v targetView
   */
  private void setErrorLayoutProgress(View v) {
    mImageError = v.findViewById(R.id.imgv_load_error_main_activity);
    mImageError.setVisibility(View.GONE);
    mProgressError = v.findViewById(R.id.progressbar_load_error_main_activity);
    mProgressError.setVisibility(View.VISIBLE);
  }

  private void hideErrorLayout() {
    mErrorView.setVisibility(View.GONE);
    resetErrorLayout();
  }

  private void resetErrorLayout() {
    mImageError.setVisibility(View.VISIBLE);
    mProgressError.setVisibility(View.GONE);
  }

  private void showLoginDig() {
    mDialogFragment.show(((FragmentActivity) mView.getViewContext()).getSupportFragmentManager(),
        mDialogFragment.getClass().getSimpleName());
  }

  /**
   * 重新获取网络后,通知获取的网络状态
   */
  private void showRetriedNetworkState(String state) {
    Toast.makeText(mView.getViewContext(), "当前是" + state + "连接", Toast.LENGTH_SHORT).show();
  }

  public List<GankDailyDataEntity> getTotalEntities() {
    return mTotalEntities;
  }

  @Override public void onDestroy() {
    // unRegister sth
    unSubscribe();
  }

  @Override public MainContract.View getView() {
    return mView;
  }

  /**
   * 处理MainActivity 的back 逻辑
   *
   * @return weather custom event
   */
  @Override public boolean onBackPress() {
    Boolean interceptBackEvent = handleFragmentBackPress();
    if (interceptBackEvent != null) {
      return interceptBackEvent;
    }
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

  /**
   * 双击推出应用
   *
   * @return 是否满足推出条件
   */
  private boolean safeQuitApp() {
    long currentClickTime = System.currentTimeMillis();
    if (Math.abs(currentClickTime - mLastClickTime) >= Constant.SAFT_QUIT_INTERVAL) {
      mLastClickTime = currentClickTime;
      mView.showToast(mView.getViewContext().getString(R.string.double_click_quit_app));
      return true;
    }
    return false;
  }

  @Override public void onLoadMore() {
    isLoadingMore = true;
    mSkipCount += Constant.MEIZI_COUNT;
    mSkipPage += 1;
    doNetGetData();
  }
}
