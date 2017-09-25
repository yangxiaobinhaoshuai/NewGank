package com.yangxiaobin.gank.common.base;

import android.os.Bundle;
import butterknife.ButterKnife;
import com.yxb.base.AbsBaseActivity;
import dagger.android.AndroidInjection;

/**
 * Created by handsomeyang on 2017/6/23.
 */

public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AbsBaseActivity {

  protected T mPresenter;//Presenter对象


  @SuppressWarnings("unchecked") @Override protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);
    mPresenter = createPresenter();//创建Presenter
    mPresenter.attachView((V) this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mPresenter.detachView();
  }

  protected abstract T createPresenter();
}
