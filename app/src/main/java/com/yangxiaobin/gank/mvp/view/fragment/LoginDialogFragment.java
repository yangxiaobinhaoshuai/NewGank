package com.yangxiaobin.gank.mvp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.bean.GitHubUserEntity;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.common.net.ErrorConsumer;
import com.yangxiaobin.gank.common.utils.Rx2Bus;
import com.yangxiaobin.gank.common.utils.SPUtils;
import com.yangxiaobin.gank.di.scope.LoginUsed;
import com.yangxiaobin.gank.mvp.model.remote.MainModel;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import javax.inject.Inject;
import retrofit2.Retrofit;

/**
 * Created by handsomeyang on 2017/8/14.
 */

public class LoginDialogFragment extends DialogFragment {

  @BindView(R.id.edt_account_login_github_dialog) EditText mEdtAccount;
  @BindView(R.id.til_account_login_github_dialog) TextInputLayout mTilAccount;
  @BindView(R.id.tv_login_login_github_dialog) TextView mTvLogin;
  @BindView(R.id.tv_cancel_login_github_dialog) TextView mTvCancel;
  @BindView(R.id.progressbar_login_github_dialog) ProgressBar mProgressBar;
  private Unbinder unbinder;
  @Inject @LoginUsed Retrofit mRetrofit;
  @Inject RealmHelper mRealmHelper;
  @Inject MainModel mModel;
  private Disposable mSubscribe;

  @Override public void onAttach(Context context) {
    AndroidSupportInjection.inject(this);
    super.onAttach(context);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_login_github_fragment, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    String userName = (String) SPUtils.get(getContext(), Constant.KEY_USER_NAME, "");
    if (!TextUtils.isEmpty(userName)) {
      mEdtAccount.setText(userName);
    }
    return rootView;
  }

  @OnClick({ R.id.tv_login_login_github_dialog, R.id.tv_cancel_login_github_dialog })
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.tv_login_login_github_dialog:
        String account = mEdtAccount.getText().toString().trim();
        if (checkEdts(account)) {
          mTvLogin.setVisibility(View.INVISIBLE);
          mProgressBar.setVisibility(View.VISIBLE);
          doLoginGithub(account);
        }
        break;
      case R.id.tv_cancel_login_github_dialog:
        dismiss();
        break;
    }
  }

  // login github
  private void doLoginGithub(final String account) {

    // 操作db保存数据
    mSubscribe = mModel.doGithubLogin(account).subscribe(new Consumer<GitHubUserEntity>() {
      @Override public void accept(final GitHubUserEntity entity) throws Exception {
        // 操作db保存数据
        Rx2Bus.getDefault().post(entity);
        SPUtils.put(getContext(), Constant.KEY_USER_NAME, account);
        SPUtils.put(getContext(), Constant.KEY_USER_ID_LOGIN, account);
        mRealmHelper.insertUser(entity);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTvLogin.setVisibility(View.VISIBLE);
        dismiss();
      }
    }, new ErrorConsumer());
  }

  private boolean checkEdts(String account) {
    if (TextUtils.isEmpty(account)) {
      mTilAccount.setError("账号不能为空");
      return false;
    }
    return true;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mSubscribe != null && !mSubscribe.isDisposed()) {
      mSubscribe.dispose();
    }
    mRealmHelper.closeRealm();
  }
}
