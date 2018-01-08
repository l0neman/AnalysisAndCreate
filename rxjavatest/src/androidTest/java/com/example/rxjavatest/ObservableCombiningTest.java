package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DSI on 2018/1/4.
 */
public class ObservableCombiningTest {
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
  public void testCombineLatest() {
    /*
      |  |  |  |  |
      0  1  2
      a        b  c
    */
    Observable.combineLatest(
        Observable.create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            Thread.sleep(100);
            e.onNext(1);
            Thread.sleep(100);
            e.onNext(2);
            e.onComplete();
          }
        }).subscribeOn(Schedulers.newThread()),
        Observable.create(new ObservableOnSubscribe<String>() {
          @Override public void subscribe(ObservableEmitter<String> e) throws Exception {
            e.onNext("a");
            Thread.sleep(300);
            e.onNext("b");
            Thread.sleep(100);
            e.onNext("c");
            e.onComplete();
          }
        }).subscribeOn(Schedulers.newThread()),
        new BiFunction<Integer, String, String>() {
          @Override public String apply(Integer integer, String s) throws Exception {
            return integer + s;
          }
        }
    )
        .subscribe(new Consumer<String>() {
          @Override public void accept(String s) throws Exception {
            L.print(s); // 0a 1a 2a 2b 2c
          }
        });
    TestHelper.block(2);
  }

  @Test
  public void testJoin() {
    /*
         *    *    *    *
        /    /    /    /
       a    b    c    d
      |    |    |    |
      0    1         2
       \    \         \
        *    \         *
              \
               \
                \
                 *
    */
    Observable
        .create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            Thread.sleep(200);
            e.onNext(1);
            Thread.sleep(400);
            e.onNext(2);
            e.onComplete();
          }
        })
        .subscribeOn(Schedulers.newThread())
        .join(
            Observable.create(new ObservableOnSubscribe<String>() {
              @Override public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("a");
                Thread.sleep(200);
                e.onNext("b");
                Thread.sleep(200);
                e.onNext("c");
                Thread.sleep(200);
                e.onNext("d");
                e.onComplete();
              }
            }).subscribeOn(Schedulers.newThread()),
            /* 为源 Observable 发射的每个项目设置时效（转换成窗口类型） */
            new Function<Integer, ObservableSource<Integer>>() {
              @Override
              public ObservableSource<Integer> apply(final Integer integer) throws Exception {
                int delay = 100;
                if (integer == 1) {
                  delay = 250;
                }
                return Observable
                    .timer(delay, TimeUnit.MILLISECONDS)
                    .map(new Function<Long, Integer>() {
                      @Override public Integer apply(Long aLong) throws Exception {
                        return integer;
                      }
                    });
              }
            },
            /* 为join的 Observable 发射的每个项目设置时效（转换成窗口类型） */
            new Function<String, ObservableSource<String>>() {
              @Override public ObservableSource<String> apply(final String s) throws Exception {
                return Observable
                    .timer(100, TimeUnit.MILLISECONDS)
                    .map(new Function<Long, String>() {
                      @Override public String apply(Long aLong) throws Exception {
                        return s;
                      }
                    });
              }
            }, new BiFunction<Integer, String, String>() {
              @Override public String apply(Integer integer, String s) throws Exception {
                return integer + s;
              }
            }
        )
        .subscribe(new Consumer<String>() {
          @Override public void accept(String s) throws Exception {
            L.print(s); // 0a 1b 1c 2d
          }
        });

    TestHelper.block(2);
  }

  @Test
  public void testMerge() {
    Observable
        .merge(
            Observable.just(1, 3, 5, 7),
            Observable.just(2, 4, 6, 8)
        )
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 1 3 5 7 2 4 6 8
          }
        });
  }

  @Test
  public void testStartWith() {
    Observable.just(0, 1, 2, 3)
        .startWith(Arrays.asList(3, 4))
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 3 4 0 1 2 3
          }
        });
  }

  @Test
  public void testSwitch() {
    /*
      |  |  |  |  |  |
      -> 0  1  2  3 ...
      ->      2  4  6
    */
    Observable.switchOnNext(Observable.create(
        new ObservableOnSubscribe<ObservableSource<Integer>>() {
          @Override
          public void subscribe(ObservableEmitter<ObservableSource<Integer>> e) throws Exception {
            e.onNext(Observable.interval(100, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Integer>() {
                  @Override public Integer apply(Long aLong) throws Exception {
                    return aLong.intValue();
                  }
                }));
            Thread.sleep(240);
            e.onNext(Observable.just(2, 4, 6));
            e.onComplete();
          }
        }
    )).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1 2 4 6
          }
        });

    TestHelper.block(1);
  }

  @Test
  public void testZip() {
    Observable.just(0, 1, 2, 3)
        .zipWith(Observable.just("a", "b", "c", "d"),
            new BiFunction<Integer, String, String>() {
              @Override public String apply(Integer integer, String s) throws Exception {
                return integer + s;
              }
            })
        .subscribe(new Consumer<String>() {
          @Override public void accept(String s) throws Exception {
            L.print(s); // 0a 1b 2c 3d
          }
        });
  }
}