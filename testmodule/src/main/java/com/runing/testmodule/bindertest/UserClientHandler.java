package com.runing.testmodule.bindertest;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.runing.testmodule.User;

/**
 * Created by WangRuning on 2017/9/14.
 */

public abstract class UserClientHandler extends Binder implements UserContract {

  public UserClientHandler() {
    attachInterface(this, DESCRIPTOR);
  }

  @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch (code) {
    case INTERFACE_TRANSACTION:
      reply.writeString(DESCRIPTOR);
      break;
    case TRANSACTION_SET_USER: {
      data.enforceInterface(DESCRIPTOR);
      final User user;
      if (data.readInt() == 0) {
        user = null;
      } else {
        user = User.CREATOR.createFromParcel(data);
      }
      setUser(user);
      reply.readException();
      return true;
    }
    case TRANSACTION_GET_USER: {
      data.enforceInterface(DESCRIPTOR);
      User user = getUser();
      if (user == null) {
        reply.writeInt(0);
      } else {
        reply.writeInt(1);
        user.writeToParcel(reply, 0);
      }
      reply.readException();
      return true;
    }
    }
    return super.onTransact(code, data, reply, flags);
  }

  @Override public IBinder asBinder() {
    return this;
  }
}
