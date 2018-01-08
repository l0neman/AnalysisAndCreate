package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by DSI on 2018/1/5.
 */
public class ObservableErrorHandlingTest {

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
  public void testCatch() {
    /* onErrorReturn test */
    Observable
        .create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            throw new AssertionError("test");
          }
        })
        .onErrorReturn(new Function<Throwable, Integer>() {
          @Override public Integer apply(Throwable throwable) throws Exception { return -1; }
        })
        .subscribe(new Observer<Integer>() {
          @Override public void onSubscribe(Disposable d) {}

          @Override public void onNext(Integer integer) {
            L.print(String.valueOf(integer)); // 0 -1
          }

          @Override public void onError(Throwable e) { L.print(e); }

          @Override public void onComplete() { L.print("onComplete"); /* call this */ }
        });

    /* onErrorResumeNext test */
    Observable
        .create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            e.onNext(1);
            throw new AssertionError("test");
          }
        })
        .onErrorResumeNext(Observable.just(2, 3, 4))
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1 2 3 4
          }
        });

    /* onExceptionResumeNext test */
    Observable
        .create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            e.onNext(1);
            /* throw new AssertionError("test"); */
            throw new Exception("test");
          }
        })
        .onExceptionResumeNext(
            Observable.just(2, 3, 4))
        .subscribe(new Observer<Integer>() {
          @Override public void onSubscribe(Disposable d) {}

          @Override public void onNext(Integer integer) {
            L.print(String.valueOf(integer)); // error is Exception ? 0 1 2 3 4 : 0 1
          }

          @Override public void onError(Throwable e) {
            L.print(e); // call this if error not a Exception
          }

          @Override public void onComplete() { L.print("onComplete"); }
        });
  }

  @Test
  public void testRetry() {
    /* 0 1 0 1 error */
    Observable
        .create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            e.onNext(1);
            throw new AssertionError("test");
          }
        })
        .retry(1)
        .subscribe(new Observer<Integer>() {
          @Override public void onSubscribe(Disposable d) {}

          @Override public void onNext(Integer integer) { L.print(String.valueOf(integer)); }

          @Override public void onError(Throwable e) { L.print(e); }

          @Override public void onComplete() { L.print("onComplete"); }
        });
  }
}