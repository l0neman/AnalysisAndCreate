package com.runing.corrdinateandscroll;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.runing.publictools.view.T;

/**
 * view滑动方法辅助类
 * Created by runing on 2017/5/19.
 */

public final class ScrollerHelper {

  /* 一像素的速度 */
  private static final int SPEED_A_PIXEL = 1;

  public static final int MODE_TRANSLATE = 0;
  public static final int MODE_SCROLL_TO = 1;
  public static final int MODE_OFFSET_FUN = 2;
  public static final int MODE_LAYOUT_FUN = 3;
  public static final int MODE_LAYOUT_PARAMS = 4;

  private int mScrollMode = 0;
  private ViewGroup mParent;
  /* parent view 的宽高值 */
  private int mParentW;
  private int mParentH;
  /* parent view 的 padding 值 */
  private int mParentPL;
  private int mParentPR;
  private int mParentPT;
  private int mParentPB;

  private View mTarget;
  /* target view 的宽高值 */
  private int mTargetW;
  private int mTargetH;
  /* target view 的 margin 值 */
  private int mTargetML;
  private int mTargetMR;
  private int mTargetMT;
  private int mTargetMB;

  private int mLastX;
  private int mLastY;

  /* 处理translation的方法 */
  private ObjectAnimator mTransAnimator;
  /* 处理layout params的方法 */
  private ValueAnimator mLayoutParamsAnimator;
  /* 处理layout和offset两种方法 */
  private Handler mLayoutHandler = new Handler(new Handler.Callback() {

    @Override
    public boolean handleMessage(Message msg) {
      scrollByOffset(msg.arg1, 0);
      return true;
    }
  });
  private boolean mStartLayoutThread;
  private Thread mLayoutThread;
  /* 处理scroll to方法 */
  private Scroller mScroller;

  public void setScrollMode(int mScrollMode) {
    this.mScrollMode = mScrollMode;
  }

  public void startScrollInParent(final ViewGroup parent, final View target) {
    mParent = parent;
    mParentPL = parent.getPaddingLeft();
    mParentPR = parent.getPaddingRight();
    mParentPT = parent.getPaddingTop();
    mParentPB = parent.getPaddingBottom();

    mTarget = target;
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTarget.getLayoutParams();
    mTargetML = params.leftMargin;
    mTargetMR = params.rightMargin;
    mTargetMT = params.topMargin;
    mTargetMB = params.bottomMargin;

    initAnim();

    target.post(new Runnable() {
      @Override
      public void run() {
        mParentW = parent.getWidth();
        mParentH = parent.getHeight();
        mTargetW = target.getWidth();
        mTargetH = target.getHeight();
        startScrollInParent();
      }
    });
  }

