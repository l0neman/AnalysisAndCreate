package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by DSI on 2018/1/8.
 */
public class ObservableConvertOperatorsTest {

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
  public void testTo() {
    /* to feature */
    Future<Integer> future = Single.create(new SingleOnSubscribe<Integer>() {
      @Override public void subscribe(SingleEmitter<Integer> e) throws Exception {
        Thread.sleep(1000);
        e.onSuccess(1);
      }
    }).toFuture();

    try {
      L.print(String.valueOf(future.get()));
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    /* to list */
    Observable.just(0, 1, 2)
        .toList()
        .subscribe(new Consumer<List<Integer>>() {
          @Override public void accept(List<Integer> integers) throws Exception {
            L.printList(integers);
          }
        });

    /* to blocking iterable */
    for (int i : Observable.just(0, 1, 2, 3).blockingIterable()) {
      L.print(String.valueOf(i));
    }

    /* to map */
    Observable.just(0, 1, 2)
        .toMap(new Function<Integer, String>() {
          @Override public String apply(Integer integer) throws Exception {
            return String.valueOf(integer);
          }
        })
        .subscribe(new Consumer<Map<String, Integer>>() {
          @Override public void accept(Map<String, Integer> stringIntegerMap) throws Exception {
            L.printMap(stringIntegerMap); // { 0: 0, 1: 1, 2: 2}
          }
        });

  }
}