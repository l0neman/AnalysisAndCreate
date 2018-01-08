package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by DSI on 2018/1/3.
 */
public class ObservableFilteringTest {
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
  public void testDebounce() throws Exception {
    Subject<Integer> subject = PublishSubject.create();
    subject.debounce(500, TimeUnit.MILLISECONDS)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 2 3
          }
        });

    subject.onNext(0);
    Thread.sleep(300);
    subject.onNext(1);
    Thread.sleep(300);
    subject.onNext(2);
    Thread.sleep(600);
    subject.onNext(3);
    subject.onComplete();
  }

  @Test
  public void testDistinct() {
    Observable.just(1, 2, 3, 1, 2, 4)
        .distinct(new Function<Integer, Integer>() {
          @Override public Integer apply(Integer integer) throws Exception {
            return integer; // 默认key为自身
          }
        })
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 1 2 3 4
          }
        });
  }

  @Test
  public void testElementAt() {
    Observable.just(0, 1, 2, 3, 4)
        .elementAt(2)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 2
          }
        });
  }

  @Test
  public void testFilter() {
    Observable.just(0, 1, 2, 3, 4, 5)
        .filter(new Predicate<Integer>() {
          @Override public boolean test(Integer integer) throws Exception {
            return integer <= 2;
          }
        })
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1 2
          }
        });
  }

  @Test
  public void testFirst() {
    Observable.just(0, 1, 2)
        .first(0)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0
          }
        });
  }

  @Test
  public void testIgnoreElements() {
    Observable.just(0, 1, 2, 3)
        .ignoreElements()
        .subscribe(new Action() {
          @Override public void run() throws Exception {
            L.print("end.");
          }
        });
  }

  @Test
  public void testLast() {
    Observable.just(0, 1, 2, 3, 4)
        .last(0)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 4
          }
        });
  }

  @Test
  public void testSample() {
    Observable.create(
        new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            e.onNext(0);
            Thread.sleep(180);
            e.onNext(1);
            Thread.sleep(180);
            e.onNext(2); // 360
            Thread.sleep(180);
            e.onNext(3); // 540
          }
        })
        .sample(400, TimeUnit.MILLISECONDS)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 2 3
          }
        });
  }

  @Test
  public void testSkip() {
    Observable.just(0, 1, 2, 3, 4)
        .skip(2)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 2 3 4
          }
        });
  }

  @Test
  public void testSkipLast() {
    Observable.just(0, 1, 2, 3, 4)
        .skipLast(2)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1 2
          }
        });
  }

  @Test
  public void testTake() {
    Observable.just(0, 1, 2, 3, 4)
        .take(2)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1
          }
        });
  }

  @Test
  public void testTakeLast() {
    Observable.just(0, 1, 2, 3, 4)
        .takeLast(2)
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 3 4
          }
        });
  }

}