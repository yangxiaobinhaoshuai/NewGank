package com.yangxiaobin.gank.mvp.view.adapter;

import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.bean.SearchHistoryEntity;
import com.yxb.base.utils.CommonUtils;
import com.yxb.easy.adapter.EasyAdapter;
import com.yxb.easy.holder.EasyViewHolder;
import java.util.List;

/**
 * Created by handsomeyang on 2017/8/30.
 */

public class SearchHistoryAdapter extends EasyAdapter<SearchHistoryEntity> {

  public SearchHistoryAdapter(List<SearchHistoryEntity> dataList, int layoutId) {
    super(dataList, layoutId);
  }

  @Override
  protected void bindViewHolder(EasyViewHolder holder, SearchHistoryEntity entity, int pos) {
    holder.setText(R.id.tv_search_key_word_item_search_history, entity.getContent());
    holder.setText(R.id.tv_search_time_item_search_history,
        CommonUtils.formatTime(entity.getSearchTime(), "yyyy-MM-dd HH:mm:ss"));
  }
}
