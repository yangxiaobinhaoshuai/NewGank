package com.yangxiaobin.gank.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 图片背景切换动画帮助类
 *
 * Created by jameson on 9/3/16.
 */
public class ViewSwitchUtils {

  public static void startSwitchBackgroundAnim(ImageView view, Bitmap bitmap) {
    Drawable oldDrawable = view.getDrawable();
    Drawable oldBitmapDrawable;
    TransitionDrawable oldTransitionDrawable = null;
    if (oldDrawable instanceof TransitionDrawable) {
      oldTransitionDrawable = (TransitionDrawable) oldDrawable;
      oldBitmapDrawable =
          oldTransitionDrawable.findDrawableByLayerId(oldTransitionDrawable.getId(1));
    } else if (oldDrawable instanceof BitmapDrawable) {
      oldBitmapDrawable = oldDrawable;
    } else {
      oldBitmapDrawable = new ColorDrawable(0xffc2c2c2);
    }

    if (oldTransitionDrawable == null) {
      oldTransitionDrawable =
          new TransitionDrawable(new Drawable[] { oldBitmapDrawable, new BitmapDrawable(bitmap) });
      oldTransitionDrawable.setId(0, 0);
      oldTransitionDrawable.setId(1, 1);
      oldTransitionDrawable.setCrossFadeEnabled(true);
      view.setImageDrawable(oldTransitionDrawable);
    } else {
      oldTransitionDrawable.setDrawableByLayerId(oldTransitionDrawable.getId(0), oldBitmapDrawable);
      oldTransitionDrawable.setDrawableByLayerId(oldTransitionDrawable.getId(1),
          new BitmapDrawable(bitmap));
    }
    oldTransitionDrawable.startTransition(1000);
  }

  public static void startSwitchBackgroundAnim(Context context, RelativeLayout background,
      Bitmap bitmap) {

    Drawable bgDrawable = background.getBackground();
    Drawable bgBitmapDrawable;
    // layerDrawable
    TransitionDrawable bgTransitionDrawable = null;
    if (bgDrawable instanceof TransitionDrawable) {
      bgTransitionDrawable = (TransitionDrawable) bgDrawable;
      bgBitmapDrawable =
          bgTransitionDrawable.findDrawableByLayerId(bgTransitionDrawable.getId(1));
    } else if (bgDrawable instanceof BitmapDrawable) {
      bgBitmapDrawable = bgDrawable;
    } else {
      bgBitmapDrawable = new ColorDrawable(0xffc2c2c2);
    }

    if (bgTransitionDrawable == null) {
      bgTransitionDrawable = new TransitionDrawable(
          new Drawable[] { bgBitmapDrawable, new BitmapDrawable(context.getResources(), bitmap) });
      bgTransitionDrawable.setId(0, 0);
      bgTransitionDrawable.setId(1, 1);
      bgTransitionDrawable.setCrossFadeEnabled(true);
      background.setBackground(bgTransitionDrawable);
    } else {
      //@param id The layer ID to search for.
      //@param drawable The replacement {@link Drawable}.
      bgTransitionDrawable.setDrawableByLayerId(bgTransitionDrawable.getId(0), bgBitmapDrawable);
      bgTransitionDrawable.setDrawableByLayerId(bgTransitionDrawable.getId(1),
          new BitmapDrawable(context.getResources(), bitmap));
    }
    bgTransitionDrawable.startTransition(500);
  }
}
