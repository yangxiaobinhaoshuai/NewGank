package com.yangxiaobin.gank.di.scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Created by handsomeyang on 2017/6/28.
 */

@Qualifier @Documented @Retention(RetentionPolicy.RUNTIME) public @interface UnCatched {
}
