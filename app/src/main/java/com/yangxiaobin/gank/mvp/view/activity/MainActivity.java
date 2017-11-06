package com.yangxiaobin.gank.mvp.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.handsome.library.T;
import com.pgyersdk.update.PgyUpdateManager;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BaseActivity;
import com.yangxiaobin.gank.common.base.BasePresenter;
import com.yangxiaobin.gank.common.bean.GitHubUserEntity;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.common.utils.BlurBitmapUtils;
import com.yangxiaobin.gank.common.utils.ImageUtils;
import com.yangxiaobin.gank.common.utils.ImmersiveStatusBarUtils;
import com.yangxiaobin.gank.common.utils.SPUtils;
import com.yangxiaobin.gank.common.utils.ViewSwitchUtils;
import com.yangxiaobin.gank.mvp.contract.MainContract;
import com.yangxiaobin.gank.mvp.presenter.MainPresenter;
import com.yangxiaobin.gank.mvp.view.listener.RecyclerViewOnScrollListener;
import com.yangxiaobin.gank.mvp.view.widget.cardgallery.CardScaleHelper;
import com.yangxiaobin.gank.mvp.view.widget.cardgallery.SpeedRecyclerView;
import com.yxb.base.utils.ConvertUtils;
import com.yxb.easy.adapter.AdapterWrapper;
import io.reactivex.functions.Consumer;
import java.lang.reflect.Field;
import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainContract.View {

  @BindView(R.id.speedrecyclerview_main_activity) SpeedRecyclerView mSpeedRecyclerView;
  @BindView(R.id.drawerlayout_main_activity) DrawerLayout mDrawerLayout;
  @BindView(R.id.linearlayout_main_activity) RelativeLayout mBackground;
  @BindView(R.id.navigation_view_main_activity) NavigationView mNavigationView;
  @BindView(R.id.toolbar_main_activity) Toolbar mToolbar;

  private ImageView mUserImageView;
  private TextView mTvUserName;
  private RecyclerViewOnScrollListener mRecyclerViewOnScrollListener;

  @Inject RealmHelper mRealmHelper;
  @Inject MainPresenter mPresenter;

  @Override protected int getLayoutResId() {
    return R.layout.activity_main_drawer;
  }

  @Override protected void initialize(Bundle bundle) {
    ButterKnife.bind(this);
    ImmersiveStatusBarUtils.makeImmersivable(this);
    mPresenter.start();
    initNavigationView();
    initToolbar();
    requestDynamicPermissions();
    //设置是否强制更新。true为强制更新；false为不强制更新（默认值)
    PgyUpdateManager.setIsForced(false);
  }

  private void initNavigationView() {
    // 让navigation menu icon 显示原有颜色
    mNavigationView.setItemIconTintList(null);
    // init header
    View headerView = mNavigationView.getHeaderView(0);
    headerView.findViewById(R.id.imgv_header_drawer_navigationview_main)
        .setOnClickListener(mPresenter);
    initUser(headerView);
    mNavigationView.setNavigationItemSelectedListener(mPresenter);
  }

  private void initUser(View headerView) {
    mUserImageView = headerView.findViewById(R.id.imgv_header_drawer_navigationview_main);
    mTvUserName = headerView.findViewById(R.id.tv_username_heaer_drawer_navigationview_main);
    // 是否登录过
    String userId = (String) SPUtils.get(mContext, Constant.KEY_USER_ID_LOGIN, "");
    if (!TextUtils.isEmpty(userId)) {
      //登录过
      GitHubUserEntity user = mRealmHelper.findUserByUserId(userId);
      ImageUtils.loadRound(mContext, user.getAvatar_url(), mUserImageView);
      mTvUserName.setText(user.getName());
    }
  }

  private void initToolbar() {
    mToolbar.inflateMenu(R.menu.menu_toolbar_main_activity);
    mToolbar.setOnMenuItemClickListener(mPresenter);
    initDrawer(mToolbar);
  }

  public void initDrawer(Toolbar toolbar) {
    ActionBarDrawerToggle drawerToggle =
        new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
          @Override public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
          }

          @Override public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
          }
        };
    drawerToggle.syncState();
    mDrawerLayout.addDrawerListener(drawerToggle);
  }

  private void requestDynamicPermissions() {
    // 检查权限
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      RxPermissions rxPermissions = new RxPermissions(this);
      rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
          .subscribe(new Consumer<Permission>() {
            @Override public void accept(Permission permission) throws Exception {
              if (permission.granted) {
                // `permission.name` is granted !
                T.info("可以保存妹子到本地啦");
              } else if (permission.shouldShowRequestPermissionRationale) {
                // Denied permission without ask never again
                // 禁止 以后不在询问
                T.info("无法保存妹子啦");
              } else {
                // Denied permission with ask never again
                // Need to go to the settings
                // 当前禁止
                T.info("无法保存妹子啦");
              }
            }
          });
    }
  }

  /**
   * 通过反射改变toolbar 样式
   *
   * @param title toolbar title
   */
  @Override public void setToolbarTitle(String title) {
    //获取类对象
    Class<?> clazz;
    try {
      clazz = mToolbar.getClass();
      Field field = clazz.getDeclaredField("mTitleTextView");//可以获取到private的对象
      field.setAccessible(true);//如果属性是私有的，那么就需要设置可访问
      TextView tvTitle = (TextView) field.get(mToolbar);
      if (tvTitle != null) {
        tvTitle.setSingleLine(false);
        tvTitle.setTextSize(ConvertUtils.sp2px(5));
        tvTitle.setPadding(0, 0, 0, 5);
      }
    } catch (IllegalAccessException e1) {
      e1.printStackTrace();
    } catch (NoSuchFieldException e1) {
      e1.printStackTrace();
    }
    mToolbar.setTitle(title);
  }

  @Override public void setUpRecyclerView(AdapterWrapper adapter) {
    mSpeedRecyclerView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
    // 滑动处理
    mRecyclerViewOnScrollListener = new RecyclerViewOnScrollListener(mPresenter);
    mSpeedRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);
    // 点击监听
    mSpeedRecyclerView.setOnItemClickListener(mPresenter);
    mSpeedRecyclerView.setAntiShake(1000);
    mSpeedRecyclerView.setAdapter(adapter);
  }

  @Override public void initCardHelper() {
    // mRecyclerView绑定scale效果
    CardScaleHelper cardScaleHelper = new CardScaleHelper();
    // 展示第一页
    cardScaleHelper.setCurrentItemPos(0);
    cardScaleHelper.attachToRecyclerView(mSpeedRecyclerView);
    // 第一次加载第一个item 渲染背景
    mRecyclerViewOnScrollListener.notifyBackgroundChange();
  }

  @Override public Context getViewContext() {
    return this;
  }

  @Override protected BasePresenter createPresenter() {
    return mPresenter;
  }

  @Override public void showToast(String msg) {
    T.info(msg);
  }

  @Override public void startSwitchBgAnim(Bitmap bitmap) {
    ViewSwitchUtils.startSwitchBackgroundAnim(this, mBackground,
        BlurBitmapUtils.getBlurBitmap(this, bitmap, 15));
  }

  @Override public void stopLoadingMore() {
    // 当前卡片向左移动
    mSpeedRecyclerView.smoothScrollBy(ConvertUtils.dp2px(150), 0);
  }

  @Override public void setUserHeadImage(String url) {
    ImageUtils.loadRound(this, url, mUserImageView);
  }

  @Override public void setUserName(String name) {
    mTvUserName.setText(name);
  }

  @Override public void resetUser() {
    mTvUserName.setText("");
    mUserImageView.setImageResource(R.drawable.ic_github_200);
  }

  @Override public View showLoadError() {
    ViewStub viewStub = findViewById(R.id.viewstub_load_error_main_activity);
    if (viewStub != null) {
      viewStub.inflate();
    }
    return findViewById(R.id.inflate_id_load_error);
  }


  @Override protected void onRestart() {
    super.onRestart();
    mPresenter.restart();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mPresenter.onDestroy();
    mRealmHelper.closeRealm();
  }

  @Override public void onBackPressed() {
    if (mPresenter.onBackPress()) {
      return;
    }
    super.onBackPressed();
  }
}
