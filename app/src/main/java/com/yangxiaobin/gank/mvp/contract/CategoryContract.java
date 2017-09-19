package com.yangxiaobin.gank.mvp.contract;

import android.os.Bundle;
import com.yangxiaobin.gank.common.base.IBaseModel;
import com.yangxiaobin.gank.common.base.IBaseView;
import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.mvp.view.adapter.CategoryAdapter;
import io.reactivex.Flowable;

/**
 * Created by handsomeyang on 2017/7/25.
 */

public interface CategoryContract {

  interface Model extends IBaseModel {

    Flowable<CategoryEntity> getSomeCategory(String category, int dataCount, int pageCount);
  }

  interface View extends IBaseView {

    RealmHelper getRealmHelper();

    void setRecyclerViewAdapter(CategoryAdapter adapter);

    void setToolbarTitle(String title);

    Bundle getFragmentBundle();

    void stopRefresh();

    void scrollRecyclerViewToPos(int pos);

    void removeSelf();
  }

  interface Presenter {

    void onDestroy();
  }
}
