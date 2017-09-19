package com.yangxiaobin.gank.common.net;

import com.handsome.library.T;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.orhanobut.logger.Logger;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

/**
 * Created by handsomeyang on 2017/8/20.
 */

public class ApiExceptionHandler {
  //对应HTTP的状态码
  private static final int NOT_FOUND = 404;
  private static final int INTERNAL_SERVER_ERROR = 500;
  private static final int UNSATISFIABLE_REQUEST = 504;
  private static final int SERVICE_TEMPORARILY_UNAVAILABLE = 503;
  // message
  private static final String SOCKETTIMEOUTEXCEPTION = "网络连接超时，请检查您的网络状态，稍后重试";
  private static final String CONNECTEXCEPTION = "网络连接异常，请检查您的网络状态";
  private static final String UNKNOWNHOST_EXCEPTION_MSG = "网络异常，请检查您的网络状态";
  private static final String NOT_FOUND_EXCEPTION_MSG = "用户未找到";
  private static final String INTERNAL_SERVER_EXCEPTION_MSG = "服务器内部错误";
  private static final String UNSATISFIABLE_REQUEST_MSG = "网关超时，服务器未响应";
  private static final String SERVICE_TEMPORARILY_UNAVAILABLE_MSG = "服务器错误";

  private ApiExceptionHandler() {
  }

  public static void handleError(Throwable e) {
    if (e instanceof HttpException) {
      int code = ((HttpException) e).code();
      switch (code) {
        case NOT_FOUND:
          // 404
          T.error(NOT_FOUND_EXCEPTION_MSG);
          break;
        case INTERNAL_SERVER_ERROR:
          // 500
          T.error(INTERNAL_SERVER_EXCEPTION_MSG);
          break;
        case UNSATISFIABLE_REQUEST:
          // 504
          T.error(UNSATISFIABLE_REQUEST_MSG);
          break;
        case SERVICE_TEMPORARILY_UNAVAILABLE:
          // 503
          T.error(SERVICE_TEMPORARILY_UNAVAILABLE_MSG);
        default:
          break;
      }
    } else if (e instanceof UnknownHostException) {
      //没有网络
      T.error(UNKNOWNHOST_EXCEPTION_MSG);
    } else if (e instanceof SocketTimeoutException) {
      // 连接超时
      T.error(SOCKETTIMEOUTEXCEPTION);
    } else if (e instanceof ConnectException) {
      T.error(CONNECTEXCEPTION);
    } else if (e instanceof ParseException) {
      T.info("解析失败");
    } else {
      T.error("未知错误");
    }
    if (e != null) {
      e.printStackTrace();
      Logger.e(e.getCause() + "  " + e.getMessage() + "  " + e.toString());
    }
  }
}
