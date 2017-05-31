package com.runing.corrdinateandscroll;

import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.runing.coordinateandscroll.R;

import runing.com.publictools.view.T;

public class MainActivity extends AppCompatActivity {

  private ViewGroup mParent;
  private View mTarget;
  private ScrollerHelper mScrollerHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    T.init(this);

    findViews();
    initViews();
  }

  private void findViews() {
    mParent = findTViewById(R.id.fl_parent);
    mTarget = findTViewById(R.id.iv_target);
  }

  private void initViews() {
    mScrollerHelper = new ScrollerHelper();
    mScrollerHelper.setScrollMode(ScrollerHelper.MODE_TRANSLATE);
    mScrollerHelper.startScrollInParent(mParent, mTarget);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.cb_trans:
        mScrollerHelper.setScrollMode(ScrollerHelper.MODE_TRANSLATE);
        item.setChecked(true);
        break;
      case R.id.cb_scroll_to:
        mScrollerHelper.setScrollMode(ScrollerHelper.MODE_SCROLL_TO);
        item.setChecked(true);
        break;
      case R.id.cb_layout_fun:
        mScrollerHelper.setScrollMode(ScrollerHelper.MODE_LAYOUT_FUN);
        item.setChecked(true);
        break;
      case R.id.cb_offset_fun:
        mScrollerHelper.setScrollMode(ScrollerHelper.MODE_OFFSET_FUN);
        item.setChecked(true);
        break;
      case R.id.cb_layout_params:
        mScrollerHelper.setScrollMode(ScrollerHelper.MODE_LAYOUT_PARAMS);
        item.setChecked(true);
        break;
      case R.id.cb_request_layout:
        mTarget.requestLayout();
    }
    mScrollerHelper.startScrollInParent(mParent, mTarget);
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mScrollerHelper.recycle();
  }

  @SuppressWarnings("unchecked")
  private <V extends View> V findTViewById(int id) {
    return (V) findViewById(id);
  }
}