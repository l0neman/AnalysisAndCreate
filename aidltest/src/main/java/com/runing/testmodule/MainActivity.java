package com.runing.testmodule;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.runing.testmodule.bindertest.ServiceAccessor;
import com.runing.testmodule.bindertest.UserContract;
import com.runing.testmodule.bindertest.UserService;

public class MainActivity extends AppCompatActivity {

  public static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  private ServiceConnection mConn = new ServiceConnection() {
    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      UserContract serviceAccessor = ServiceAccessor.asInterface(iBinder);
      try {
        serviceAccessor.setUser(new User("runing", "boy", 22));
      } catch (RemoteException e) {
        e.printStackTrace();
      }
      try {
        User user = serviceAccessor.getUser();
        Log.d(TAG, "get user from remote -> \n" + user);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
    }
  };

  @Override protected void onStart() {
    super.onStart();
    bindService(new Intent(this, UserService.class), mConn, Context.BIND_AUTO_CREATE);
  }

  @Override protected void onStop() {
    super.onStop();
    unbindService(mConn);
  }
}
