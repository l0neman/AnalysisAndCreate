package com.runing.testmodule.sss;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

/**
 * Created by DSI on 2017/9/14.
 */

public interface UserRequest extends IInterface {

  String DESCRIPTOR = "UserRequest";

  int TRANSACTION_SET_USER = IBinder.FIRST_CALL_TRANSACTION;
  int TRANSACTION_GET_USER = IBinder.FIRST_CALL_TRANSACTION + 1;

  void setUser(User user) throws RemoteException;

  User getUser() throws RemoteException;
}
