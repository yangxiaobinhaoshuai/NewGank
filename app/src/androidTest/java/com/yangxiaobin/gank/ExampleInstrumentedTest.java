package com.yangxiaobin.gank;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.orhanobut.logger.Logger;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class) public class ExampleInstrumentedTest {
  @Test public void useAppContext() throws Exception {
    // Context of the app under test.
    Context appContext = InstrumentationRegistry.getTargetContext();

    //assertEquals("com.yangxiaobin.gank", appContext.getPackageName());
    Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).take(3).skip(2).subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        Logger.e(integer + "");
      }
    });
  }
}
