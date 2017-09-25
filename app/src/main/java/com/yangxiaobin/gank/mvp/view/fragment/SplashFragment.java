package com.yangxiaobin.gank.mvp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ImageView;
import butterknife.BindView;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.base.BaseFragment;
import com.yangxiaobin.gank.common.utils.ImageUtils;
import com.yangxiaobin.gank.common.utils.SPUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends BaseFragment {

  @BindView(R.id.imgv_splash_fragment) ImageView mImageView;
  private Disposable mSubscribe;

  @Override protected int getLayoutResId() {
    return R.layout.fragment_splash;
  }

  @Override protected void initialize(Bundle bundle) {
    super.initialize(bundle);
    // setimage
    String splashImageUrl = (String) SPUtils.get(mContext, Constant.KEY_SPLASH_IMAGE_PATH, "");
    if (!TextUtils.isEmpty(splashImageUrl)) {
      ImageUtils.load(mContext, splashImageUrl, mImageView);
    }

    // destroy self
    mSubscribe = Observable.timer(Constant.SPLASH_FINISH_DELAY, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Long>() {
          @Override public void accept(Long aLong) throws Exception {
            getFragmentManager().beginTransaction()
                .remove(SplashFragment.this)
                .commitAllowingStateLoss();
          }
        });
  }

  @Override public Context getViewContext() {
    return mContext;
  }

  @Override public void showToast(String msg) {
    // to do nothing
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mSubscribe != null && !mSubscribe.isDisposed()) {
      mSubscribe.dispose();
    }
  }
}
