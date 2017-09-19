package com.yangxiaobin.gank.di.module;

import android.support.v4.app.Fragment;
import com.yangxiaobin.gank.di.component.AboutComponent;
import com.yangxiaobin.gank.mvp.view.fragment.AboutFragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Created by handsomeyang on 2017/8/26.
 */

@Module(subcomponents = AboutComponent.class) public abstract class AboutModule {

  @Binds @IntoMap @FragmentKey(AboutFragment.class)
  abstract AndroidInjector.Factory<? extends Fragment> bind(AboutComponent.Builder builder);
}
