package com.example.rxjavatest;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;

/**
 * Created by DSI on 2017/12/18.
 */
public class RxJavaTestTest {

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

//  @Test
//  public void testHelloRx() throws InterruptedException {
//    if (false) {
//      L.startLongPrint();
//      /* 从数组获得观察者对象，并遍历输出 */
//      Observable.fromArray(new String[]{"hello0", "hello1", "hello2"})
//          .subscribe(new Action1<String>() {
//            @Override public void call(String s) {
//              L.print(s);
//            }
//          });
//      L.endLongPrint();
//
//      L.startLongPrint();
//      /* 从一个或多个对象获得观察者对象 */
//      Observable.just("sdd", "qww", "12")
//          .subscribe(new Action1<String>() {
//            @Override public void call(String s) {
//              L.print(s);
//            }
//          });
//      L.endLongPrint();
//
//      /* 测试空标记 */
//      Observable.empty().subscribe(new Subscriber<Object>() {
//        @Override public void onCompleted() {
//          /* 只输出了完成 */
//          L.print("onCompleted");
//        }
//
//        @Override public void onError(Throwable e) {
//          L.print("error: " + e);
//        }
//
//        @Override public void onNext(Object o) {
//          L.print(String.valueOf(o));
//        }
//      });
//
//      /* 不执行标记，没有任何回调 */
//      Observable.never().subscribe(new Action1<Object>() {
//        @Override public void call(Object o) {}
//      });
//
//      /* 异常标记 */
//      Observable.error(new AssertionError()).subscribe(new Subscriber<Object>() {
//        @Override public void onCompleted() {
//          L.print("onCompleted");
//        }
//
//        @Override public void onError(Throwable e) {
//          /* 只输出了异常 */
//          L.print("error: " + e);
//        }
//
//        @Override public void onNext(Object o) {
//          L.print(String.valueOf(o));
//        }
//      });
//
//      /* 延迟发送事件 */
//      Observable.timer(3, TimeUnit.SECONDS)
//          .subscribe(new Consumer<Long>() {
//            @Override public void accept(Long aLong) {
//              L.print(String.valueOf(aLong));
//            }
//          });
//
//      /* 回调一定范围内的int数字 */
//      Observable.range(0, 5)
//          .subscribe(new Consumer<Integer>() {
//            @Override public void accept(Integer integer) {
//              L.print(String.valueOf(integer));
//            }
//          });
//
//      /* 每隔一秒，回调一次 */
//      Observable.interval(1, TimeUnit.SECONDS)
//          .subscribe(new Consumer<Long>() {
//            @Override public void accept(Long aLong) {
//              L.print(String.valueOf(aLong));
//            }
//          });
//
//      /* 创建一个延迟初始化的Observable，在每次订阅时都生成一个新的Observable，并重新开始执行任务 */
//      Observable<Integer> defer = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
//        @Override public ObservableSource<? extends Integer> call() throws Exception {
//          return Observable.range(0, 4);
//        }
//      });
//
//      defer.subscribe(new Consumer<Integer>() {
//        @Override public void accept(Integer integer) {
//          L.print("0-" + integer);
//        }
//      });
//
//      defer.subscribe(new Consumer<Integer>() {
//        @Override public void accept(Integer integer) {
//          L.print("1-" + integer);
//        }
//      });
//    }
//
//    block(3);
//  }
//
//  @Test
//  public void testError() {
//    if (false) {
//      Observable.unsafeCreate(
//          new Observable.OnSubscribe<String>() {
//            @Override public void call(final Subscriber<? super String> subscriber) {
//              subscriber.onStart();
//              subscriber.onNext("normal");
//              subscriber.onCompleted();
//            }
//          })
//          /* 出现 */
//          .onErrorResumeNext(new Func1<Throwable, Observable<? extends String>>() {
//            @Override public Observable<? extends String> call(Throwable throwable) {
//              return Observable.error(new AssertionError("replace error"));
//            }
//          })
//          .subscribe(new Subscriber<String>() {
//            @Override public void onCompleted() {
//            }
//
//            @Override public void onError(Throwable e) {
//              L.print(e);
//            }
//
//            @Override public void onNext(String s) {
//
//            }
//          });
//    }
//
//    Observable.unsafeCreate(
//        new Observable.OnSubscribe<String>() {
//          @Override public void call(Subscriber<? super String> subscriber) {
//            subscriber.onError(new Exception());
//          }
//        })
//        .subscribe(new Subscriber<String>() {
//          @Override public void onCompleted() {
//            L.print("onCompleted");
//          }
//
//          @Override public void onError(Throwable e) {
//            L.print(e);
//          }
//
//          @Override public void onNext(String s) {
//            L.print(s);
//          }
//        });
//
//    block(3);
//
//  }
//
//  @Test
//  public void testAsync() {
//    if (false) {
//      /* 从一个操作获取Observable,可完成异步操作 */
//      Observable.fromCallable(
//          new Callable<String>() {
//            @Override public String call() throws Exception {
//            /* 模拟耗时操作 */
//              Thread.block(1000);
//              return "delay";
//            }
//          })
//          .observeOn(Schedulers.newThread())
//          .subscribe(new Action1<String>() {
//            @Override public void call(String s) {
//              L.print("s");
//            }
//          });
//    }
//
//    block(3);
//  }

  private static void block(long second) {
    try {
      Thread.sleep(second * 1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}