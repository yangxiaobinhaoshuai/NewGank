package com.yangxiaobin.gank.mvp.view.adapter;

import com.yangxiaobin.adapter.EasyAdapter;
import com.yangxiaobin.gank.common.bean.SearchHistoryEntity;
import com.yangxiaobin.holder.EasyViewHolder;
import java.util.List;

/**
 * Created by handsomeyang on 2017/8/30.
 */

public class SearchHistoryAdapter extends EasyAdapter<SearchHistoryEntity> {

  public SearchHistoryAdapter(List<SearchHistoryEntity> dataList, int layoutId) {
    super(dataList, layoutId);
  }

  @Override protected void bindViewHolder(EasyViewHolder easyViewHolder,
      SearchHistoryEntity searchHistoryEntity, int i) {

  }
}
