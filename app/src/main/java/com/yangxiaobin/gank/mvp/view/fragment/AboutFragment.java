package com.yangxiaobin.gank.mvp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.bean.GitHubUserEntity;
import com.yangxiaobin.gank.common.net.ApiService;
import com.yangxiaobin.gank.common.net.ErrorConsumer;
import com.yangxiaobin.gank.common.utils.ImageUtils;
import com.yangxiaobin.gank.common.utils.RxUtils;
import com.yangxiaobin.gank.common.utils.SPUtils;
import com.yangxiaobin.gank.di.scope.LoginUsed;
import com.yxb.base.AbsBaseFragment;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.functions.Consumer;
import javax.inject.Inject;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends AbsBaseFragment implements View.OnClickListener {

  @BindView(R.id.toolbar_about_frament) Toolbar mToolbar;
  @BindView(R.id.tv_card_about_fragment) TextView mTvAboutContent;
  @BindView(R.id.imgv_author_head_about_fragment) ImageView mImageViewAuthor;
  @BindView(R.id.tv_github_page_about_framgent) TextView mTvGithubPage;
  @Inject @LoginUsed Retrofit mRetrofit;
  private Unbinder mBind;

  @Override protected int getLayoutResId() {
    return R.layout.fragment_about;
  }

  @Override public void onAttach(Context context) {
    AndroidSupportInjection.inject(this);
    super.onAttach(context);
  }

  @Override protected void initialize(Bundle bundle) {
    mBind = ButterKnife.bind(this, mRootView);
    mToolbar.setNavigationOnClickListener(this);
    initAboutApp();
    initAboutAuthor();
    initGithubPage();
  }

  private void initGithubPage() {
    //超级链接（需要添加setMovementMethod方法附加响应）
    String githubText = "githup: https://github.com/yangxiaobinhaoshuai";
    SpannableString spannableString = new SpannableString(githubText);
    // 粗体
    spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 7,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
    // 设置超链接
    spannableString.setSpan(new URLSpan("https://github.com/yangxiaobinhaoshuai"), 7,
        githubText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    //设置超链接字体前景色 为蓝色
    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)),
        7, githubText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
    mTvGithubPage.setText(spannableString);
    // 响应超链接
    mTvGithubPage.setMovementMethod(LinkMovementMethod.getInstance());
  }

  private void initAboutAuthor() {
    String headImageUrl = (String) SPUtils.get(mContext, Constant.KEY_HAS_GOT_MY_HEAD_IMAGE, "");
    if (!TextUtils.isEmpty(headImageUrl)) {
      ImageUtils.load(mContext, headImageUrl, mImageViewAuthor);
      return;
    }
    // 获取github 头像
    mRetrofit.create(ApiService.class)
        .getUserInfo(Constant.MY_GITHUB_ACCOUNT)
        .compose(RxUtils.<GitHubUserEntity>switchObservableSchedulers())
        .subscribe(new Consumer<GitHubUserEntity>() {
          @Override public void accept(GitHubUserEntity entity) throws Exception {
            String myAvatarUrl = entity.getAvatar_url();
            if (!TextUtils.isEmpty(myAvatarUrl)) {
              SPUtils.put(mContext, Constant.KEY_HAS_GOT_MY_HEAD_IMAGE, myAvatarUrl);
              ImageUtils.load(mContext, myAvatarUrl, mImageViewAuthor);
            }
          }
        }, new ErrorConsumer());
  }

  private void initAboutApp() {
    String aboutText = "分享干货和萌妹子。"
        + "\n"
        + "\r\n功能："
        + "\r\n  · 每日干货推荐"
        + "\r\n  · 干货分类查询"
        + "\r\n  · 干货长按收藏/移除收藏"
        + "\r\n  · 用户管理"
        + "\r\n  · 下载喜欢的妹子做壁纸 :)"
        + "\r\n  · 缓存清理";

    mTvAboutContent.setText(aboutText);
    mRootView.setBackgroundResource(R.drawable.bg_dialog_fragment_login_github);
  }

  @Override public void onClick(View v) {
    getFragmentManager().popBackStack();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mBind.unbind();
  }
}
