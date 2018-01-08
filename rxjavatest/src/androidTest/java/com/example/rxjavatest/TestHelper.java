package com.example.rxjavatest;

import io.reactivex.Emitter;

/**
 * Created by DSI on 2017/12/29.
 */

public class TestHelper {

  public static <T> void callOnNextOnNewThread(final Emitter<T> e, final T value) {
    runOnNewThread(new Runnable() {
      @Override public void run() {
        e.onNext(value);
      }
    });
  }

  public static void runOnNewThread(Runnable runnable) {
    new Thread(runnable).start();
  }

  public static void block(long second) {
    try {
      Thread.sleep(second * 1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static Error error(String error) {
    return new Error(error);
  }
}
