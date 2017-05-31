package com.runing.corrdinateandscroll;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by DSI on 2017/5/23.
 */

public final class ScrollerLayout extends FrameLayout {

  public ScrollerLayout(Context context) {
    super(context);
  }

  public ScrollerLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ScrollerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private OnComputeScrollListener mOnComputeScrollListener;

  public void setOnComputeScrollListener(OnComputeScrollListener onComputeScrollListener) {
    this.mOnComputeScrollListener = onComputeScrollListener;
  }

  public interface OnComputeScrollListener {
    void onComputeScroll();
  }

  @Override
  public void computeScroll() {
    if (mOnComputeScrollListener != null) {
      mOnComputeScrollListener.onComputeScroll();
    }
  }
}
