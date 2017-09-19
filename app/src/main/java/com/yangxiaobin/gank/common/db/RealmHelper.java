package com.yangxiaobin.gank.common.db;

import android.content.Context;
import android.text.TextUtils;
import com.handsome.library.T;
import com.yangxiaobin.Constant;
import com.yangxiaobin.gank.common.bean.CollectionEntity;
import com.yangxiaobin.gank.common.bean.ContentItemEntity;
import com.yangxiaobin.gank.common.bean.GitHubUserEntity;
import com.yangxiaobin.gank.common.bean.RealmString;
import com.yangxiaobin.gank.common.bean.SearchHistoryEntity;
import com.yangxiaobin.gank.common.utils.SPUtils;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by handsomeyang on 2017/7/28.
 */

public class RealmHelper {

  private Realm mRealm;
  private String mUserid;

  @Inject public RealmHelper(Context context) {
    mUserid = ((String) SPUtils.get(context, Constant.KEY_USER_ID_LOGIN, ""));
  }

  private Realm getRealm() {
    if (mRealm == null) {
      mRealm = Realm.getDefaultInstance();
    }
    return mRealm;
  }

  private boolean hasLogined() {
    return !TextUtils.isEmpty(mUserid);
  }

  public void insertUser(final GitHubUserEntity entity) {
    getRealm().executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.copyToRealm(entity);
      }
    });
  }

  public void insert(ContentItemEntity entity) {
    if (!hasLogined()) {
      return;
    }
    final CollectionEntity collection = new CollectionEntity();
    collection.setWho(entity.getWho());
    collection.setDesc(entity.getDesc());
    collection.setPublishedAt(entity.getPublishedAt());
    collection.setUrl(entity.getUrl());
    collection.setTitle(entity.getTitle());
    collection.setType(entity.getType());
    collection.setUserId(mUserid);
    List<String> images = entity.getImages();
    RealmList<RealmString> realmStrings = new RealmList<>();
    if (images != null) {
      for (String imageUrl : images) {
        RealmString realmString = new RealmString();
        realmString.setString(imageUrl);
        realmStrings.add(realmString);
      }
    }
    collection.setImages(realmStrings);
    getRealm().executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.copyToRealm(collection);
        T.info("添加收藏成功!");
      }
    });
  }

  // 根据url 来删除
  public void delete(ContentItemEntity entity) {
    if (!hasLogined()) {
      return;
    }
    final String url = entity.getUrl();
    getRealm().executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.where(CollectionEntity.class)
            .equalTo("userId", mUserid)
            .equalTo("url", url)
            .findFirst()
            .deleteFromRealm();
        T.info("删除收藏成功!");
      }
    });
  }

  // 根据url来查找
  public CollectionEntity findOne(ContentItemEntity entity) {
    if (!hasLogined()) {
      return null;
    }
    return getRealm().where(CollectionEntity.class)
        .equalTo("userId", mUserid)
        .equalTo("url", entity.getUrl())
        .findFirst();
  }

  public List<ContentItemEntity> getAllSortedConentItemEntities() {
    if (!hasLogined()) {
      return null;
    }
    // 按照类别排序
    RealmResults<CollectionEntity> all = getRealm().where(CollectionEntity.class)
        .equalTo("userId", mUserid)
        .findAll()
        .sort("type", Sort.ASCENDING);
    List<ContentItemEntity> entities = new ArrayList<>();
    ContentItemEntity titleEntity = null;
    String lastEntityType = "";
    for (CollectionEntity collection : all) {
      String type = collection.getType();
      boolean equals = lastEntityType.equals(type);
      if (!equals) {
        // 和上一次标题不一样
        titleEntity = new ContentItemEntity();
        titleEntity.setTitle(type);
        lastEntityType = type;
      }
      ContentItemEntity itemEntity = new ContentItemEntity();
      itemEntity.setTitle(collection.getTitle());
      itemEntity.setDesc(collection.getDesc());
      itemEntity.setPublishedAt(collection.getPublishedAt());
      itemEntity.setWho(collection.getWho());
      itemEntity.setUrl(collection.getUrl());
      itemEntity.setType(type);
      RealmList<RealmString> realmStrings = collection.getImages();
      List<String> normalStrings = new ArrayList<>();
      for (RealmString realmString : realmStrings) {
        normalStrings.add(realmString.getString());
      }
      itemEntity.setImages(normalStrings);
      // add title
      if (!equals) {
        entities.add(titleEntity);
      }
      // add item
      entities.add(itemEntity);
    }
    return entities;
  }

  // 根据登录账号找到用户头像和姓名
  public GitHubUserEntity findUserByUserId(String userId) {
    if (!hasLogined()) {
      return null;
    }
    return getRealm().where(GitHubUserEntity.class).equalTo("login", userId).findFirst();
  }

  /**
   * 添加搜索历史记录
   *
   * @param content 搜索内容
   * @param currentTime 搜索时间
   */
  public boolean insertSearchHistory(String content, long currentTime) {
    if (!TextUtils.isEmpty(content)) {
      final SearchHistoryEntity entity = new SearchHistoryEntity();
      entity.setContent(content);
      entity.setSearchTime(currentTime);
      getRealm().executeTransaction(new Realm.Transaction() {
        @Override public void execute(Realm realm) {
          realm.copyToRealmOrUpdate(entity);
        }
      });
      return true;
    }
    return false;
  }

  // 返回所有历史记录  默认降序
  public RealmResults<SearchHistoryEntity> getAllSearchHistory() {

    return getRealm().where(SearchHistoryEntity.class)
        .findAll()
        .sort("searchTime", Sort.DESCENDING);
  }

  // 删除某条历史记录
  public void deleteSomeHistory(final String content) {
    getRealm().executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.where(SearchHistoryEntity.class)
            .equalTo("content", content)
            .findFirst()
            .deleteFromRealm();
      }
    });
  }

  /**
   * close realm
   */
  public void closeRealm() {
    if (mRealm != null && !mRealm.isClosed()) {
      mRealm.close();
    }
  }
}
