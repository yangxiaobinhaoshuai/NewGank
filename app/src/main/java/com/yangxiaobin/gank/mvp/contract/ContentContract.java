package com.yangxiaobin.gank.mvp.contract;

import android.os.Bundle;
import com.yangxiaobin.gank.common.base.IBaseModel;
import com.yangxiaobin.gank.common.base.IBaseView;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.mvp.view.adapter.ContentAdapter;

/**
 * Created by handsomeyang on 2017/7/11.
 */

public interface ContentContract {

  interface Model extends IBaseModel {

  }

  interface View extends IBaseView {

    void setUpToolbar();

    void setUpToolbarTitle(String title);

    Bundle getFragmentArgument();

    void setUpRecyclerView();

    void setRecyclerViewAdapter(ContentAdapter adapter);

    void setImageViewUrl(String url);

    RealmHelper getRealmHelper();

    void removeSelf();
  }

  interface Presenter {

    void onDestroy();
  }
}
