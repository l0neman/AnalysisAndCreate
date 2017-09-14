package com.runing.testmodule.bindertest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.runing.testmodule.User;

public class UserService extends Service {

  public static final String TAG = "UserService";

  @Override
  public IBinder onBind(Intent intent) {
    return new UserClientHandler() {
      @Override public void setUser(User user) throws RemoteException {
        Log.d(TAG, "user from client -> " + user);
      }

      @Override public User getUser() throws RemoteException {
        return new User("service", "sss", 1);
      }
    };
  }

}
