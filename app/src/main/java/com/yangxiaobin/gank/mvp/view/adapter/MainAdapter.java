package com.yangxiaobin.gank.mvp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yangxiaobin.adapter.MultiTypeAdapter;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.bean.GankDailyDataEntity;
import com.yangxiaobin.gank.mvp.view.Itemtype.ItemTypeMainCard;
import com.yangxiaobin.gank.mvp.view.widget.cardgallery.CardAdapterHelper;
import com.yangxiaobin.holder.EasyViewHolder;
import java.util.List;

/**
 * Created by handsomeyang on 2017/7/6.
 */
// 呈现每日数据adapter
public class MainAdapter extends MultiTypeAdapter<GankDailyDataEntity> {
  private CardAdapterHelper mCardAdapterHelper;

  public MainAdapter(List<GankDailyDataEntity> dataList) {
    super(dataList);
    mCardAdapterHelper = new CardAdapterHelper();
    addItemViewDelegate(new ItemTypeMainCard());
  }

  @Override public EasyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_main_recyclerview_main_card, parent, false);
    mCardAdapterHelper.onCreateViewHolder(parent, itemView);
    return super.onCreateViewHolder(parent, viewType);
  }

  @Override public void onBindViewHolder(EasyViewHolder holder, int position) {
    super.onBindViewHolder(holder, position);
    mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
  }
}
