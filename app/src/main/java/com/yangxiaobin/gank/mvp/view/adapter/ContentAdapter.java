package com.yangxiaobin.gank.mvp.view.adapter;

import com.yangxiaobin.adapter.MultiTypeAdapter;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.mvp.view.Itemtype.ItemTypeContentCategoryContent;
import com.yangxiaobin.gank.mvp.view.Itemtype.ItemTypeContentCategoryTitle;
import java.util.List;

/**
 * Created by handsomeyang on 2017/7/11.
 */

public class ContentAdapter extends MultiTypeAdapter<ContentItemEntity> {
  // 共用adapter 标识
  private String mFLag;

  public ContentAdapter(List<ContentItemEntity> dataList) {
    super(dataList);
    addItemViewDelegate(new ItemTypeContentCategoryTitle());
    addItemViewDelegate(new ItemTypeContentCategoryContent(this));
  }

  public ContentAdapter(List<ContentItemEntity> dataList, String FLag) {
    super(dataList);
    mFLag = FLag;
  }

  public String getFLag() {
    return mFLag;
  }
}
