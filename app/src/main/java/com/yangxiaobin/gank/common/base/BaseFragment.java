package com.yangxiaobin.gank.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.yxb.base.AbsBaseFragment;
import dagger.android.support.AndroidSupportInjection;

/**
 * Created by handsomeyang on 2017/6/23.
 */

public abstract class BaseFragment extends AbsBaseFragment implements IBaseView {

  private Unbinder mBind;

  @Override protected void initialize(Bundle savedInstanceState) {
    mBind = ButterKnife.bind(this, mRootView);
  }


  @Override public void onAttach(Context context) {
    AndroidSupportInjection.inject(this);
    super.onAttach(context);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mBind.unbind();
  }
}
