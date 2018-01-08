package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by DSI on 2018/1/5.
 */
public class ObservableConditionalOperatorsTest {
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
  public void testAll() {
    Observable
        .just(0, 1, 2, 3, 4)
        .all(new Predicate<Integer>() {
          @Override public boolean test(Integer integer) throws Exception {
            return integer < 4;
          }
        })
        .subscribe(new Consumer<Boolean>() {
          @Override public void accept(Boolean aBoolean) throws Exception {
            L.print(String.valueOf(aBoolean)); // false
          }
        });
  }

  @Test
  public void testAmb() {
    Observable
        .amb(Arrays.asList(
            Observable.just(1, 3, 5)
                .delay(40, TimeUnit.MILLISECONDS),
            Observable.just(2, 4, 6)
                .delay(60, TimeUnit.MILLISECONDS))
        )
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 2 4 6
          }
        });

    TestHelper.block(1);
  }

  @Test
  public void testContains() {
    Observable
        .just(0, 1, 2, 3)
        .contains(2)
        .subscribe(new Consumer<Boolean>() {
          @Override public void accept(Boolean aBoolean) throws Exception {
            L.print(String.valueOf(aBoolean)); // true
          }
        });
  }

  @Test
  public void testDefaultIfEmpty() {
    Observable.<Integer>empty()
        .defaultIfEmpty(-1)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // -1
          }
        });
  }

  @Test
  public void testSequenceEqual() {
    Observable.sequenceEqual(
        Observable.create(new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            e.onNext(1);
            e.onNext(2);
            e.onComplete();
          }
        }),
        Observable.just(0, 1, 2)
    ).subscribe(new Consumer<Boolean>() {
      @Override public void accept(Boolean aBoolean) throws Exception {
        L.print(String.valueOf(aBoolean));
      }
    });
  }

  @Test
  public void testSkipUntil() {
    /*
       |  |  |  |
       0  1  2  3
              a *
    */
    Observable.create(new ObservableOnSubscribe<Integer>() {
      @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
        e.onNext(0);
        Thread.sleep(100);
        e.onNext(1);
        Thread.sleep(100);
        e.onNext(2);
        Thread.sleep(100);
        e.onNext(3);
        e.onComplete();
      }
    }).skipUntil(Observable.just('a').delay(220, TimeUnit.MILLISECONDS))
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 3
          }
        });
  }

  @Test
  public void testSkipWhile() {
    Observable.just(0, 1, 2, 3, 2, 5)
        .skipWhile(new Predicate<Integer>() {
          @Override public boolean test(Integer integer) throws Exception {
            return integer < 2;
          }
        })
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 2 3 2 5
          }
        });
  }

  @Test
  public void testTakeUntil() {
    /*
       |  |  |  |
       0  1  2  3
              a *
    */
    Observable.create(new ObservableOnSubscribe<Integer>() {
      @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
        e.onNext(0);
        Thread.sleep(100);
        e.onNext(1);
        Thread.sleep(100);
        e.onNext(2);
        Thread.sleep(100);
        e.onNext(3);
        e.onComplete();
      }
    }).takeUntil(Observable.just('a').delay(220, TimeUnit.MILLISECONDS))
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1 2
          }
        });
  }

  @Test
  public void testTakeWhile() {
    Observable
        .just(0, 1, 2, 3, 2, 5)
        .takeWhile(new Predicate<Integer>() {
          @Override public boolean test(Integer integer) throws Exception {
            return integer < 2;
          }
        })
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1
          }
        });
  }
}