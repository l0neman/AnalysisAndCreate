package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

/**
 * Created by DSI on 2017/12/29.
 */
public class SubjectCreateTest {

  private static final String TAG = "$RxTest";

  @Before
  public void init() {
    L.init(
        new L.Builder()
            .stackCount(1)
            .mainTag(TAG)
            .level(L.DEBUG)
    );
    L.t("SubjectCreateTest");
  }

  @Test
  public void testAsyncSubject() {
    final AsyncSubject<Integer> subject = AsyncSubject.create();

    subject.onNext(0);
    subject.onNext(1);
    subject.onNext(2);
    subject.onComplete();

    subject.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer));
      }
    });

    /* error test  */
    final AsyncSubject<Integer> error = AsyncSubject.create();

    error.onNext(0);
    error.onNext(1);
    error.onNext(2);

    error.onError(TestHelper.error(""));

    error.subscribe(new Observer<Integer>() {
      @Override public void onSubscribe(Disposable d) {}

      @Override public void onNext(Integer integer) {}

      @Override public void onError(Throwable e) {
        L.print(e);
      }

      @Override public void onComplete() {}
    });
  }


  @Test
  public void testBehaviorSubject() {
    BehaviorSubject<Integer> subject = BehaviorSubject.createDefault(-1);
    subject.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // -1 0 1 2
      }
    });
    subject.onNext(0);
    subject.onNext(1);
    subject.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // 1 2
      }
    });
    subject.onNext(2);
    subject.onComplete();
    subject.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // no result
      }
    });

    /* error test  */
    BehaviorSubject<Integer> error = BehaviorSubject.create();
    error.onNext(0);
    error.onNext(1);
    error.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer));
      }
    });
    error.onNext(2);
    error.onError(TestHelper.error(""));
  }

  @Test
  public void testPublishSubject() {
    PublishSubject<Integer> subject = PublishSubject.create();
    subject.onNext(0);
    subject.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // 1 2
      }
    });
    subject.onNext(1);
    subject.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // 2
      }
    });
    subject.onNext(2);
    subject.onComplete();

    /* error test */
    PublishSubject<Integer> error = PublishSubject.create();
    error.onNext(0);
    error.subscribe(new Observer<Integer>() {
      @Override public void onSubscribe(Disposable d) {

      }

      @Override public void onNext(Integer integer) {
        L.print(String.valueOf(integer));
      }

      @Override public void onError(Throwable e) {
        L.print(e);
      }

      @Override public void onComplete() {

      }
    });
    error.onNext(1);
    error.onNext(2);
    error.onError(TestHelper.error(""));
  }

  @Test
  public void testReplaySubject() {
    ReplaySubject<Integer> subject = ReplaySubject.createWithSize(2); // 数量限制
    subject.onNext(0);
    subject.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // 0 1 2
      }
    });
    subject.onNext(1);
    subject.onNext(2);
    subject.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // 1 2
      }
    });
    subject.onComplete();

    /* error test */
    ReplaySubject<Integer> error = ReplaySubject.createWithSize(2);
    error.onNext(0);
    error.onNext(1);
    error.subscribe(new Observer<Integer>() {
      @Override public void onSubscribe(Disposable d) {}

      @Override public void onNext(Integer integer) {
        L.print(String.valueOf(integer));
      }

      @Override public void onError(Throwable e) {
        L.print(e);
      }

      @Override public void onComplete() {}
    });
    error.onNext(2);
    error.onError(TestHelper.error(""));
  }
}