package com.yangxiaobin.gank.di.module;

import android.support.v4.app.Fragment;
import com.yangxiaobin.gank.di.component.LoginDialogComponent;
import com.yangxiaobin.gank.mvp.view.fragment.LoginDialogFragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Created by handsomeyang on 2017/8/14.
 */

@Module(subcomponents = LoginDialogComponent.class) public abstract class LoginDialogModule {

  @Binds @IntoMap @FragmentKey(LoginDialogFragment.class)
  abstract AndroidInjector.Factory<? extends Fragment> bind(LoginDialogComponent.Builder builder);
}
