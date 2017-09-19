package com.yangxiaobin.gank.di.scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by handsomeyang on 2017/8/14.
 */

@Qualifier @Documented @Retention(RUNTIME) public @interface LoginUsed {
}