  private void initAnim() {
    if (mTransAnimator == null) {
      mTransAnimator = ObjectAnimator.ofFloat(mTarget, "translationX", 0, 0);
      mLayoutParamsAnimator = ValueAnimator.ofInt(0);
      mLayoutParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          final int marginLeft = (int) animation.getAnimatedValue();
          FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTarget.getLayoutParams();
          params.leftMargin = marginLeft;
          mTarget.setLayoutParams(params);
        }
      });
      mScroller = new Scroller(mParent.getContext());
      ((ScrollerLayout) mParent).setOnComputeScrollListener(
          new ScrollerLayout.OnComputeScrollListener() {
            @Override
            public void onComputeScroll() {
              if (mScroller.computeScrollOffset()) {
                mParent.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
              }
            }
          });
    }
  }

  private void startScrollInParent() {
    switch (mScrollMode) {
      case MODE_TRANSLATE:
        startScrollWithTranslation();
        break;
      case MODE_SCROLL_TO:
        startScrollWithScrollTo();
        break;
      case MODE_OFFSET_FUN:
        startScrollWithOffsetFun();
        break;
      case MODE_LAYOUT_FUN:
        startScrollWithLayoutFun();
        break;
      case MODE_LAYOUT_PARAMS:
        startScrollWithLayoutParams();
        break;
    }
  }

  /**
   * 使用 layout params 的方法进行滑动
   */
  private void startScrollWithLayoutParams() {
    mTarget.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        switch (event.getActionMasked()) {
          case MotionEvent.ACTION_DOWN:
            stopAllAnimAndThread();
            mLastX = x;
            mLastY = y;
            break;
          case MotionEvent.ACTION_MOVE:
            int xOffset = x - mLastX;
            int yOffset = y - mLastY;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTarget.getLayoutParams();
            final int oldMarginLeft = params.leftMargin;
            final int newMarginLeft = oldMarginLeft + xOffset;
            final int marginLeftLowerLimit = mTargetML;
            if (newMarginLeft < marginLeftLowerLimit) {
              xOffset = marginLeftLowerLimit - oldMarginLeft;
            }
            final int marginLeftUpperLimit = mParentW - mTargetW - mParentPL - mParentPR - mTargetMR;
            if (newMarginLeft > marginLeftUpperLimit) {
              xOffset = marginLeftUpperLimit - oldMarginLeft;
            }
            final int oldMarginTop = params.topMargin;
            final int newMarginTop = oldMarginTop + yOffset;
            final int marginTopLowerLimit = mTargetMT;
            if (newMarginTop < marginLeftLowerLimit) {
              yOffset = marginTopLowerLimit - oldMarginTop;
            }
            final int marginTopUpperLimit = mParentH - mTargetH - mParentPT - mParentPB - mTargetMB;
            if (newMarginTop > marginTopUpperLimit) {
              yOffset = marginTopUpperLimit - oldMarginTop;
            }
            scrollByOffset(xOffset, yOffset);
            printViewInfo(event, xOffset, yOffset);
            break;
          case MotionEvent.ACTION_UP:
            scrollToSideWithLayoutParams();
            break;
        }
        return true;
      }
    });
  }

  /**
   * 使用 layout 方法进行滑动
   */
  private void startScrollWithLayoutFun() {
    startScrollWithOffsetFun();
  }

  /**
   * 使用 offsetxxAndxx 方法进行滑动
   */
  private void startScrollWithOffsetFun() {
    mTarget.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        switch (event.getActionMasked()) {
          case MotionEvent.ACTION_DOWN:
            stopAllAnimAndThread();
            mLastX = x;
            mLastY = y;
            break;
          case MotionEvent.ACTION_MOVE:
            int xOffset = x - mLastX;
            int yOffset = y - mLastY;
            final int oldLeft = mTarget.getLeft();
            final int newLeft = oldLeft + xOffset;
            final int leftLowerLimit = mParentPL + mTargetML;
            if (newLeft < leftLowerLimit) {
              xOffset = leftLowerLimit - oldLeft;
            }
            final int leftUpperLimit = mParentW - mTargetW - mParentPR - mTargetMR;
            if (newLeft > leftUpperLimit) {
              xOffset = leftUpperLimit - oldLeft;
            }
            final int oldTop = mTarget.getTop();
            final int newTop = oldTop + yOffset;
            final int topLowerLimit = mParentPT + mTargetMT;
            if (newTop < topLowerLimit) {
              yOffset = topLowerLimit - oldTop;
            }
            final int topUpperLimit = mParentH - mTargetH - mParentPB - mTargetMB;
            if (newTop > topUpperLimit) {
              yOffset = topUpperLimit - oldTop;
            }
            scrollByOffset(xOffset, yOffset);
            printViewInfo(event, xOffset, yOffset);
            break;
          case MotionEvent.ACTION_UP:
            scrollToSideWithLayoutOrOffsetFun();
            break;
        }
        return true;
      }
    });
  }

  /**
   * 使用 scroll to 的方法进行滑动
   */
  private void startScrollWithScrollTo() {
    mTarget.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        switch (event.getActionMasked()) {
          case MotionEvent.ACTION_DOWN:
            mLastX = x;
            mLastY = y;
            break;
          case MotionEvent.ACTION_MOVE:
            int xOffset = x - mLastX;
            int yOffset = y - mLastY;

            final int oldScrollX = -mParent.getScrollX();
            final int newScrollX = oldScrollX + xOffset;
            final int scrollXLowerLimit = 0;
            if (newScrollX < scrollXLowerLimit) {
              xOffset = scrollXLowerLimit - oldScrollX;
            }
            final int scrollXUpperLimit = mParentW - mTargetW - mParentPL - mParentPR - mTargetML - mTargetMR;
            if (newScrollX > scrollXUpperLimit) {
              xOffset = scrollXUpperLimit - oldScrollX;
            }
            final int oldScrollY = -mParent.getScrollY();
            final int newScrollY = oldScrollY + yOffset;
            final int scrollYLowerLimit = 0;
            if (newScrollY < scrollXLowerLimit) {
              yOffset = scrollYLowerLimit - oldScrollY;
            }
            final int scrollYUpperLimit = mParentH - mTargetH - mParentPT - mParentPB - mTargetMT - mTargetMB;
            if (newScrollY > scrollYUpperLimit) {
              yOffset = scrollYUpperLimit - oldScrollY;
            }
            scrollByOffset(xOffset, yOffset);
            printViewInfo(event, xOffset, yOffset);
            break;
          case MotionEvent.ACTION_UP:
            scrollToSideWithScrollTo();
            break;
        }
        return true;
      }
    });
  }

  /**
   * 使用 translation 的方法进行滑动
   */
  private void startScrollWithTranslation() {
    mTarget.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        switch (event.getActionMasked()) {
          case MotionEvent.ACTION_DOWN:
            stopAllAnimAndThread();
            mLastX = x;
            mLastY = y;
            break;
          case MotionEvent.ACTION_MOVE:
            int xOffset = x - mLastX;
            int yOffset = y - mLastY;
            final int oldTransX = (int) mTarget.getTranslationX();
            final int newTransX = oldTransX + xOffset;
            final int transXLowerLimit = 0;
            if (newTransX < transXLowerLimit) {
              xOffset = transXLowerLimit - oldTransX;
            }
            final int transXUpperLimit = mParentW - mTargetW - mParentPL - mParentPR - mTargetML - mTargetMR;
            if (newTransX > transXUpperLimit) {
              xOffset = transXUpperLimit - oldTransX;
            }

            final int oldTransY = (int) mTarget.getTranslationY();
            final int newTransY = oldTransY + yOffset;
            final int transYLowerLimit = 0;
            if (newTransY < transYLowerLimit) {
              yOffset = transYLowerLimit - oldTransY;
            }
            final int transYUpperLimit = mParentH - mTargetH - mParentPT - mParentPB - mTargetMT - mTargetMB;
            if (newTransY > transYUpperLimit) {
              yOffset = transYUpperLimit - oldTransY;
            }

            scrollByOffset(xOffset, yOffset);
            printViewInfo(event, xOffset, yOffset);
            break;
          case MotionEvent.ACTION_UP:
            scrollToSideWithTranslation();
            break;
        }
        return true;
      }
    });
  }

  /**
   * 通过使用 value animator 更新 layout params 的方法进行平滑
   */
  private void scrollToSideWithLayoutParams() {
    final int marginLeftMinLimit = mTargetML;
    final int marginLeftMaxLimit = mParentW - mParentPR - mTargetMR - mTargetW - mParentPL;
    final int marginLeftHalfLimit = (marginLeftMaxLimit + marginLeftMinLimit) / 2;
    final int marginLeft = ((FrameLayout.LayoutParams) mTarget.getLayoutParams()).leftMargin;
    if (marginLeft < marginLeftHalfLimit) {
      mLayoutParamsAnimator.setIntValues(marginLeft, marginLeftMinLimit);
      mLayoutParamsAnimator.setDuration(SPEED_A_PIXEL * (marginLeft - marginLeftMinLimit));
    } else {
      mLayoutParamsAnimator.setIntValues(marginLeft, marginLeftMaxLimit);
      mLayoutParamsAnimator.setDuration(SPEED_A_PIXEL * (marginLeftMaxLimit - marginLeft));
    }
    mLayoutParamsAnimator.start();
  }

  /**
   * 通过 Scroller 进行平滑
   */
  private void scrollToSideWithScrollTo() {
    final int scrollXLimit = mParentW - mParentPR - mTargetMR - mTargetW - mTargetML;
    final int scrollX = -mParent.getScrollX();
    final int scrollXHalfLimit = scrollXLimit / 2;

    if (scrollX < scrollXHalfLimit) {
      mScroller.startScroll(mParent.getScrollX(), mParent.getScrollY(), scrollX, 0,
          SPEED_A_PIXEL * scrollX);
    } else {
      mScroller.startScroll(mParent.getScrollX(), mParent.getScrollY(), -(scrollXLimit - scrollX),
          0, SPEED_A_PIXEL * (scrollXLimit - scrollX));
    }
    mParent.invalidate();
  }

  /**
   * 通过使用 thread 发送至 handler 不断调用 offset 或 scroll to 方法进行平滑
   */
  private void scrollToSideWithLayoutOrOffsetFun() {

    final int layoutXMaxLimit = mParentW - mParentPR - mTargetMR - mTargetW;
    final int layoutXMinLimit = mParentPL + mTargetML;
    final int layoutXHalfLimit = (layoutXMaxLimit + layoutXMinLimit) / 2;
    final int targetLeft = mTarget.getLeft();
    if (targetLeft == layoutXMinLimit || targetLeft == layoutXMaxLimit) {
      return;
    }
    mStartLayoutThread = true;
    if (targetLeft < layoutXHalfLimit) {
      final int transDistance = targetLeft - layoutXMinLimit;
      final int time = SPEED_A_PIXEL * transDistance;
      final int buffer = transDistance * 10 / time;
      mLayoutThread = new Thread(new Runnable() {
        @Override
        public void run() {
          int left = targetLeft;
          while (mStartLayoutThread) {
            Message msg = Message.obtain(mLayoutHandler);
            left -= buffer;
            if (left < layoutXMinLimit) {
              msg.arg1 = -(layoutXMinLimit - left);
              msg.sendToTarget();
              break;
            } else {
              msg.arg1 = -buffer;
              msg.sendToTarget();
            }
            if (left == layoutXMinLimit) {
              break;
            }
            try {
              Thread.sleep(10);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      });
    } else {
      final int transDistance = layoutXMaxLimit - targetLeft;
      final int time = SPEED_A_PIXEL * transDistance;
      final int buffer = transDistance * 10 / time;
      mLayoutThread = new Thread(new Runnable() {
        @Override
        public void run() {
          int left = targetLeft;
          while (mStartLayoutThread) {
            Message msg = Message.obtain(mLayoutHandler);
            left += buffer;
            if (left > layoutXMaxLimit) {
              msg.arg1 = layoutXMaxLimit - left;
              msg.sendToTarget();
              break;
            } else {
              msg.arg1 = buffer;
              msg.sendToTarget();
            }
            try {
              Thread.sleep(10);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      });
    }
    mLayoutThread.start();
  }

  /**
   * 通过 object animator 更新 "translationX" 属性进行平滑
   */
  private void scrollToSideWithTranslation() {
    final int transXLimit = mParentW - mParentPL - mParentPR - mTargetMR - mTargetML - mTargetW;
    final int transXHalfLimit = transXLimit / 2;
    final float translationX = mTarget.getTranslationX();
    if (translationX == 0 || translationX == transXLimit) {
      return;
    }
    if (translationX < transXHalfLimit) {
      mTransAnimator.setDuration((long) (SPEED_A_PIXEL * translationX));
      mTransAnimator.setFloatValues(translationX, 0);
    } else {
      mTransAnimator.setDuration((long) (SPEED_A_PIXEL * (transXLimit - translationX)));
      mTransAnimator.setFloatValues(translationX, transXLimit);
    }
    mTransAnimator.start();
  }

  /**
   * 停止所有可能执行的线程和动画
   */
  private void stopAllAnimAndThread() {
    if (mLayoutThread != null) {
      mStartLayoutThread = false;
      mLayoutThread = null;
    }
    if (mTransAnimator != null && mTransAnimator.isRunning()) {
      mTransAnimator.cancel();
    }
    if (mLayoutParamsAnimator != null && mLayoutParamsAnimator.isRunning()) {
      mLayoutParamsAnimator.cancel();
    }
  }

  public void recycle() {
    stopAllAnimAndThread();
  }

  /**
   * 处理不同的滑动方法
   *
   * @param xOffset x轴偏移
   * @param yOffset y轴偏移
   */
  private void scrollByOffset(int xOffset, int yOffset) {
    switch (mScrollMode) {
      case MODE_TRANSLATE:
        mTarget.setTranslationX(mTarget.getTranslationX() + xOffset);
        mTarget.setTranslationY(mTarget.getTranslationY() + yOffset);
        break;
      case MODE_SCROLL_TO:
        mParent.scrollBy(-xOffset, -yOffset);
        break;
      case MODE_OFFSET_FUN:
        mTarget.offsetLeftAndRight(xOffset);
        mTarget.offsetTopAndBottom(yOffset);
        break;
      case MODE_LAYOUT_FUN:
        mTarget.layout(mTarget.getLeft() + xOffset, mTarget.getTop() + yOffset,
            mTarget.getRight() + xOffset, mTarget.getBottom() + yOffset);
        break;
      case MODE_LAYOUT_PARAMS:
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTarget.getLayoutParams();
        params.leftMargin += xOffset;
        params.topMargin += yOffset;
        mTarget.setLayoutParams(params);
        break;
    }
  }

  /**
   * 在屏幕打印坐标信息
   *
   * @param event   触摸事件
   * @param xOffset x轴偏移
   * @param yOffset y轴偏移
   */
  private void printViewInfo(MotionEvent event, final int xOffset, final int yOffset) {
    T.show(
        getOutPutString(
            mParent.getScrollX(), mParent.getScrollY(),
            (int) event.getX(), (int) event.getY(), (int) event.getRawX(), (int) event.getRawY(),
            xOffset, yOffset,
            mTarget.getWidth(), mTarget.getHeight(),
            (int) mTarget.getX(), (int) mTarget.getY(),
            mTarget.getScrollX(), mTarget.getScrollY(),
            (int) mTarget.getTranslationX(), (int) mTarget.getTranslationY(),
            mTarget.getLeft(), mTarget.getRight(), mTarget.getTop(), mTarget.getBottom()
        )
    );
  }

  /**
   * 获取格式化的坐标信息字符串
   *
   * @param pScrollX      parent view getScrollX
   * @param pScrollY      parent view getScrollY
   * @param mGetX         motion event getX
   * @param mGetY         motion event getY
   * @param mGetRawX      motion event getRawX
   * @param mGetRawY      motion event getRawY
   * @param mOffsetX      x轴偏移
   * @param mOffsetY      y轴偏移
   * @param tWidth        target view width
   * @param tHeight       target view height
   * @param tGetX         target view getX
   * @param tGetY         target view getY
   * @param tScrollX      target view getScrollX
   * @param tScrollY      target view getScrollY
   * @param tTranslationX target view getTranslationX
   * @param tTranslationY target view getTranslationY
   * @param tLeft         target view getLeft
   * @param tRight        target view getRight
   * @param tTop          target view getTop
   * @param tBottom       target view getBottom
   * @return result string
   */
  private static String getOutPutString(int pScrollX, int pScrollY,
                                        int mGetX, int mGetY, int mGetRawX, int mGetRawY,
                                        int mOffsetX, int mOffsetY,
                                        int tWidth, int tHeight, int tGetX, int tGetY,
                                        int tScrollX, int tScrollY,
                                        int tTranslationX, int tTranslationY,
                                        int tLeft, int tRight, int tTop, int tBottom) {
    return "Parent: {" +
        "\nscrollX: " + pScrollX +
        "\nscrollY: " + pScrollY +
        "\n}\n" +
        "MotionEvent: {" +
        "\ngetX: " + mGetX +
        "\ngetY: " + mGetY +
        "\ngetRawX: " + mGetRawX +
        "\ngetRawY: " + mGetRawY +
        "\noffsetX: " + mOffsetX +
        "\noffsetY:" + mOffsetY +
        "\n}\n" +
        "TargetView: {" +
        "\nwidth: " + tWidth +
        "\nheight: " + tHeight +
        "\ngetX: " + tGetX +
        "\ngetY: " + tGetY +
        "\ngetScrollX: " + tScrollX +
        "\ngetScrollY: " + tScrollY +
        "\ngetTranslationX: " + tTranslationX +
        "\ngetTranslationY: " + tTranslationY +
        "\ngetLeft: " + tLeft +
        "\ngetRight: " + tRight +
        "\ngetTop: " + tTop +
        "\ngetBottom: " + tBottom +
        "\n}";
  }
}
