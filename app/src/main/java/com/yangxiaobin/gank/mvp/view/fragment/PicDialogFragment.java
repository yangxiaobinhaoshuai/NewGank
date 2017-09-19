package com.yangxiaobin.gank.mvp.view.fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.github.chrisbanes.photoview.PhotoView;
import com.orhanobut.logger.Logger;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.utils.ImageUtils;

/**
 * Created by handsomeyang on 2017/8/16.
 */

public class PicDialogFragment extends DialogFragment {

  @BindView(R.id.imgv_content_pic_dialog_framgent) PhotoView mImageView;
  Unbinder unbinder;
  @BindView(R.id.imgv_close_pic_dialog_fragment) ImageView mCloseImage;
  private String mUrl;
  private Dialog mDialog;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_pic_fragment, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    if (!TextUtils.isEmpty(mUrl)) {
      //Logger.e("图片URL："+mUrl);
      ImageUtils.load(getContext(), mUrl, mImageView);
    }
    mDialog = getDialog();
    mDialog.setCanceledOnTouchOutside(true);
    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
    return rootView;
  }

  public void setUrl(String url) {
    mUrl = url;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick(R.id.imgv_close_pic_dialog_fragment) public void onViewClicked() {
    if (mDialog != null && mDialog.isShowing()) {
      mDialog.dismiss();
    }
  }
}
