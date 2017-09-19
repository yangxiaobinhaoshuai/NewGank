package com.yangxiaobin.gank.common.utils;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;

/**
 * Created by handsomeyang on 2017/8/19.
 */

public class RxUtils {

  private RxUtils() {

  }

  /**
   * 封装rxjava线程切换
   *
   * @param <T> 实体类型
   * @return ObservableTransformer
   */
  public static <T> ObservableTransformer<T, T> switchObservableSchedulers() {
    return new ObservableTransformer<T, T>() {
      @Override public ObservableSource<T> apply(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
      }
    };
  }

  public static <T> FlowableTransformer<T, T> switchFlowableSchedulers() {
    return new FlowableTransformer<T, T>() {
      @Override public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
      }
    };
  }

  public static <T> SingleTransformer<T, T> switchSingleSchedulers() {
    return new SingleTransformer<T, T>() {
      @Override public SingleSource<T> apply(Single<T> upstream) {

        return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
      }
    };
  }
}
