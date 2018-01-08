package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by DSI on 2017/12/29.
 */
public class SingleCreateTest {
  private static final String TAG = "$RxTest";

  @Before
  public void init() {
    L.init(
        new L.Builder()
            .stackCount(1)
            .mainTag(TAG)
            .level(L.DEBUG)
    );
    L.t("SingleTest");
  }

  @Test
  public void test() {
    Single.just("a string")
        .subscribe(new SingleObserver<String>() {
          @Override public void onSubscribe(Disposable d) {
            L.print("onSubscribe");
          }

          @Override public void onSuccess(String s) {
            L.print("result %s", s); // 回调最终结果
          }

          @Override public void onError(Throwable e) {
            L.print(e);
          }
        });
  }

}