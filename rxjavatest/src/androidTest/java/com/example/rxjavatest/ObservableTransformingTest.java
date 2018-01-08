package com.example.rxjavatest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.widget.ImageView;

import com.runing.utilslib.debug.log.L;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;

/**
 * Created by DSI on 2018/1/3.
 */
public class ObservableTransformingTest {

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
  public void testBuffer() {
    Observable.just(0, 1, 2, 3, 4, 5)
        .buffer(2) // 1 个一组打包发射
        .subscribe(new Consumer<List<Integer>>() {
          @Override public void accept(List<Integer> integers) throws Exception {
            L.printList(integers); // [0, 1] [2, 3] [4, 5]
          }
        });

    Observable.just(0, 1, 2, 3, 4, 5)
        .buffer(2, 3)  // 每隔3个作为起点
        .subscribe(new Consumer<List<Integer>>() {
          @Override public void accept(List<Integer> integers) throws Exception {
            L.printList(integers); // [0, 1] [3, 4]
          }
        });
  }

  @Test
  public void testFlatMap() {
    /* 将一维的列表平铺成单个的整型 */
    List<Integer> list = Arrays.asList(1, 3, 5, 7);
    List<Integer> list1 = Arrays.asList(2, 4, 6, 8);
    Observable.just(list, list1)
        .flatMap(new Function<List<Integer>, ObservableSource<Integer>>() {
          @Override
          public ObservableSource<Integer> apply(List<Integer> integers) throws Exception {
            return Observable.fromIterable(integers);
          }
        })
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            L.print(String.valueOf(integer)); // 1 3 5 7 2 4 6 8
          }
        });
  }

  @Test
  public void testGroupBy() {
    Observable.just(0, 1, 2, 3, 4, 5)
        .groupBy(new Function<Integer, Integer>() {
          @Override public Integer apply(Integer integer) throws Exception {
            return integer % 2 == 0 ? 0 : 1; // 按奇偶数分组
          }
        })
        .subscribe(new Consumer<GroupedObservable<Integer, Integer>>() {
          @Override
          public void accept(GroupedObservable<Integer, Integer> groupedObservable) throws Exception {
            if (groupedObservable.getKey() == 0) {
              groupedObservable.subscribe(new Consumer<Integer>() {
                @Override public void accept(Integer integer) throws Exception {
                  L.print("偶数 -> %d", integer); // 偶数 0 2 4
                }
              });
            } else if (groupedObservable.getKey() == 1) {
              groupedObservable.subscribe(new Consumer<Integer>() {
                @Override public void accept(Integer integer) throws Exception {
                  L.print("奇数 -> %d", integer); // 奇数 1 3 5
                }
              });
            }
          }
        });
  }

  @Test
  public void testMap() {
    final Context context = InstrumentationRegistry.getContext();
    final ImageView imageView = new ImageView(context);
    Observable
        .just(BitmapFactory.decodeResource(
            context.getResources(), R.mipmap.ic_launcher
        ))
        .map(new Function<Bitmap, Drawable>() { // Bitmap 转换为 Drawable
          @Override public Drawable apply(Bitmap bitmap) throws Exception {
            return new BitmapDrawable(bitmap);
          }
        })
        .subscribe(new Consumer<Drawable>() {
          @Override public void accept(Drawable drawable) throws Exception {
            imageView.setImageDrawable(drawable); // set image
          }
        });
  }

  @Test
  public void testScan() {
    Observable.just(0, 1, 2, 3, 4)
        .scan(new BiFunction<Integer, Integer, Integer>() {
          @Override public Integer apply(Integer integer, Integer integer2) throws Exception {
            return integer + integer2;
          }
        })
        .subscribe(new Consumer<Integer>() {
          @Override public void accept(Integer integer) throws Exception {
            //0 (0+1)=1 (1+2)=3 (3+3)=6 (6+4)=10
            L.print(String.valueOf(integer)); // 0 1 3 6 10
          }
        });
  }

  @Test
  public void testWindow() {
    /* print s(subscribe)... 0 1 c(complete)... s... 2 3 c... s... 4 5 c...*/
    Observable.just(0, 1, 2, 3, 4, 5)
        .window(2) // 每两个分成一个窗口
        .subscribe(new Consumer<Observable<Integer>>() {
          @Override
          public void accept(final Observable<Integer> integerObservable) throws Exception {
            integerObservable.subscribe(new Observer<Integer>() {
              @Override public void onSubscribe(Disposable d) {
                L.print("subscribe on " + String.valueOf(integerObservable));
              }

              @Override public void onNext(Integer integer) {
                L.print(String.valueOf(integer));
              }

              @Override public void onError(Throwable e) {}

              @Override public void onComplete() {
                L.print("complete on " + String.valueOf(integerObservable));
              }
            });
          }
        });
  }
}