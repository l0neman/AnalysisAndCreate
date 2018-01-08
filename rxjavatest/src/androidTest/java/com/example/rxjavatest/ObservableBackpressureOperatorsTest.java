package com.example.rxjavatest;

import android.util.Log;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by DSI on 2018/1/8.
 */
public class ObservableBackpressureOperatorsTest {
  private static final String TAG = "$RxTest";

  @Before
  public void init() {
    L.init(
        new L.Builder()
            .stackCount(1)
            .mainTag(TAG)
            .level(L.DEBUG)
    );
  }

  @Test
  public void testOnBackpressureBuffer() {
    Flowable.range(0, 10)
        .onBackpressureBuffer()
        .subscribe(new Subscriber<Integer>() {
          Subscription s;

          @Override public void onSubscribe(Subscription s) {
            this.s = s;
            s.request(1);
          }

          @Override public void onNext(Integer integer) {
            L.print(String.valueOf(integer));
            TestHelper.sleep(500); // 0 ~ 10 每隔 500 毫秒输出
            s.request(1);
          }

          @Override public void onError(Throwable t) {}

          @Override public void onComplete() {}
        });
  }

  @Test
  public void testOnBackpressureDrop() {
  }

  @Test
  public void testOnBackpressureLatest() {

  }
}