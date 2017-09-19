package com.yangxiaobin.gank.di.component;

import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.di.module.AboutModule;
import com.yangxiaobin.gank.di.module.ApiModule;
import com.yangxiaobin.gank.di.module.AppModule;
import com.yangxiaobin.gank.di.module.LoginDialogModule;
import com.yangxiaobin.gank.di.module.RepositoryModule;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import javax.inject.Singleton;

/**
 * Created by handsomeyang on 2017/6/26.
 */
@Singleton @Component(modules = {
    AndroidInjectionModule.class,
    AndroidSupportInjectionModule.class,
    AppModule.class,
    ApiModule.class,
    RepositoryModule.class,
    LoginDialogModule.class,
    AboutModule.class,
}) public interface AppComponent extends AndroidInjector<App> {

  @Component.Builder abstract class Builder extends AndroidInjector.Builder<App> {

  }
}
