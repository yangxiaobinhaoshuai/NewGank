package com.yangxiaobin.gank.common.net;

import io.reactivex.functions.Consumer;

/**
 * Created by handsomeyang on 2017/8/21.
 */

public class ErrorConsumer implements Consumer<Throwable> {
  @Override public void accept(Throwable throwable) throws Exception {
    ApiExceptionHandler.handleError(throwable);
  }
}
