package com.runing.publictools.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by DSI on 2017/5/22.
 */

public final class T {

  private static Toast sToast;

  @SuppressLint("ShowToast")
  public static void init(Context context) {
    sToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    TextView view = new TextView(context);
    view.setTextColor(Color.BLACK);
    view.setLayoutParams(
      new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
      )
    );
    sToast.setView(view);
  }

  public static void show(String text) {
    ((TextView)sToast.getView()).setText(text);
    sToast.setDuration(Toast.LENGTH_SHORT);
    sToast.show();
  }
}
