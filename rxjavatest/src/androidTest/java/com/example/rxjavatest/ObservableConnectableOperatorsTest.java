package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;

/**
 * Created by DSI on 2018/1/8.
 */
public class ObservableConnectableOperatorsTest {

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
  public void testPublish() {
    ConnectableObservable<Integer> publish = Observable.just(0, 1, 2, 3, 4)
        .publish();
    publish.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print(String.valueOf(integer)); // 0 1 2 3 4
      }
    });
    publish.connect();
  }

  @Test
  public void testRefCount() {
    ConnectableObservable<Integer> publish = Observable.range(0, 4).publish();
    publish.refCount()
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 0 1 2
          }
        });
  }

  @Test
  public void testReplay() {
    ConnectableObservable<Integer> replay = Observable.create(
        new ObservableOnSubscribe<Integer>() {
          @Override public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            for (int i = 0; i < 4; i++) {
              Thread.sleep(100);
              e.onNext(i);
            }
          }
        })
        .replay();
    replay.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print("sub 1: %d", integer); // 0 1 2 3
      }
    });
    replay.connect();
    TestHelper.sleep(200);
    replay.subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        L.print("sub 2: %d", integer); // 0 1 2 3
      }
    });
  }
}