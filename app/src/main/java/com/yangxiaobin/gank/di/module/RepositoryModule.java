package com.yangxiaobin.gank.di.module;

import com.yangxiaobin.gank.mvp.contract.CategoryContract;
import com.yangxiaobin.gank.mvp.contract.CollectionContract;
import com.yangxiaobin.gank.mvp.contract.ContentContract;
import com.yangxiaobin.gank.mvp.contract.MainContract;
import com.yangxiaobin.gank.mvp.contract.SearchContract;
import com.yangxiaobin.gank.mvp.model.remote.CategoryModel;
import com.yangxiaobin.gank.mvp.model.remote.CollectionMoel;
import com.yangxiaobin.gank.mvp.model.remote.ContentModel;
import com.yangxiaobin.gank.mvp.model.remote.MainModel;
import com.yangxiaobin.gank.mvp.model.remote.SearchModel;
import com.yangxiaobin.gank.mvp.view.activity.MainActivity;
import com.yangxiaobin.gank.mvp.view.fragment.CategoryFragment;
import com.yangxiaobin.gank.mvp.view.fragment.CollectionFragment;
import com.yangxiaobin.gank.mvp.view.fragment.ContentFragment;
import com.yangxiaobin.gank.mvp.view.fragment.SearchFragment;
import dagger.Binds;
import dagger.Module;

/**
 * Created by handsomeyang on 2017/7/6.
 */
@Module public abstract class RepositoryModule {

  // 表示MainContractView 由 MainActivity 提供
  @Binds abstract MainContract.View provideMainContractView(MainActivity mainActivity);

  @Binds abstract MainContract.Model provideMainContractModel(MainModel mainModel);

  // contentFragment
  @Binds abstract ContentContract.View provideContentContractView(ContentFragment contentFragment);

  @Binds abstract ContentContract.Model provideContentContractModel(ContentModel model);

  // Category fragment
  @Binds abstract CategoryContract.View provideCategoryContractView(
      CategoryFragment categoryFragment);

  @Binds abstract CategoryContract.Model provideCategoryContractModel(CategoryModel model);

  // CollectionFragment
  @Binds abstract CollectionContract.View provideCollectionContractView(
      CollectionFragment collectionFragment);

  @Binds abstract CollectionContract.Model provideCollctionContractModel(CollectionMoel model);

  // SearchFragment
  @Binds abstract SearchContract.View provideSearchContractView(SearchFragment view);

  @Binds abstract SearchContract.Model provideSearchContractModel(SearchModel model);
}
