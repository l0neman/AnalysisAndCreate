package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;

/**
 * Created by DSI on 2018/1/5.
 */
public class ObservableUtilityOperatorsTest {
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
  public void testDelay() {
    Observable.just(0, 1, 2, 3)
        .delay(100, TimeUnit.MILLISECONDS)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 100毫秒后 0 1 2 3
          }
        });
    TestHelper.block(1);
  }

  @Test
  public void testDo() {
    Observable.just(0, 1, 2, 3)
        /* 注册 onSubscribe 监听 */
        .doOnSubscribe(new Consumer<Disposable>() {
          @Override public void accept(Disposable disposable) throws Exception {}
        })
        /* 注册 onNext 监听 */
        .doOnNext(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {}
        })
        /* 注册 onComplete 监听 */
        .doOnComplete(new Action() {
          @Override public void run() throws Exception {}
        })
        /* 注册 onError 监听 */
        .doOnError(new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {}
        })
        /* 注册 onNext 和 onComplete 监听 */
        .doOnEach(new Observer<Integer>() {
          @Override public void onSubscribe(Disposable d) { }

          @Override public void onNext(Integer integer) { }

          @Override public void onError(Throwable e) { }

          @Override public void onComplete() {}
        });
  }

  @Test
  public void testMaterialize() {
    Observable<Notification<Integer>> materialize = Observable.just(0, 1, 2)
        .materialize();

    materialize
        .subscribe(new Consumer<Notification<Integer>>() {
          @Override public void accept(Notification<Integer> integerNotification) throws Exception {
            if (integerNotification.isOnComplete()) {
              L.print("onComplete");
            } else if (integerNotification.isOnNext()) {
              L.print(String.valueOf(integerNotification.getValue()));
            } else if (integerNotification.isOnError()) {
              L.print(integerNotification.getError());
            }
          }
        });

    materialize.dematerialize()
        .subscribe(new Consumer<Object>() {
          @Override public void accept(Object o) throws Exception {
            L.print(String.valueOf(o)); // 0 1 2
          }
        });
  }

  @Test
  public void testObserveOn() {
    Observable.just(0, 1, 2)
        .observeOn(Schedulers.newThread())
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1 2 on thread RxNewThreadScheduler-1
          }
        });
    TestHelper.block(1);
  }

  @Test
  public void testSerialize() {
    /*
      t1 3    *
         |  |  |
      t2  0  1  2

      -> 3 0 1 onComplete
    */
    Observable
        .unsafeCreate(new ObservableSource<Integer>() {
          @Override public void subscribe(final Observer<? super Integer> observer) {
            TestHelper.runOnNewThread(new Runnable() {
              @Override public void run() {
                TestHelper.sleep(20);
                observer.onNext(0);
                TestHelper.sleep(120);
                observer.onNext(1);
                TestHelper.sleep(100);
                observer.onNext(2);
              }
            });
            TestHelper.runOnNewThread(new Runnable() {
              @Override public void run() {
                observer.onNext(3);
                TestHelper.sleep(150);
                observer.onComplete();
              }
            });
          }
        })
        .serialize()
        .subscribe(new DisposableObserver<Integer>() {
          @Override public void onNext(Integer integer) {
            L.print(String.valueOf(integer));
          }

          @Override public void onError(Throwable e) {}

          @Override public void onComplete() {
            L.print("onComplete");
          }
        });
  }

  @Test
  public void testSubscribeOn() {
    Observable
        .create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            /* run on thread RxNewThreadScheduler-1 */
            e.onNext(0);
            e.onNext(1);
            e.onNext(2);
            e.onComplete();
          }
        })
        .subscribeOn(Schedulers.newThread())
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer));
          }
        });

    TestHelper.block(1);
  }

  @Test
  public void testTimeInterval() {
    Observable
        .create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            Thread.sleep(100);
            e.onNext(0);
            Thread.sleep(120);
            e.onNext(1);
            Thread.sleep(50);
            e.onNext(2);
            Thread.sleep(3);
            e.onComplete();
          }
        })
        .subscribeOn(Schedulers.newThread())
        .timeInterval()
        .subscribe(new Consumer<Timed<Integer>>() {
          @Override public void accept(Timed<Integer> integerTimed) throws Exception {
            L.print(String.valueOf(integerTimed.time())); // 100 120 50 (大约)
          }
        });

    TestHelper.block(2);
  }

  @Test
  public void testTimeout() {
    Observable
        .create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            Thread.sleep(200);
            e.onNext(0);
            e.onComplete();
          }
        })
        .timeout(100, TimeUnit.MILLISECONDS)
        .subscribe(new DisposableObserver<Integer>() {
          @Override public void onNext(Integer integer) {}

          @Override public void onError(Throwable e) {
            L.print(e); // call this
          }

          @Override public void onComplete() {}
        });
  }

  @Test
  public void testTimestamp() {
    Observable
        .create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            Thread.sleep(60);
            e.onNext(0);
            Thread.sleep(100);
            e.onNext(1);
            Thread.sleep(80);
            e.onNext(2);
            e.onComplete();
          }
        })
        .timestamp()
        .subscribe(new Consumer<Timed<Integer>>() {
          @Override public void accept(Timed<Integer> integerTimed) throws Exception {
            L.print(String.valueOf(integerTimed.time()));
          }
        });

    TestHelper.block(2);
  }

  @Test
  public void testUsing() {
    /*
       -> sub     dispose
           |  |  |  |
       ->         close
     */
    Observable
        .using(new Callable<BufferedReader>() {
          @Override public BufferedReader call() throws Exception {
            return new BufferedReader(new StringReader("string line."));
          }
        }, new Function<BufferedReader, ObservableSource<String>>() {
          @Override
          public ObservableSource<String> apply(BufferedReader reader) throws Exception {
            return Observable.just(reader.readLine());
          }
        }, new Consumer<BufferedReader>() {
          @Override public void accept(BufferedReader reader) throws Exception {
            reader.close();
          }
        })
        .subscribe(new Consumer<String>() {
          @Override public void accept(String s) throws Exception {
            L.print(s);
          }
        });
  }
}