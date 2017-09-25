package com.yangxiaobin.gank.common.glide;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import java.io.InputStream;

/**
 * Created by handsomeyang on 2017/9/25.
 */

@GlideModule public class GankGlideModule extends AppGlideModule {

  @Override public void registerComponents(Context context, Glide glide, Registry registry) {
    super.registerComponents(context, glide, registry);
    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
  }
}
