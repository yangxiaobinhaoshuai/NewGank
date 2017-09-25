package com.yangxiaobin.gank.mvp.view.adapter;

import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.mvp.view.Itemtype.ItemTypeCategory;
import com.yxb.easy.adapter.MultiTypeAdapter;
import java.util.List;

/**
 * Created by handsomeyang on 2017/7/25.
 */

public class CategoryAdapter extends MultiTypeAdapter<CategoryEntity.ResultsBean> {

  public CategoryAdapter(List<CategoryEntity.ResultsBean> dataList) {
    super(dataList);
    addItemViewDelegate(new ItemTypeCategory(this));
  }
}
