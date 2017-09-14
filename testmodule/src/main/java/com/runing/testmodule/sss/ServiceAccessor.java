package com.runing.testmodule.sss;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by DSI on 2017/9/14.
 */

public class ServiceAccessor implements UserRequest {

  private IBinder mRemote;

  public ServiceAccessor(IBinder mRemote) {
    this.mRemote = mRemote;
  }

  @Override public void setUser(User user) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel replay = Parcel.obtain();
    try {
      data.writeInterfaceToken(DESCRIPTOR);
      if (user == null) {
        data.writeInt(0);
      } else {
        data.writeInt(1);
        user.writeToParcel(data, 0);
      }
      mRemote.transact(TRANSACTION_SET_USER, data, replay, 0);
      replay.readException();
    } finally {
      data.recycle();
      replay.recycle();
    }
  }

  @Override public User getUser() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel replay = Parcel.obtain();
    final User user;
    try {
      data.writeInterfaceToken(DESCRIPTOR);
      mRemote.transact(TRANSACTION_GET_USER, data, replay, 0);
      if (replay.readInt() == 0) {
        user = null;
      } else {
        user = User.CREATOR.createFromParcel(replay);
      }
      replay.readException();
    } finally {
      data.recycle();
      replay.recycle();
    }
    return user;
  }

  @Override public IBinder asBinder() {
    return mRemote;
  }
}
