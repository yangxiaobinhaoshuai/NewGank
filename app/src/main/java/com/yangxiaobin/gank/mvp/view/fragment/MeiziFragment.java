package com.yangxiaobin.gank.mvp.view.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.handsome.library.T;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.utils.ImageUtils;
import com.yxb.base.AbsBaseFragment;
import com.yxb.base.CommonKey;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import org.reactivestreams.Publisher;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeiziFragment extends AbsBaseFragment implements View.OnLongClickListener {

  @BindView(R.id.photoview_meizi_fragment) PhotoView mPhotoView;
  private String mMeiziUrl;
  private Unbinder mBind;

  @Override protected int getLayoutResId() {
    return R.layout.fragment_meizi;
  }

  @Override protected void initialize(Bundle bundle) {
    mBind = ButterKnife.bind(this, mRootView);
    Bundle arguments = getArguments();
    if (arguments != null) {
      mMeiziUrl = arguments.getString(CommonKey.STR1);
      ImageUtils.load(mContext, mMeiziUrl, mPhotoView);
    } else {
      T.error("获取妹子失败");
    }
    mPhotoView.setOnLongClickListener(this);
  }

  private void showSnackBar(View v) {
    Snackbar snackbar = Snackbar.make(v, "下载妹子？", Snackbar.LENGTH_SHORT);
    if (!TextUtils.isEmpty("下载")) {
      snackbar.setAction("下载", new View.OnClickListener() {
        @Override public void onClick(View v) {
          saveMeizi();
        }
      });
    }
    snackbar.show();
  }

  @Override public boolean onLongClick(View v) {
    showSnackBar(v);
    return false;
  }

  private void saveMeizi() {
    Flowable.just(mMeiziUrl)
        .subscribeOn(Schedulers.newThread())
        .flatMap(new Function<String, Publisher<File>>() {
          @Override public Publisher<File> apply(String s) throws Exception {
            return Flowable.just(Glide.with(mContext)
                .load(mMeiziUrl)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get());
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<File>() {
          @Override public void accept(File file) throws Exception {
            boolean isSuccess = ImageUtils.saveImageToGallery(mContext, file);
            if (isSuccess) {
              T.info("保存到相册成功");
            } else {
              T.error("妹子保存失败");
            }
          }
        });
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mBind.unbind();
  }
}
