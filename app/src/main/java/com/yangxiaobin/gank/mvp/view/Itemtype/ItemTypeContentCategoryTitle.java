package com.yangxiaobin.gank.mvp.view.Itemtype;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import com.yangxiaobin.Constant;
import com.yangxiaobin.adapter.ItemTypeDelegate;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.common.utils.CommonUtils;
import com.yangxiaobin.holder.EasyViewHolder;

/**
 * Created by handsomeyang on 2017/9/13.
 */

public class ItemTypeContentCategoryTitle implements ItemTypeDelegate<ContentItemEntity> {

  @Override public int getItemViewLayoutId() {
    return R.layout.item_content_fragment_title;
  }

  @Override public boolean isMatchType(ContentItemEntity contentItemEntity, int pos) {
    return !TextUtils.isEmpty(contentItemEntity.getTitle());
  }

  @Override
  public void bindData(Context context, EasyViewHolder holder, ContentItemEntity entity, int pos) {
    //title
    String title = entity.getTitle();
    TextView titleView = holder.getView(R.id.tv_item_title_content_fragment);
    titleView.setText(title);
    CommonUtils.setTextViewDrawableLeft(titleView, getTitleIcon(title));
  }

  private int getTitleIcon(String title) {
    switch (title) {
      case Constant.Category.ANDROID:
        return R.drawable.ic_android_128;
      case Constant.Category.IOS:
        return R.drawable.ic_ios_128;
      case Constant.Category.WEB:
        return R.drawable.ic_web_128;
      case Constant.Category.EXTEND:
        return R.drawable.ic_extend_128;
      case Constant.Category.VIDEO:
        return R.drawable.ic_video_128;
      case Constant.Category.MEIZI:
        return R.drawable.ic_the_heart_stealer_128;
      case Constant.Category.SUGGEST:
        return R.drawable.ic_recommend_128;
      case Constant.Category.APP:
        return R.drawable.ic_app_128;
      default:
        return R.drawable.ic_the_heart_stealer_128;
    }
  }
}
