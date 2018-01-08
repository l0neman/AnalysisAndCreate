package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DSI on 2017/12/23.
 */
public class ObservableCreateTest {
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
  public void testCreateFun() {
    /* 提供 Disposable 或 Cancellable 的创建*/
    Observable.create(new ObservableOnSubscribe<Integer>() {
      @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
        e.setDisposable(new Disposable() {
          @Override public void dispose() {
            L.print("dispose");
          }

          @Override public boolean isDisposed() { return false; }
        });
        // e.setCancellable(new Cancellable() {
        //   @Override public void cancel() throws Exception { /* 取消操作 */ }
        // });
        e.onNext(0);
        e.onNext(1);
        e.onNext(2);
        e.onComplete();
      }
    });

    Observable.unsafeCreate(new ObservableSource<Integer>() {
      @Override public void subscribe(Observer<? super Integer> observer) {
        Disposable d = new Disposable() {
          private boolean isDispose;

          @Override public void dispose() { isDispose = true; }

          @Override public boolean isDisposed() { return isDispose; }
        };
        observer.onSubscribe(d);

        if (!d.isDisposed()) {
          observer.onNext(0);
        }
        if (!d.isDisposed()) {
          observer.onNext(1);
        }
        if (!d.isDisposed()) {
          observer.onNext(2);
        }
        if (!d.isDisposed()) {
          observer.onComplete();
        }
      }
    });
  }

  @Test
  public void testDefer() {
    Observable<Integer> defer = Observable.defer(new Callable<ObservableSource<Integer>>() {
      @Override public ObservableSource<Integer> call() throws Exception {
        return Observable.just(0, 1, 2);
      }
    });

    defer.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // 0 1 2
      }
    });

    defer.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // 0 1 2
      }
    });
  }

  @Test
  public void testNever() {
    Observable.empty()  // 只发射完成
        .subscribe(new Observer<Object>() {
          @Override public void onSubscribe(Disposable d) {}

          @Override public void onNext(Object o) {}

          @Override public void onError(Throwable e) {}

          @Override public void onComplete() { L.print("onComplete"); }
        });

    Observable.never(); // 什么都不发射

    Observable.error(new Exception("test")) // 只发射错误
        .subscribe(new Observer<Object>() {
          @Override public void onSubscribe(Disposable d) {}

          @Override public void onNext(Object o) {}

          @Override public void onError(Throwable e) { L.print(e); }

          @Override public void onComplete() {}
        });
  }

  @Test
  public void testFrom() {
    /* 来自一个数组类型 */
    Observable.fromArray("1", "2", "3");
    /* 来自一个集合类型 */
    Observable.fromIterable(Arrays.asList("1", "2", "3"));
    /* 来自一个函数返回值 */
    Observable.fromCallable(new Callable<String>() {
      @Override public String call() throws Exception {
        return "a string";
      }
    });
    /* ... */
  }

  @Test
  public void testInterval() {
  /* 每隔一秒发送一次 */
    Observable.interval(1, TimeUnit.SECONDS)
        .subscribe(new Consumer<Long>() {
          @Override public void accept(Long aLong) throws Exception {
            L.print(String.valueOf(aLong)); // 0 1 2 3 ...
          }
        });

    TestHelper.block(10);
  }

  @Test
  public void testJust() {
/* 从一个或多个对象创建 Observable */
    Observable.just(0);
    Observable.just(0, 1, 2, 3)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1 2 3
          }
        });
  }

  @Test
  public void testRange() {
    Observable.range(1, 4)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 1 2 3 4
          }
        });
  }

  @Test
  public void testRepeat() {
    Observable.just(0, 1, 2)
        .repeat(2)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1 2 0 1 2
          }
        });
  }

  @Test
  public void testStart() {
    Observable
        .fromCallable(new Callable<String>() {
          @Override public String call() throws Exception {
            HttpURLConnection conn = (HttpURLConnection)
                new URL("https://github.com/wangruning/android-notes/blob/master/README.md")
                    .openConnection();
            conn.setRequestMethod("GET");
            StringBuilder builder = new StringBuilder();
            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int l;
            while ((l = is.read(buffer)) != -1) {
              builder.append(new String(buffer, 0, l));
            }
            conn.disconnect();
            return builder.toString();
          }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {
          @Override public void accept(String s) throws Exception {
            L.print(s);
          }
        });

    TestHelper.block(20);
  }

  @Test
  public void testTimer() {
    /* 指定一秒后发出 */
    Observable.timer(1, TimeUnit.SECONDS)
        .subscribe(new Consumer<Long>() {
          @Override public void accept(Long aLong) throws Exception {
            L.print(String.valueOf(aLong)); // 0
          }
        });
    TestHelper.block(2);
  }
}