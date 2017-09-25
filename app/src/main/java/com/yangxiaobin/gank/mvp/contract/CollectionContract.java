package com.yangxiaobin.gank.mvp.contract;

import com.yangxiaobin.gank.common.base.IBaseModel;
import com.yangxiaobin.gank.common.base.IBaseView;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yxb.easy.EasyRecyclerView;
import com.yxb.easy.adapter.AdapterWrapper;

/**
 * Created by handsomeyang on 2017/8/15.
 */

public interface CollectionContract {

  interface Model extends IBaseModel {

  }

  interface View extends IBaseView {
    void removeSelf();

    void setRecyclerViewAdapter(AdapterWrapper adapter);

    RealmHelper getRealmHelper();

    EasyRecyclerView getEmptyParent();
  }

  interface Presenter {

    void onDestroy();
  }
}
