package com.yangxiaobin.gank.di.module;

import com.yangxiaobin.gank.mvp.view.activity.MainActivity;
import com.yangxiaobin.gank.mvp.view.fragment.CategoryFragment;
import com.yangxiaobin.gank.mvp.view.fragment.CollectionFragment;
import com.yangxiaobin.gank.mvp.view.fragment.ContentFragment;
import com.yangxiaobin.gank.mvp.view.fragment.SearchFragment;
import com.yangxiaobin.gank.mvp.view.fragment.SplashFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by handsomeyang on 2017/7/6.
 * abstract @Binds 和 @Multibinds 要用static
 */

@Module public abstract class AppModule {

  // 自动生成module 和 component
  @ContributesAndroidInjector abstract MainActivity contributesMainActivity();

  // Splash Fragment
  @ContributesAndroidInjector abstract SplashFragment contributesSplashFragment();

  // Content Fragment
  @ContributesAndroidInjector abstract ContentFragment contributesGankContentFragment();

  // category Activity
  @ContributesAndroidInjector abstract CategoryFragment contributesCategoryFragment();

  // collection Fragment
  @ContributesAndroidInjector abstract CollectionFragment contributesCollectionFragment();

  // searchFragment

  @ContributesAndroidInjector abstract SearchFragment contributesSearchFragment();
}
