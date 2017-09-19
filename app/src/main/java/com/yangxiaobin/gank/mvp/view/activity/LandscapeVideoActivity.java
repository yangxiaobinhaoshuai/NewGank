package com.yangxiaobin.gank.mvp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.handsome.library.T;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.mvp.view.fragment.WebFragment;
import com.yangxiaobin.kits.base.CommonKey;
import com.yangxiaobin.kits.base.FragmentSkiper;

public class LandscapeVideoActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_landscape_video);
    initialize();
  }

  private void initialize() {
    Intent intent = getIntent();
    String url = intent.getStringExtra(CommonKey.STR1);
    String title = intent.getStringExtra(CommonKey.STR2);
    if (!TextUtils.isEmpty(url)) {
      WebFragment webFragment = new WebFragment();
      FragmentSkiper.getInstance()
          .init(this)
          .target(webFragment.setUrl(url).setTitle(title))
          .add(R.id.framelayout_landscape_video_activity);
    } else {
      T.info("视频地址解析错误");
    }
  }

  @Override public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    // 还原web toolbar
    RelativeLayout.LayoutParams params =
        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    Toolbar toolbar = WebFragment.getToolbar();
    toolbar.setPadding(0, 0, 0, 0);
    toolbar.setLayoutParams(params);
  }
}
