package com.runing.testmodule.bindertest;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.runing.testmodule.User;

/**
 * Created by DSI on 2017/9/14.
 */

public interface UserContract extends IInterface {

  String DESCRIPTOR = "UserContract";

  int TRANSACTION_SET_USER = IBinder.FIRST_CALL_TRANSACTION;
  int TRANSACTION_GET_USER = IBinder.FIRST_CALL_TRANSACTION + 1;

  void setUser(User user) throws RemoteException;

  User getUser() throws RemoteException;
}
