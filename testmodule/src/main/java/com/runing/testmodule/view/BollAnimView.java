package com.runing.testmodule.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DSI on 2017/9/13.
 */

public final class BollAnimView extends View {

  private static final int MSG_INVALIDATE = 0;
  private static final int MSG_UPDATE_BOLL = 1;

  private WeakReference<Bitmap> mCenter;
  private @DrawableRes int mImageId;
  private int mW;
  private int mH;

  private ExecutorService mBollThreadPool = Executors.newCachedThreadPool();
  private LinkedList<Point> mBolls = new LinkedList<>();
  private int mBollDuration = 800;
  private Paint mBollPaint;

  private Handler mHandler = new Handler(new Handler.Callback() {
    @Override public boolean handleMessage(Message msg) {
      switch (msg.what) {
      case MSG_INVALIDATE:
        invalidate();
        break;
      case MSG_UPDATE_BOLL:
        Bundle bollData = msg.getData();
        Point boll = (Point) msg.obj;
        final double angle = bollData.getDouble("angle");
        final int radius = bollData.getInt("radius");
        boll.set(getCircleEdgeX(radius, angle), getCircleEdgeY(radius, angle));
        break;
      }
      return true;
    }
  });

  public BollAnimView(Context context) {
    this(context, null);
  }

  public BollAnimView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BollAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mBollPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mBollPaint.setColor(Color.BLUE);
    mBollPaint.setStyle(Paint.Style.FILL);
  }

  public void setCenterImage(@DrawableRes int imageId) {
    this.mImageId = imageId;
    this.mCenter = new WeakReference<>(BitmapFactory.decodeResource(getResources(), imageId));
  }

  public Bitmap getImage() {
    final Bitmap bitmap = mCenter.get();
    if (bitmap != null) {
      return bitmap;
    }
    final Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), mImageId);
    this.mCenter = new WeakReference<>(bitmap1);
    return bitmap1;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int wMode = MeasureSpec.getMode(widthMeasureSpec);
    final int hMode = MeasureSpec.getMode(heightMeasureSpec);
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);

    if (wMode == MeasureSpec.AT_MOST && mCenter != null) {
      width = getImage().getWidth() * 2;
    }
    if (hMode == MeasureSpec.AT_MOST && mCenter != null) {
      height = getImage().getHeight() * 2;
    }
    super.onMeasure(
        MeasureSpec.makeMeasureSpec(width, wMode),
        MeasureSpec.makeMeasureSpec(height, hMode)
    );
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mW = w;
    mH = h;
  }

  public void setBollDuartion(int mBollDuration) {
    if (this.mBollDuration < 200) {
      mBollDuration = 200;
    }
    this.mBollDuration = mBollDuration;
  }

  public void startNewBoll() {
    if (mCenter == null) {
      return;
    }
    if (!mStart) {
      startInvalidate();
    }
    Random random = new Random();
    final Point newBoll = new Point();
    final double angle = random.nextDouble() * 360D;
    final int radius = getImage().getWidth() / 2;
    newBoll.x = getCircleEdgeX(radius, angle);
    newBoll.y = getCircleEdgeY(radius, angle);

    mBolls.add(newBoll);
    final int every = 20;
    final int n = mBollDuration / every;
    mBollThreadPool.execute(new Runnable() {
      @Override public void run() {
        for (int i = 0; i < n; i++) {

          Message booMSg = Message.obtain();
          booMSg.obj = newBoll;
          Bundle bundle = new Bundle();
          bundle.putInt("radius", (int) (i * 1F / n * radius));
          bundle.putDouble("angle", angle);
          booMSg.setData(bundle);
          booMSg.what = MSG_UPDATE_BOLL;
          mHandler.sendMessage(booMSg);

          try {
            Thread.sleep(n);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      }
    });
  }

  private Thread mUpdate;
  private boolean mStart = false;

  private void startInvalidate() {
    mStart = true;
    mUpdate = new Thread(new Runnable() {
      @Override public void run() {
        for (; mStart; ) {
          mHandler.sendEmptyMessage(MSG_INVALIDATE);
          try {
            Thread.sleep(20);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      }
    });
    mUpdate.start();
  }

  public void recycle() {
    mStart = false;
    mUpdate = null;
  }

  private int getCircleEdgeX(int radius, double angle) {
    return getXByOx((int) (radius * Math.cos(angle)));
  }

  private int getCircleEdgeY(int radius, double angle) {
    return getYByOy((int) (radius * Math.sin(angle)));
  }

  private int getYByOy(int oY) {
    final int halfH = mH >>> 1;
    return oY > 0 ? halfH - oY : oY - halfH;
  }

  private int getXByOx(int oX) {
    final int halfW = mW >>> 1;
    return oX > 0 ? halfW - oX : oX - halfW;
  }

//  private int getCircleYByY(int y) {
//    final int halfH = mH >>> 1;
//    return y > halfH ? y - halfH : halfH - y;
//  }
//
//  private int getCircleXByX(int x) {
//    final int halfW = mW >>> 1;
//    return x > halfW ? x - halfW : halfW - x;
//  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mCenter == null) {
      return;
    }
    drawImage(canvas);
    if (mBolls.isEmpty()) {
      return;
    }
    final int size = mBolls.size();
    for (int i = 0; i < size; i++) {
      Point boll = mBolls.get(i);
      drawBoll(canvas, boll.x - 20, boll.y - 20, 40);
    }
  }

  private void drawImage(Canvas canvas) {
    final Bitmap image = getImage();
    canvas.drawBitmap(image, (mW - image.getWidth()) / 2, (mH - image.getHeight()) / 2, null);
  }

  private void drawBoll(Canvas canvas, int x, int y, int radius) {
    canvas.drawCircle(x, y, radius, mBollPaint);
  }
}
