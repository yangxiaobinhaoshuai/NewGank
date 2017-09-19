package com.yangxiaobin.gank.common.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by handsomeyang on 2017/6/30.
 */

public abstract class BasePresenter<T> {

  protected Reference<T> mViewRef;//View接口类型弱引用
  protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

  public void attachView(T view) {
    mViewRef = new WeakReference<T>(view); //建立关联
  }

  protected T getView() {
    return mViewRef.get();//获取View
  }

  public boolean isViewAttached() {//判断是否与View建立了关联
    return mViewRef != null && mViewRef.get() != null;
  }

  public void detachView() {//解除关联
    if (mViewRef != null) {
      mViewRef.clear();
      mViewRef = null;
    }
  }

  public abstract void start();

  protected void unSubscribe() {
    mCompositeDisposable.clear();
  }

  protected void register(Disposable disposable) {
    mCompositeDisposable.add(disposable);
  }
}
