package com.yangxiaobin.gank.di.component;

import com.yangxiaobin.gank.mvp.view.fragment.AboutFragment;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by handsomeyang on 2017/8/26.
 */

@Subcomponent public interface AboutComponent extends AndroidInjector<AboutFragment> {
  @Subcomponent.Builder abstract class Builder extends AndroidInjector.Builder<AboutFragment> {

  }
}
