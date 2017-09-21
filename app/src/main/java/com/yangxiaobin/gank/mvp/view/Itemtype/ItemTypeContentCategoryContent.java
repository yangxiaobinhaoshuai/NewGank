package com.yangxiaobin.gank.mvp.view.Itemtype;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.handsome.library.T;
import com.yangxiaobin.adapter.AdapterWrapper;
import com.yangxiaobin.adapter.SlideItemTypeDelegate;
import com.yangxiaobin.gank.App;
import com.yangxiaobin.gank.R;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.common.db.RealmHelper;
import com.yangxiaobin.gank.common.utils.ImageUtils;
import com.yangxiaobin.gank.common.utils.UserUtils;
import com.yangxiaobin.gank.mvp.view.adapter.ContentAdapter;
import com.yangxiaobin.gank.mvp.view.adapter.FlagForContentAdapter;
import com.yangxiaobin.holder.EasyViewHolder;
import java.util.List;

/**
 * Created by handsomeyang on 2017/9/13.
 */

public class ItemTypeContentCategoryContent implements SlideItemTypeDelegate<ContentItemEntity> {

  private RealmHelper mRealmHelper;
  private AdapterWrapper mAdapterWrapper;
  private ContentAdapter mAdapter;

  public ItemTypeContentCategoryContent(ContentAdapter adapter) {
    mAdapter = adapter;
  }

  public ItemTypeContentCategoryContent(AdapterWrapper adapterWrapper) {
    mAdapterWrapper = adapterWrapper;
  }

  @Override public int getItemViewLayoutId() {
    return R.layout.item_content_fragment_content;
  }

  @Override public boolean isMatchType(ContentItemEntity contentItemEntity, int pos) {
    return TextUtils.isEmpty(contentItemEntity.getTitle());
  }

  @Override
  public void bindData(Context context, EasyViewHolder holder, ContentItemEntity entity, int pos) {
    // collected or not ... must login
    if (mRealmHelper == null) {
      mRealmHelper = new RealmHelper(context);
    }
    checkForCollected(context, holder, entity);
    recordReadItems(context, holder, entity);
    initImages(context, holder, entity);
    initSlideMenu(holder, entity);
  }

  @Override public int getSlideMenuId() {
    return R.layout.slide_menu_collection;
  }

  @Override
  public void onSlide(View view, EasyViewHolder holder, ContentItemEntity entity, final int pos) {
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
          if (mAdapterWrapper != null) {
            // 如果是收藏界面要删除item的
            ContentAdapter innerAdapter = (ContentAdapter) mAdapterWrapper.getInnerAdapter();
            String fLag = innerAdapter.getFLag();
            if (!TextUtils.isEmpty(fLag) && fLag.equals(FlagForContentAdapter.COLLECTION)) {
              // 收藏列表数据
              List<ContentItemEntity> dataList = innerAdapter.getDataList();
              dataList.remove(entity);
              // FIXME: 2017/9/21  pos是不会变的
              //mAdapterWrapper.notifyItemRemoved(pos);
              mAdapterWrapper.notifyDataSetChanged();
              return;
            }
          }
          break;
        default:
          break;
      }
      mAdapter.notifyItemChanged(pos);
    }
  }

  private void initImages(Context context, EasyViewHolder holder, ContentItemEntity entity) {
    // image
    holder.setText(R.id.tv_item_content_content_fragment, entity.getDesc());
    holder.setText(R.id.tv_who_content_content_fragment, entity.getWho());
    ImageView imageView2 = holder.getView(R.id.imgv2_item_content_content_fragment);
    ImageView imageView1 = holder.getView(R.id.imgv1_item_content_content_fragment);
    List<String> images = entity.getImages();
    if (images != null && images.size() > 0) {
      // 第一张gif
      String imageUrl2 = images.get(0);
      if (!TextUtils.isEmpty(imageUrl2)) {
        imageView2.setVisibility(View.VISIBLE);
        ImageUtils.load(context, imageUrl2, imageView2);
      }
      if (images.size() > 1) {
        // 第二张gif
        String imageUrl1 = images.get(1);
        if (!TextUtils.isEmpty(imageUrl1)) {
          imageView1.setVisibility(View.VISIBLE);
          ImageUtils.load(context, imageUrl1, imageView1);
        }
      } else {
        imageView1.setVisibility(View.GONE);
      }
    } else {
      imageView1.setVisibility(View.GONE);
      imageView2.setVisibility(View.GONE);
    }
  }

  private void recordReadItems(Context context, EasyViewHolder holder, ContentItemEntity entity) {
    // 点击过改变颜色
    if (App.getINSTANCE().getItemUrls().contains(entity.getUrl())) {
      //holder.setBackgroundRes(R.id.layout_item_content_fragment, R.color.gray);
      TextView tvContent = holder.getView(R.id.tv_item_content_content_fragment);
      tvContent.setTextColor(context.getResources().getColor(R.color.gray));
    }
  }

  private void initSlideMenu(EasyViewHolder holder, ContentItemEntity entity) {
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

  private void checkForCollected(Context context, EasyViewHolder holder, ContentItemEntity entity) {
    if (UserUtils.hasLogined(context)) {
      if (mRealmHelper.findOne(entity) != null) {
        ImageView collectedImage = holder.getView(R.id.imgv_is_collected_content_framgent);
        collectedImage.setImageResource(R.drawable.ic_collected_128);
      }
    }
  }
}
