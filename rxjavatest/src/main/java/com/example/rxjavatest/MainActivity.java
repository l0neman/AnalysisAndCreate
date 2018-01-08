package com.example.rxjavatest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.runing.utilslib.debug.log.L;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    L.t("Main").d();

  }

}
