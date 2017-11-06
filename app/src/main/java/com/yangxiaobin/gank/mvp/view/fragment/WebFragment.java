package com.yangxiaobin.gank.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.handsome.library.T;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.mvp.view.activity.LandscapeVideoActivity;
import com.yxb.base.utils.NetworkUtils;
import java.lang.ref.WeakReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment
    implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

  private static final String TAG = "WebFragment";
  private WebView mWebView;
  private View mRootView;
  private String mTitle;
  private String mUrl;

  @SuppressLint("StaticFieldLeak") private static Toolbar sToolbar;
  @SuppressLint("StaticFieldLeak") private static ProgressBar sProgressBar;

  public WebFragment setUrl(String url) {
    if (!TextUtils.isEmpty(url)) {
      mUrl = url;
    } else {
      Log.e(TAG, "WebView网址不可用: " + url);
    }
    return this;
  }

  public WebFragment setTitle(@NonNull String title) {
    mTitle = title;
    return this;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    mRootView = inflater.inflate(R.layout.fragment_web, container, false);
    initialize();
    return mRootView;
  }

  private void initialize() {
    createWebView();
    initToolbar();
    sProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressbar);
    initWebViewConfig();
    initWebSettings();
    // webView load url
    if (!TextUtils.isEmpty(mUrl)) {
      if (NetworkUtils.isNetworkAvailable()) {
        mWebView.loadUrl(mUrl);
      } else {
        T.error(getContext().getString(R.string.net_is_not_available));
      }
    }
  }

  private void createWebView() {
    // 不在xml中创建可以防止内存泄漏
    RelativeLayout.LayoutParams params =
        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    mWebView = new WebView(getActivity().getApplicationContext());
    params.addRule(RelativeLayout.BELOW, R.id.progressbar);
    mWebView.setLayoutParams(params);
    ((ViewGroup) mRootView).addView(mWebView);
  }

  private void initToolbar() {
    sToolbar = mRootView.findViewById(R.id.toolbar_web_fragment);
    sToolbar.setNavigationIcon(R.drawable.ic_left_arraw_128);
    sToolbar.inflateMenu(R.menu.menu_web_fragment);
    sToolbar.setOnMenuItemClickListener(this);
    sToolbar.setNavigationOnClickListener(this);
  }

  @Override public boolean onMenuItemClick(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.copy_link_menu_item_web_fragment:
        ClipboardManager cm =
            (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        ClipData url = ClipData.newPlainText("webUrl", mUrl);
        assert cm != null;
        cm.setPrimaryClip(url);
        T.info("成功复制URL到剪切板");
        break;
      case R.id.open_in_browser_menu_item_web_fragment:
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri webUrl = Uri.parse(mUrl);
        intent.setData(webUrl);
        startActivity(intent);
        break;
      case R.id.share_menu_item_web_fragment:
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //分享文本
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
        startActivity(Intent.createChooser(shareIntent, "分享"));
        break;
      default:
        break;
    }
    return false;
  }

  public static Toolbar getToolbar() {
    if (sToolbar != null) {
      return sToolbar;
    } else {
      throw new RuntimeException("u should call this in webFragment onCreate");
    }
  }

  /**
   * webview load url
   *
   * @param url url
   */
  public void webViewLoadUrl(String url) {
    if (!TextUtils.isEmpty(url)) {
      mWebView.loadUrl(url);
    }
  }

  private void initWebViewConfig() {
    //支持获取手势焦点，输入用户名、密码或其他
    mWebView.requestFocusFromTouch();
    mWebView.setWebViewClient(new MyWebViewClient());
    mWebView.setWebChromeClient(new MyChromeWebViewClient());
  }

  /**
   * init websettings
   */
  @SuppressLint("SetJavaScriptEnabled") private void initWebSettings() {
    WebSettings webSettings = mWebView.getSettings();
    // 支持js
    webSettings.setJavaScriptEnabled(true);
    //设置自适应屏幕，两者合用
    webSettings.setUseWideViewPort(true);       //将图片调整到适合webview的大小
    webSettings.setLoadWithOverviewMode(true);  // 缩放至屏幕的大小

    webSettings.setSupportZoom(true);           //支持缩放，默认为true。是下面那个的前提。
    webSettings.setBuiltInZoomControls(true);   //设置内置的缩放控件。
    //若上面是false，则该WebView不可缩放，这个不管设置什么都不能缩放。
    webSettings.setTextZoom(2);                 //设置文本的缩放倍数，默认为 100

    webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
    webSettings.supportMultipleWindows();        //多窗口
    webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
    webSettings.setAllowFileAccess(true);        //设置可以访问文件
    webSettings.setNeedInitialFocus(true);       //当webview调用requestFocus时为webview设置节点
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
    webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
    webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

    webSettings.setStandardFontFamily("");        //设置 WebView 的字体，默认字体为 "sans-serif"
    webSettings.setDefaultFontSize(20);           //设置 WebView 字体的大小，默认大小为 16
    webSettings.setMinimumFontSize(12);           //设置 WebView 支持的最小字体大小，默认为 8
  }

  @Override public void onResume() {
    super.onResume();
    if (mWebView != null) {
      mWebView.onResume();
    }
  }

  @Override public void onPause() {
    super.onPause();
    if (mWebView != null) {
      mWebView.onPause();
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mWebView != null) {
      mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
      mWebView.clearHistory();
      ((ViewGroup) mWebView.getParent()).removeView(mWebView);
      mWebView.destroy();
      mWebView = null;
    }
  }

  @Override public void onClick(View v) {
    if (mWebView != null && mWebView.canGoBack()) {
      mWebView.goBack();
      return;
    }
    if (getActivity() instanceof LandscapeVideoActivity) {
      getActivity().finish();
      return;
    }
    if (getFragmentManager() != null) {
      getFragmentManager().beginTransaction().remove(this).commit();
    }
  }

  private static class MyWebViewClient extends WebViewClient {
    private boolean isFristLink;

    //不调用浏览器，自己打开网页
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
      view.loadUrl(request.getUrl().toString());
      return super.shouldOverrideUrlLoading(view, request);
    }

    @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      //必须加上，不然会不断刷新WebView界面
      if (!isFristLink) {
        isFristLink = true;
      }
    }
  }

  /**
   * WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
   */
  private static class MyChromeWebViewClient extends WebChromeClient {
    // http://www.jianshu.com/p/3fcf8ba18d7f
    //获得网页的加载进度，显示在右上角的TextView控件中
    private WeakReference<Toolbar> mToolbarWeakReference;
    private WeakReference<ProgressBar> mProgressBarWeakReference;

    private MyChromeWebViewClient() {
      mToolbarWeakReference = new WeakReference<>(sToolbar);
      mProgressBarWeakReference = new WeakReference<>(sProgressBar);
    }

    @Override public void onProgressChanged(WebView view, int newProgress) {
      if (newProgress < 100) {
        mProgressBarWeakReference.get().setProgress(newProgress);
      } else {
        mProgressBarWeakReference.get().setProgress(100);
        mProgressBarWeakReference.get().setVisibility(View.GONE);
      }
    }

    //获取Web页中的title用来设置自己界面中的title
    //当加载出错的时候，比如无网络，这时onReceiveTitle中获取的标题为 找不到该网页,
    //因此建议当触发onReceiveError时，不要使用获取到的title
    @Override public void onReceivedTitle(WebView view, String title) {
      mToolbarWeakReference.get().setTitle(title);
    }
  }

  public boolean canWebViewGoBack() {
    if (mWebView != null) {
      if (mWebView.canGoBack()) {
        mWebView.goBack();
        return true;
      }
    }
    return false;
  }
}
