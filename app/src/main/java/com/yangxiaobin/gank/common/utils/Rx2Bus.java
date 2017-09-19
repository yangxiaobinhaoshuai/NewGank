package com.yangxiaobin.gank.common.utils;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.SerializedSubscriber;

/**
 * Created by handsomeyang on 2017/8/9.
 */

public class Rx2Bus {

  private static Rx2Bus sRx2Bus;
  //相当于Rxjava1.x中的Subject
  private final FlowableProcessor<Object> mBus;

  private Rx2Bus() {
    //调用toSerialized()方法，保证线程安全
    mBus = PublishProcessor.create().toSerialized();
  }

  public static Rx2Bus getDefault() {
    if (sRx2Bus == null) {
      synchronized (String.class) {
        if (sRx2Bus == null) {
          sRx2Bus = new Rx2Bus();
        }
      }
    }
    return sRx2Bus;
  }

  // 发送事件
  public void post(Object object) {
    new SerializedSubscriber<>(mBus).onNext(object);
  }

  // 接受事件
  public <T> Flowable<T> toFlowable(Class<T> aClass) {
    return mBus.ofType(aClass);
  }
}
