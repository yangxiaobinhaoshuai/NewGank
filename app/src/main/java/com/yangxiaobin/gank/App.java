package com.yangxiaobin.gank;

import android.support.v4.app.Fragment;
import com.handsome.library.T;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.pgyersdk.crash.PgyCrashManager;
import com.yangxiaobin.gank.common.bean.GankDailyDataEntity;
import com.yangxiaobin.gank.common.utils.RxUtils;
import com.yangxiaobin.gank.di.component.DaggerAppComponent;
import com.yxb.base.utils.CommonUtils;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/6/23.
 */

public class App extends DaggerApplication implements HasSupportFragmentInjector {

  @Inject DispatchingAndroidInjector<Fragment> mFragmentSupportInjector;

  // 保存item点击过的url
  private List<String> mItemUrls = new ArrayList<>();
  private static App sINSTANCE;

  @Override public void onCreate() {
    super.onCreate();
    initialize();
  }

  public static App getINSTANCE() {
    return sINSTANCE;
  }

  public List<String> getItemUrls() {
    return mItemUrls;
  }

  private void initialize() {
    sINSTANCE = this;
    CommonUtils.init(this);
    // doc: https://github.com/orhanobut/logger
    Logger.addLogAdapter(new AndroidLogAdapter());
    T.init(this);
    // the realm file will be located in Context.getFilesDir() with the name "realm.default"
    Realm.init(this);
    RealmConfiguration configuration =
        new RealmConfiguration.Builder().name("gank.realm").deleteRealmIfMigrationNeeded().build();
    Realm.setDefaultConfiguration(configuration);
    PgyCrashManager.register(this);
  }

  @Override protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    return DaggerAppComponent.builder().create(this);
  }

  @Override public AndroidInjector<Fragment> supportFragmentInjector() {
    return mFragmentSupportInjector;
  }
}
