package com.yangxiaobin.gank.mvp.view.Itemtype;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.handsome.library.T;
import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.bean.CategoryEntity;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.common.utils.ImageUtils;
import com.yangxiaobin.gank.common.utils.UserUtils;
import com.yangxiaobin.gank.mvp.view.adapter.CategoryAdapter;
import com.yxb.easy.adapter.SlideItemTypeDelegate;
import com.yxb.easy.holder.EasyViewHolder;
import java.util.List;

/**
 * Created by handsomeyang on 2017/9/13.
 */

public class ItemTypeCategory implements SlideItemTypeDelegate<CategoryEntity.ResultsBean> {

  private RealmHelper mRealmHelper;
  private CategoryAdapter mAdapter;

  public ItemTypeCategory(CategoryAdapter adapter) {
    mAdapter = adapter;
  }

  @Override public int getItemViewLayoutId() {
    return R.layout.item_content_fragment_content;
  }

  @Override public boolean isMatchType(CategoryEntity.ResultsBean resultsBean, int pos) {
    return true;
  }

  @Override
  public void bindData(Context context, EasyViewHolder holder, CategoryEntity.ResultsBean entity,
      int pos) {
    if (mRealmHelper == null) {
      mRealmHelper = new RealmHelper(context);
    }
    //content
    //是否收藏过  先判断是否登录
    if (UserUtils.hasLogined(context)) {
      if (mRealmHelper.findOne(entity) != null) {
        ((ImageView) holder.getView(R.id.imgv_is_collected_content_framgent)).setImageResource(
            R.drawable.ic_collected_128);
      }
    }
    if (App.getINSTANCE().getItemUrls().contains(entity.getUrl())) {
      //holder.setBackgroundRes(R.id.layout_item_content_fragment,R.color.gray);
      TextView tvContent = holder.getView(R.id.tv_item_content_content_fragment);
      tvContent.setTextColor(context.getResources().getColor(R.color.gray));
    }
    holder.setText(R.id.tv_item_content_content_fragment, entity.getDesc());
    holder.setText(R.id.tv_who_content_content_fragment, entity.getWho());
    holder.setVisible(R.id.tv_when_content_content_fragment, View.VISIBLE);
    holder.setText(R.id.tv_when_content_content_fragment, entity.getPublishedAt().split("T")[0]);
    ImageView imageView1 = holder.getView(R.id.imgv1_item_content_content_fragment);
    ImageView imageView2 = holder.getView(R.id.imgv2_item_content_content_fragment);
    List<String> images = entity.getImages();
    if (entity.getImages() != null && images.size() > 0) {
      // 第一张gif
      String imageUrl2 = images.get(0);
      if (!TextUtils.isEmpty(imageUrl2)) {
        imageView2.setVisibility(View.VISIBLE);
        ImageUtils.load(context, imageUrl2, imageView2);
      }
      if (entity.getImages().size() > 1) {
        // 第二张gif
        String imageUrl1 = entity.getImages().get(1);
        if (!TextUtils.isEmpty(imageUrl1)) {
          imageView1.setVisibility(View.VISIBLE);
          Glide.with(context).load(imageUrl1).into(imageView1);
          ImageUtils.load(context, imageUrl1, imageView1);
        }
      } else {
        imageView1.setVisibility(View.GONE);
      }
    } else {
      imageView1.setVisibility(View.GONE);
      imageView2.setVisibility(View.GONE);
    }
    // 初始化侧滑菜单
    View tvAdd = holder.getItemView().findViewById(R.id.tv_add_collection_menu_slide);
    View tvRemove = holder.getItemView().findViewById(R.id.tv_remove_collection_menu_slide);
    if (hasInsertedBefore(entity)) {
      tvAdd.setVisibility(View.GONE);
      tvRemove.setVisibility(View.VISIBLE);
    } else {
      tvAdd.setVisibility(View.VISIBLE);
      tvRemove.setVisibility(View.GONE);
    }
  }

  // 查询是否添加过
  private boolean hasInsertedBefore(ContentItemEntity entity) {
    return mRealmHelper.findOne(entity) != null;
  }

  @Override public int getSlideMenuId() {
    return R.layout.slide_menu_collection;
  }

  @Override public void onSlide(View view, EasyViewHolder holder, CategoryEntity.ResultsBean entity,
      int pos) {
    Context context = view.getContext();
    if (!UserUtils.hasLogined(context)) {
      T.info(context.getString(R.string.please_login_frist));
    } else {
      // 登录过
      switch (view.getId()) {
        case R.id.tv_add_collection_menu_slide:
          mRealmHelper.insert(entity);
          break;
        case R.id.tv_remove_collection_menu_slide:
          mRealmHelper.delete(entity);
          break;
        default:
          break;
      }
      mAdapter.notifyItemChanged(pos);
    }
  }
}
