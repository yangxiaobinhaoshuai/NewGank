package com.yangxiaobin.gank.common.base;

import android.content.Context;
import com.yangxiaobin.kits.base.AbsBaseFragment;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.DispatchingAndroidInjector_Factory;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by handsomeyang on 2017/6/23.
 */

public abstract class BaseFragment extends AbsBaseFragment implements IBaseView {

  @Override public void onAttach(Context context) {
    AndroidSupportInjection.inject(this);
    super.onAttach(context);
  }
}
