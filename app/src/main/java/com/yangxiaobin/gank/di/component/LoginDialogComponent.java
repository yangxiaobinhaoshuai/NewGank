package com.yangxiaobin.gank.di.component;

import com.yangxiaobin.gank.mvp.view.fragment.LoginDialogFragment;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by handsomeyang on 2017/8/14.
 */
@Subcomponent public interface LoginDialogComponent extends AndroidInjector<LoginDialogFragment> {

  @Subcomponent.Builder abstract class Builder
      extends AndroidInjector.Builder<LoginDialogFragment> {

  }
}
