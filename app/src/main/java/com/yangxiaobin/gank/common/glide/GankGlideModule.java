package com.yangxiaobin.gank.common.glide;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import java.io.InputStream;

/**
 * Created by handsomeyang on 2017/9/25.
 */

@GlideModule public class GankGlideModule extends AppGlideModule {

  //手动分配缓存  缓存4屏图片，默认是2
  @Override public void applyOptions(Context context, GlideBuilder builder) {
    MemorySizeCalculator calculator =
        new MemorySizeCalculator.Builder(context).setMemoryCacheScreens(4).build();
    builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
  }

  // 更换网络请求为okHttp
  @Override public void registerComponents(Context context, Glide glide, Registry registry) {
    super.registerComponents(context, glide, registry);
    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
  }
}
