package com.yangxiaobin.gank.di.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.BuildConfig;
import com.yangxiaobin.gank.common.net.ApiService;
import com.yangxiaobin.gank.common.utils.Unicode2ChineseUtils;
import com.yangxiaobin.gank.di.scope.Cached;
import com.yangxiaobin.gank.di.scope.LoginUsed;
import com.yangxiaobin.gank.di.scope.UnCatched;
import com.yxb.base.utils.NetworkUtils;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by handsomeyang on 2017/6/26.
 */

@Module public class ApiModule {

  // application context
  @Provides @Singleton public Context provideApplicationContext(App application) {
    return application.getApplicationContext();
  }

  @Provides @Singleton public Gson provideGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return gsonBuilder.create();
  }

  // 10M 用户缓存网络
  @Provides @Singleton Cache provideOkHttpCache(App application) {
    int cacheSize = 10 * 1024 * 1024; // 10 MiB
    //设置缓存目录
    File cacheFile = new File(application.getExternalCacheDir(), Constant.CACHE_FILE_NAME);
    return new Cache(cacheFile, cacheSize);
  }

  @Provides @Singleton HttpLoggingInterceptor provideHttpLogginInterceptor() {
    //设置log拦截器
    HttpLoggingInterceptor.Logger logger = new HttpLoggingInterceptor.Logger() {
      @Override public void log(String message) {
        Log.d("Okhttp Interceptor  " + ":", Unicode2ChineseUtils.decode(message));
      }
    };
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(logger);
    // 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
    if (BuildConfig.DEBUG) {
      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    } else {
      interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
    }
    return interceptor;
  }

  @Provides @Singleton Interceptor provideCacheInterceptor() {
    return new Interceptor() {
      Response response;

      @Override public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        //网络不可用
        if (!NetworkUtils.isNetworkAvailable()) {
          //在请求头中加入：强制使用缓存，不访问网络
          request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
          response = chain.proceed(request);
          // 无网络时，在响应头中加入：设置超时为1周
          int maxStale = 60 * 60 * 24 * 7;
          response.newBuilder()
              .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
              .build();
        } else {
          response = chain.proceed(request);
          //网络可用
          int maxAge = 0;
          // 有网络时 在响应头中加入：设置缓存超时时间0个小时
          response.newBuilder().header("Cache-Control", "public, max-age=" + maxAge).build();
        }
        return response;
      }
    };
  }

  // 带缓存client
  @Provides @Cached @Singleton OkHttpClient provideCachedOkHttpClient(
      HttpLoggingInterceptor interceptor, Interceptor cacheInterceptor, Cache cache) {
    return new OkHttpClient.Builder().addInterceptor(interceptor)
        .addInterceptor(cacheInterceptor)
        .cache(cache)
        //设置出现错误重新连接
        .retryOnConnectionFailure(true)
        //设置超时时间为15s
        .connectTimeout(Constant.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(Constant.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(Constant.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build();
  }

  // 不带缓存client
  @Provides @UnCatched @Singleton OkHttpClient provideUnCatchedOkHttpClient(
      HttpLoggingInterceptor interceptor) {
    return new OkHttpClient.Builder().addInterceptor(interceptor)
        //设置出现错误重新连接
        .retryOnConnectionFailure(true)
        //设置超时时间为15s
        .connectTimeout(Constant.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(Constant.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(Constant.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build();
  }

  // 默认使用网络缓存
  @Provides @Singleton public Retrofit provideRetrofit(@Cached OkHttpClient client) {
    return new Retrofit.Builder().client(client)
        .baseUrl(Constant.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
  }

  @Provides @Singleton @LoginUsed
  public Retrofit provideLoginRetrofit(@UnCatched OkHttpClient client) {
    return new Retrofit.Builder().client(client)
        .baseUrl(Constant.GITHUB_SERVER)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
  }

  @Provides @Singleton public ApiService provideApiService(Retrofit retrofit) {
    return retrofit.create(ApiService.class);
  }

  @Provides @Singleton public Realm provideRealm() {
    return Realm.getDefaultInstance();
  }
}
