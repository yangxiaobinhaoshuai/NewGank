package com.yangxiaobin.gank.common.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by handsomeyang on 2017/7/14.
 */

public class CircularRevealUtils {

  private CircularRevealUtils() {
  }

  /**
   * show view with circular reveal animation
   *
   * @param v target view
   * @param centerX x
   * @param centerY y
   * @param finalRadius radius
   */
  public static Animator animateRevealHide(View v, int centerX, int centerY, int finalRadius) {
    Animator anim = ViewAnimationUtils.createCircularReveal(v, centerX, centerY, 0, finalRadius);
    //viewRoot.setVisibility(View.VISIBLE);
    anim.setDuration(300);
    anim.setInterpolator(new AccelerateInterpolator());
    anim.start();
    return anim;
  }

  /**
   * show view with circular reveal animation
   *
   * @param viewRoot target view
   */
  public static Animator animateRevealShow(View viewRoot) {
    int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
    int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
    int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

    Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
    //viewRoot.setVisibility(View.VISIBLE);
    anim.setDuration(300);
    anim.setInterpolator(new AccelerateInterpolator());
    anim.start();
    return anim;
  }

  /**
   * hide view with circular reveal animation
   *
   * @param viewRoot target vsiew
   */
  public static Animator animateRevealHide(final View viewRoot) {
    int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
    int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
    int initialRadius = viewRoot.getWidth();

    Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
    //anim.addListener(new AnimatorListenerAdapter() {
    //  @Override public void onAnimationEnd(Animator animation) {
    //    super.onAnimationEnd(animation);
    //    viewRoot.setVisibility(View.INVISIBLE);
    //  }
    //});
    anim.setDuration(300);
    anim.start();
    return anim;
  }
}
