package com.yangxiaobin.gank.mvp.view.Itemtype;

import android.content.Context;
import android.widget.ImageView;
import com.yangxiaobin.adapter.ItemTypeDelegate;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.bean.GankDailyDataEntity;
import com.yangxiaobin.gank.common.utils.ImageUtils;
import com.yangxiaobin.holder.EasyViewHolder;

/**
 * Created by handsomeyang on 2017/9/13.
 */

public class ItemTypeMainCard implements ItemTypeDelegate<GankDailyDataEntity> {

  @Override public int getItemViewLayoutId() {
    return R.layout.item_main_recyclerview_main_card;
  }

  @Override public boolean isMatchType(GankDailyDataEntity gankDailyDataEntity, int pos) {
    return true;
  }

  @Override public void bindData(Context context, EasyViewHolder holder, GankDailyDataEntity entity,
      int pos) {
    ImageView meizi = holder.getView(R.id.imgv_item_main_recyclerview);
    //Logger.e("妹子url：" + entity.getResults().get福利().get(0).getUrl());
    String url = entity.getResults().get福利().get(0).getUrl();
    ImageUtils.load(context, url, meizi);
  }
}
